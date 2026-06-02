package com.flowx.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * User create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class UserDTO implements Serializable {

    /**
     * Primary key ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Username
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 30, message = "用户名长度必须在2-30个字符之间")
    private String username;

    /**
     * Password
     */
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    /**
     * Nickname
     */
    @Size(max = 30, message = "昵称长度不能超过30个字符")
    private String nickname;

    /**
     * Email
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * Phone
     */
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    /**
     * Gender (0=unknown, 1=male, 2=female)
     */
    private Integer gender;

    /**
     * Avatar URL
     */
    private String avatar;

    /**
     * Department ID
     */
    private Long deptId;

    /**
     * Position ID
     */
    private Long positionId;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Assigned role IDs
     */
    private List<Long> roleIds;
}
