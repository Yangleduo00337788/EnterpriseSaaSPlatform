package com.flowcloud.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "企业名称不能为空")
    private String tenantName;

    @NotBlank(message = "企业编码不能为空")
    @Size(min = 2, max = 32, message = "企业编码长度2-32位")
    private String tenantCode;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "手机号不能为空")
    private String contactPhone;

    @Email(message = "邮箱格式不正确")
    private String contactEmail;

    @NotBlank(message = "管理员用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度6-32位")
    private String password;

    @NotBlank(message = "管理员姓名不能为空")
    private String realName;
}
