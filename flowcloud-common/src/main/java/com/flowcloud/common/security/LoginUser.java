package com.flowcloud.common.security;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long tenantId;
    private String username;
    private String realName;
    private String avatar;
    private Set<String> permissions;
    private Set<String> roles;
}
