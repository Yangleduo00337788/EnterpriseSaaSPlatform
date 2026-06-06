package com.flowcloud.system.entity;

import com.flowcloud.common.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
public class SysUser extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Long deptId;
    private Long managerId;
    private String jobTitle;
    private String workStatus;
    private Integer status;
    private Integer isAdmin;
}
