package com.flowx.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("sys_tenant_package")
public class SysTenantPackage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Package name
     */
    @Column("package_name")
    private String packageName;

    /**
     * Menu IDs (JSON array string)
     */
    @Column("menu_ids")
    private String menuIds;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Remark
     */
    @Column("remark")
    private String remark;
}