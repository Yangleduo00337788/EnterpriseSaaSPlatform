package com.flowcloud.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("sys_user_role")
public class SysUserRole {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;
    private Long roleId;
}
