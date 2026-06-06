package com.flowcloud.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String email;
    private String phone;
    private Long deptId;
    private Long managerId;
    private String jobTitle;
    private String workStatus;
    private Integer status;
    private List<Long> roleIds;
}
