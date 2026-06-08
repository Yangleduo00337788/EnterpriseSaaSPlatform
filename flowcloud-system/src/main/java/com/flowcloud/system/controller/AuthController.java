package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.ChangePasswordDTO;
import com.flowcloud.system.dto.LoginDTO;
import com.flowcloud.system.dto.ProfileUpdateDTO;
import com.flowcloud.system.dto.RegisterDTO;
import com.flowcloud.system.service.AuthService;
import com.flowcloud.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "企业注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.ok();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<LoginVO> me() {
        return Result.ok(authService.getCurrentUser());
    }

    @Operation(summary = "更新当前用户资料")
    @PutMapping("/profile")
    public Result<LoginVO> updateProfile(@Valid @RequestBody ProfileUpdateDTO dto) {
        return Result.ok(authService.updateCurrentProfile(dto));
    }

    @Operation(summary = "上传当前用户头像")
    @PostMapping("/profile/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.ok(Map.of("avatarUrl", authService.uploadCurrentUserAvatar(file)));
    }

    @Operation(summary = "修改当前用户密码")
    @PutMapping("/profile/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        authService.changeCurrentPassword(dto);
        return Result.ok();
    }
}
