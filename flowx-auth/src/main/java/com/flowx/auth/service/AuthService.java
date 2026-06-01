package com.flowx.auth.service;

import com.flowx.auth.dto.CaptchaVO;
import com.flowx.auth.dto.ChangePasswordDTO;
import com.flowx.auth.dto.LoginDTO;
import com.flowx.auth.dto.PasswordResetDTO;
import com.flowx.auth.dto.RegisterDTO;
import com.flowx.auth.dto.TokenVO;

/**
 * Authentication service interface
 *
 * @author FlowX Team
 */
public interface AuthService {

    /**
     * User login
     *
     * @param loginDTO login request
     * @return token response
     */
    TokenVO login(LoginDTO loginDTO);

    /**
     * User registration
     *
     * @param registerDTO registration request
     */
    void register(RegisterDTO registerDTO);

    /**
     * Refresh access token
     *
     * @param refreshToken refresh token
     * @return new token response
     */
    TokenVO refreshToken(String refreshToken);

    /**
     * User logout
     */
    void logout();

    /**
     * Change password
     *
     * @param changePasswordDTO change password request
     */
    void changePassword(ChangePasswordDTO changePasswordDTO);

    /**
     * Reset password
     *
     * @param passwordResetDTO password reset request
     */
    void resetPassword(PasswordResetDTO passwordResetDTO);

    /**
     * Get captcha
     *
     * @return captcha response
     */
    CaptchaVO getCaptcha();
}
