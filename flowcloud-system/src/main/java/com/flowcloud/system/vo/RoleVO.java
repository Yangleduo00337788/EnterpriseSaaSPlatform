package com.flowcloud.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {

    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private String dataScope;
    private Integer sort;
    private Integer status;
    private List<Long> permissionIds;
    private LocalDateTime createTime;
}
