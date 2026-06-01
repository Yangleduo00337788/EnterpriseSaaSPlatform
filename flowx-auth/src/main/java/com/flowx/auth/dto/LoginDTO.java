package com.flowx.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login request DTO
 *
 * @author FlowX Team
 */
@Data
public class LoginDTO {

    /**
     * Username
     */
    @NotBlank(message = "Username cannot be empty")
    private String username;

    /**
     * Password
     */
    @NotBlank(message = "Password cannot be empty")
    private String password;

    /**
     * Captcha code
     */
    @NotBlank(message = "Captcha code cannot be empty")
    private String captchaCode;

    /**
     * Captcha key for verification
     */
    @NotBlank(message = "Captcha key cannot be empty")
    private String captchaKey;
}
