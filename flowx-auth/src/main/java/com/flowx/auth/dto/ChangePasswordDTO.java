package com.flowx.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Change password request DTO
 *
 * @author FlowX Team
 */
@Data
public class ChangePasswordDTO {

    /**
     * Old password
     */
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank(message = "New password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String newPassword;
}
