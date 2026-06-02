package com.flowx.auth.service;

import com.flowx.auth.dto.*;
import java.util.List;
import java.util.Map;

public interface AuthService {
    TokenVO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
    TokenVO refreshToken(String refreshToken);
    void logout();
    void changePassword(ChangePasswordDTO changePasswordDTO);
    void resetPassword(PasswordResetDTO passwordResetDTO);
    CaptchaVO getCaptcha();
    Map<String, Object> getUserInfo();
    List<Map<String, Object>> getRouters();
}