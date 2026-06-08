package com.flowcloud.system.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LoginVO {

    private String token;
    private Long userId;
    private Long tenantId;
    private String tenantCode;
    private Long deptId;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private String jobTitle;
    private String tenantName;
    private String logo;
    private String themeColor;
    private String dataScope;
    private Set<String> roles;
    private Set<String> permissions;
    private List<String> enabledFeatures;
}
