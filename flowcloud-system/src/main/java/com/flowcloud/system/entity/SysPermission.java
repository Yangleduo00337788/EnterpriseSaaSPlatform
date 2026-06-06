package com.flowcloud.system.entity;

import com.flowcloud.common.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_permission")
public class SysPermission extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long parentId;
    private String permCode;
    private String permName;
    private String permType;
    private String path;
    private String icon;
    private Integer sort;
    private Integer status;
}
