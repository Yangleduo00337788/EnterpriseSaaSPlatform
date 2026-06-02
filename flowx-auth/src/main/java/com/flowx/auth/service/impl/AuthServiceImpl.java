package com.flowx.auth.service.impl;

import com.flowx.auth.dto.CaptchaVO;
import com.flowx.auth.dto.ChangePasswordDTO;
import com.flowx.auth.dto.LoginDTO;
import com.flowx.auth.dto.PasswordResetDTO;
import com.flowx.auth.dto.RegisterDTO;
import com.flowx.auth.dto.TokenVO;
import com.flowx.auth.service.AuthService;
import com.flowx.auth.util.JwtUtil;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.base.SecurityUser;
import com.flowx.infrastructure.redis.RedisService;
import com.flowx.user.entity.SysMenu;
import com.flowx.user.entity.SysUser;
import com.flowx.user.mapper.SysMenuMapper;
import com.flowx.user.mapper.SysUserMapper;
import com.wf.captcha.SpecCaptcha;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    @Lazy
    private final SysUserMapper sysUserMapper;
    @Lazy
    private final SysMenuMapper sysMenuMapper;

    @Value("${flowx.captcha.expire-minutes:5}")
    private int captchaExpireMinutes;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String RESET_PASSWORD_PREFIX = "reset_password:";

    @Override
    public TokenVO login(LoginDTO loginDTO) {
        String captchaKey = CAPTCHA_KEY_PREFIX + loginDTO.getCaptchaKey();
        String cachedCaptcha = redisService.getString(captchaKey);
        if (cachedCaptcha == null) {
            throw new BizException(ResultCodeEnum.CAPTCHA_EXPIRED);
        }
        if (!cachedCaptcha.equalsIgnoreCase(loginDTO.getCaptchaCode())) {
            throw new BizException(ResultCodeEnum.CAPTCHA_ERROR);
        }
        redisService.delete(captchaKey);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginDTO.getUsername(), e);
            throw new BizException(ResultCodeEnum.LOGIN_FAILED);
        }

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(securityUser);
        String refreshToken = jwtUtil.generateRefreshToken(securityUser);

        long accessTokenExpireSeconds = jwtUtil.parseToken(accessToken).getExpiration().getTime() / 1000;
        long refreshTokenExpireSeconds = jwtUtil.parseToken(refreshToken).getExpiration().getTime() / 1000;

        String accessTokenKey = ACCESS_TOKEN_PREFIX + securityUser.getUserId() + ":" + securityUser.getTenantId();
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + securityUser.getUserId() + ":" + securityUser.getTenantId();

        redisService.set(accessTokenKey, accessToken, accessTokenExpireSeconds, TimeUnit.SECONDS);
        redisService.set(refreshTokenKey, refreshToken, refreshTokenExpireSeconds, TimeUnit.SECONDS);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User logged in successfully: {}", loginDTO.getUsername());

        return TokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpireSeconds)
                .build();
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        throw new BizException(ResultCodeEnum.NOT_IMPLEMENTED);
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            redisService.delete(ACCESS_TOKEN_PREFIX + securityUser.getUserId() + ":" + securityUser.getTenantId());
            redisService.delete(REFRESH_TOKEN_PREFIX + securityUser.getUserId() + ":" + securityUser.getTenantId());
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public TokenVO refreshToken(String refreshToken) {
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        if (username == null || jwtUtil.isTokenExpired(refreshToken)) {
            throw new BizException(ResultCodeEnum.TOKEN_INVALID);
        }
        throw new BizException(ResultCodeEnum.NOT_IMPLEMENTED);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new BizException(ResultCodeEnum.UNAUTHORIZED);
        }
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        logout();
        log.info("Password changed for user: {}", securityUser.getUsername());
    }

    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        if (passwordResetDTO.getCaptchaCode() != null && passwordResetDTO.getCaptchaKey() != null) {
            String captchaKey = CAPTCHA_KEY_PREFIX + passwordResetDTO.getCaptchaKey();
            String cachedCaptcha = redisService.getString(captchaKey);
            if (cachedCaptcha == null) {
                throw new BizException(ResultCodeEnum.CAPTCHA_EXPIRED);
            }
            if (!cachedCaptcha.equalsIgnoreCase(passwordResetDTO.getCaptchaCode())) {
                throw new BizException(ResultCodeEnum.CAPTCHA_ERROR);
            }
            redisService.delete(captchaKey);
        }
        String resetToken = UUID.randomUUID().toString();
        redisService.set(RESET_PASSWORD_PREFIX + resetToken, passwordResetDTO.getEmail(), 30, TimeUnit.MINUTES);
        try {
            sendResetEmail(passwordResetDTO.getEmail(), resetToken);
        } catch (MessagingException e) {
            log.error("Failed to send reset email to: {}", passwordResetDTO.getEmail(), e);
            throw new BizException(ResultCodeEnum.EMAIL_SEND_FAILED);
        }
        log.info("Password reset email sent to: {}", passwordResetDTO.getEmail());
    }

    @Override
    public CaptchaVO getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String captchaKey = UUID.randomUUID().toString();
        String captchaCode = specCaptcha.text().toUpperCase();
        String captchaImage = specCaptcha.toBase64();

        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisService.setString(redisKey, captchaCode, captchaExpireMinutes, TimeUnit.MINUTES);
        log.info("Captcha generated for key: {}, code: {}", captchaKey, captchaCode);

        return CaptchaVO.builder()
                .captchaKey(captchaKey)
                .captchaImage(captchaImage)
                .expireTime(System.currentTimeMillis() + captchaExpireMinutes * 60 * 1000L)
                .build();
    }

    @Override
    public Map<String, Object> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new BizException(ResultCodeEnum.UNAUTHORIZED);
        }
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // Query fresh user data from DB
        SysUser user = sysUserMapper.selectUserByUsername(securityUser.getUsername());
        Map<String, Object> userMap = new HashMap<>();
        if (user != null) {
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("nickname", user.getNickname());
            userMap.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
            userMap.put("email", user.getEmail() != null ? user.getEmail() : "");
            userMap.put("phone", user.getPhone() != null ? user.getPhone() : "");
            userMap.put("sex", user.getGender() != null ? user.getGender() : 0);
            userMap.put("deptId", user.getDeptId());
            userMap.put("deptName", "");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", userMap);
        result.put("roles", securityUser.getRoles());
        result.put("permissions", securityUser.getPermissions());
        return result;
    }

    @Override
    public List<Map<String, Object>> getRouters() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new BizException(ResultCodeEnum.UNAUTHORIZED);
        }
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        List<SysMenu> menus;
        if (securityUser.getRoles().contains("admin")) {
            // Admin gets all menus
            menus = sysMenuMapper.selectMenuTree();
        } else {
            menus = sysMenuMapper.selectMenusByUserId(securityUser.getUserId());
        }

        // Filter only directory and menu types, not buttons
        menus = menus.stream()
                .filter(m -> m.getMenuType() != null && m.getMenuType() != 2)
                .collect(Collectors.toList());

        return buildMenuTree(menus, 0L);
    }

    private List<Map<String, Object>> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("name", menu.getMenuName());
                node.put("path", menu.getPath() != null ? menu.getPath() : "");
                node.put("component", menu.getComponent() != null ? menu.getComponent() : "");
                node.put("icon", menu.getIcon() != null ? menu.getIcon() : "");
                node.put("title", menu.getMenuName());

                Map<String, Object> meta = new LinkedHashMap<>();
                meta.put("title", menu.getMenuName());
                meta.put("icon", menu.getIcon() != null ? menu.getIcon() : "");
                node.put("meta", meta);

                List<Map<String, Object>> children = buildMenuTree(menus, menu.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                tree.add(node);
            }
        }
        return tree;
    }

    private void sendResetEmail(String email, String resetToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailFrom);
        helper.setTo(email);
        helper.setSubject("FlowX - Password Reset");
        String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;
        helper.setText(buildResetEmailContent(resetUrl), true);
        mailSender.send(message);
    }

    private String buildResetEmailContent(String resetUrl) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body>"
                + "<div style='max-width:600px;margin:0 auto;padding:20px;'>"
                + "<h2>FlowX Password Reset</h2>"
                + "<p>Click to reset: <a href='" + resetUrl + "'>Reset Password</a></p>"
                + "<p>This link expires in 30 minutes.</p>"
                + "</div></body></html>";
    }
}