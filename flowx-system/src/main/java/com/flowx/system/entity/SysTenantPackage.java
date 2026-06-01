package com.flowx.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tenant package entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_package")
public class SysTenantPackage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Package name
     */
    @TableField("package_name")
    private String packageName;

    /**
     * Menu IDs (JSON array string)
     */
    @TableField("menu_ids")
    private String menuIds;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Remark
     */
    @TableField("remark")
    private String remark;
}
