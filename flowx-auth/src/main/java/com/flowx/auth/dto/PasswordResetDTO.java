package com.flowx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Password reset request DTO
 *
 * @author FlowX Team
 */
@Data
public class PasswordResetDTO {

    private Long id;

    /**
     * Email address
     */
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Captcha code
     */
    private String captchaCode;

    /**
     * Captcha key for verification
     */
    private String captchaKey;
}
