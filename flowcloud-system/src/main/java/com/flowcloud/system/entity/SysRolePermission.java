package com.flowcloud.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("sys_role_permission")
public class SysRolePermission {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long roleId;
    private Long permissionId;
}
