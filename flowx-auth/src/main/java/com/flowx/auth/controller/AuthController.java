package com.flowx.auth.controller;

import com.flowx.auth.dto.CaptchaVO;
import com.flowx.auth.dto.LoginDTO;
import com.flowx.auth.dto.RegisterDTO;
import com.flowx.auth.dto.TokenVO;
import com.flowx.auth.service.AuthService;
import com.flowx.common.core.result.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/captcha")
    public R<Map<String, Object>> captcha() {
        CaptchaVO captcha = authService.getCaptcha();
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", captcha.getCaptchaKey());
        data.put("img", captcha.getCaptchaImage());
        return R.ok(data);
    }

    @PostMapping("/login")
    public R<TokenVO> login(@RequestBody LoginDTO loginDTO) {
        return R.ok(authService.login(loginDTO));
    }

    @PostMapping("/register")
    public R<Void> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return R.ok();
    }

    @PostMapping("/refresh-token")
    public R<TokenVO> refreshToken(@RequestParam String refreshToken) {
        return R.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    @GetMapping("/user-info")
    public R<Map<String, Object>> getUserInfo() {
        return R.ok(authService.getUserInfo());
    }

    @GetMapping("/routers")
    public R<List<Map<String, Object>>> getRouters() {
        return R.ok(authService.getRouters());
    }
}