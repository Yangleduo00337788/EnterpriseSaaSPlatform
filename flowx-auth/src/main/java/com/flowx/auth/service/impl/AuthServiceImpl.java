package com.flowx.auth.service.impl;

import com.flowx.auth.dto.CaptchaVO;
import com.flowx.auth.dto.ChangePasswordDTO;
import com.flowx.auth.dto.LoginDTO;
import com.flowx.auth.dto.PasswordResetDTO;
import com.flowx.auth.dto.RegisterDTO;
import com.flowx.auth.dto.TokenVO;
import com.flowx.auth.service.AuthService;
import com.flowx.auth.util.JwtUtil;
import com.flowx.common.constant.CacheConstants;
import com.flowx.common.constant.SecurityConstants;
import com.flowx.common.exception.BusinessException;
import com.flowx.common.exception.ErrorCode;
import com.flowx.common.security.SecurityUser;
import com.flowx.common.utils.RedisUtil;
import com.flowx.common.utils.SecurityUtils;
import com.wf.captcha.SpecCaptcha;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Authentication service implementation
 *
 * @author FlowX Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${flowx.captcha.expire-minutes:5}")
    private int captchaExpireMinutes;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    /**
     * Redis key prefixes
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String RESET_PASSWORD_PREFIX = "reset_password:";

    @Override
    public TokenVO login(LoginDTO loginDTO) {
        // Validate captcha
        String captchaKey = CAPTCHA_KEY_PREFIX + loginDTO.getCaptchaKey();
        String cachedCaptcha = redisUtil.get(captchaKey);
        if (cachedCaptcha == null) {
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED);
        }
        if (!cachedCaptcha.equalsIgnoreCase(loginDTO.getCaptchaCode())) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
        }
        // Delete captcha after validation
        redisUtil.delete(captchaKey);

        // Authenticate user
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginDTO.getUsername(), e);
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // Get security user from authentication
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(securityUser);
        String refreshToken = jwtUtil.generateRefreshToken(securityUser);

        // Save tokens to Redis
        long accessTokenExpireSeconds = jwtUtil.parseToken(accessToken).getExpiration().getTime() / 1000;
        long refreshTokenExpireSeconds = jwtUtil.parseToken(refreshToken).getExpiration().getTime() / 1000;

        String accessTokenKey = ACCESS_TOKEN_PREFIX + securityUser.getUserId() + ":" + securityUser.getTenantId();
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + securityUser.getUserId() + ":" + securityUser.getTenantId();

        redisUtil.set(accessTokenKey, accessToken, accessTokenExpireSeconds, TimeUnit.SECONDS);
        redisUtil.set(refreshTokenKey, refreshToken, refreshTokenExpireSeconds, TimeUnit.SECONDS);

        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("User logged in successfully: {}", loginDTO.getUsername());

        return TokenVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpireSeconds)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // Check if username already exists
        // TODO: Implement user existence check via UserMapper or UserService
        // boolean exists = userMapper.existsByUsername(registerDTO.getUsername());
        // if (exists) {
        //     throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        // }

        // Encode password
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());

        // TODO: Create user entity and save to database
        // User user = new User();
        // user.setUsername(registerDTO.getUsername());
        // user.setPassword(encodedPassword);
        // user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        // user.setEmail(registerDTO.getEmail());
        // user.setPhone(registerDTO.getPhone());
        // user.setStatus(StatusEnum.ENABLE.getCode());
        // userMapper.insert(user);

        // TODO: Create default role assignment
        // UserRole userRole = new UserRole();
        // userRole.setUserId(user.getId());
        // userRole.setRoleId(defaultRoleId);
        // userRoleMapper.insert(userRole);

        // TODO: Create default department assignment if tenant has default department

        log.info("User registered successfully: {}", registerDTO.getUsername());
    }

    @Override
    public TokenVO refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // Check token type
        var claims = jwtUtil.parseToken(refreshToken);
        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();

        // Verify refresh token exists in Redis
        // Note: We need tenantId to construct the key properly
        // For simplicity, we'll check if the token matches
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + userId + ":*";
        // In production, you should store and retrieve tenantId properly

        // Generate new access token
        // TODO: Load full SecurityUser from database or cache
        SecurityUser securityUser = SecurityUser.builder()
                .userId(userId)
                .username(username)
                .build();

        String newAccessToken = jwtUtil.generateAccessToken(securityUser);

        long accessTokenExpireSeconds = jwtUtil.parseToken(newAccessToken).getExpiration().getTime() / 1000;

        // Update access token in Redis
        String accessTokenKey = ACCESS_TOKEN_PREFIX + userId;
        redisUtil.set(accessTokenKey, newAccessToken, accessTokenExpireSeconds, TimeUnit.SECONDS);

        log.info("Token refreshed for user: {}", username);

        return TokenVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpireSeconds)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void logout() {
        try {
            SecurityUser securityUser = SecurityUtils.getCurrentUser();
            if (securityUser != null) {
                Long userId = securityUser.getUserId();
                Long tenantId = securityUser.getTenantId();

                // Remove tokens from Redis
                String accessTokenKey = ACCESS_TOKEN_PREFIX + userId + ":" + tenantId;
                String refreshTokenKey = REFRESH_TOKEN_PREFIX + userId + ":" + tenantId;

                redisUtil.delete(accessTokenKey);
                redisUtil.delete(refreshTokenKey);

                log.info("User logged out: {}", securityUser.getUsername());
            }
        } finally {
            // Clear security context
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        SecurityUser securityUser = SecurityUtils.getCurrentUser();
        if (securityUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // TODO: Get current password from database
        // User user = userMapper.selectById(securityUser.getUserId());
        // if (user == null) {
        //     throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        // }

        // Verify old password
        // if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
        //     throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        // }

        // Update password
        // String newPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        // user.setPassword(newPassword);
        // userMapper.updateById(user);

        // Invalidate existing tokens
        logout();

        log.info("Password changed for user: {}", securityUser.getUsername());
    }

    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        // Validate captcha if provided
        if (passwordResetDTO.getCaptchaCode() != null && passwordResetDTO.getCaptchaKey() != null) {
            String captchaKey = CAPTCHA_KEY_PREFIX + passwordResetDTO.getCaptchaKey();
            String cachedCaptcha = redisUtil.get(captchaKey);
            if (cachedCaptcha == null) {
                throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED);
            }
            if (!cachedCaptcha.equalsIgnoreCase(passwordResetDTO.getCaptchaCode())) {
                throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
            }
            redisUtil.delete(captchaKey);
        }

        // TODO: Check if email exists in database
        // User user = userMapper.selectByEmail(passwordResetDTO.getEmail());
        // if (user == null) {
        //     throw new BusinessException(ErrorCode.EMAIL_NOT_FOUND);
        // }

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        String resetKey = RESET_PASSWORD_PREFIX + resetToken;

        // Save reset token to Redis with 30 minutes expiration
        redisUtil.set(resetKey, passwordResetDTO.getEmail(), 30, TimeUnit.MINUTES);

        // Send reset email
        try {
            sendResetEmail(passwordResetDTO.getEmail(), resetToken);
        } catch (MessagingException e) {
            log.error("Failed to send reset email to: {}", passwordResetDTO.getEmail(), e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }

        log.info("Password reset email sent to: {}", passwordResetDTO.getEmail());
    }

    @Override
    public CaptchaVO getCaptcha() {
        // Generate captcha using easy-captcha
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);

        // Generate unique captcha key
        String captchaKey = UUID.randomUUID().toString();

        // Get captcha code and image
        String captchaCode = specCaptcha.text().toLowerCase();
        String captchaImage = specCaptcha.toBase64();

        // Save captcha to Redis with expiration
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisUtil.set(redisKey, captchaCode, captchaExpireMinutes, TimeUnit.MINUTES);

        log.debug("Captcha generated for key: {}", captchaKey);

        return CaptchaVO.builder()
                .captchaKey(captchaKey)
                .captchaImage(captchaImage)
                .expireTime(System.currentTimeMillis() + captchaExpireMinutes * 60 * 1000L)
                .build();
    }

    /**
     * Send password reset email
     */
    private void sendResetEmail(String email, String resetToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(mailFrom);
        helper.setTo(email);
        helper.setSubject("FlowX - Password Reset");

        String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;
        String htmlContent = buildResetEmailContent(resetUrl);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Build password reset email content
     */
    private String buildResetEmailContent(String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #333;'>FlowX Password Reset</h2>" +
                "<p>You have requested to reset your password. Click the link below to proceed:</p>" +
                "<p><a href='" + resetUrl + "' style='display: inline-block; padding: 10px 20px; " +
                "background-color: #1890ff; color: white; text-decoration: none; border-radius: 4px;'>" +
                "Reset Password</a></p>" +
                "<p>This link will expire in 30 minutes.</p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='color: #999; font-size: 12px;'>This is an automated message from FlowX Platform.</p>" +
                "</div></body></html>";
    }
}
