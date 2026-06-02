package com.flowx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * User registration request DTO
 *
 * @author FlowX Team
 */
@Data
public class RegisterDTO {

    private Long id;

    /**
     * Username
     */
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    /**
     * Password
     */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Email
     */
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Phone number
     */
    private String phone;

    /**
     * Tenant name
     */
    private String tenantName;
}
