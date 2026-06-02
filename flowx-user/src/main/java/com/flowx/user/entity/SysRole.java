package com.flowx.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * System role entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Role name
     */
    @Column("role_name")
    private String roleName;

    /**
     * Role key (unique identifier)
     */
    @Column("role_key")
    private String roleKey;

    /**
     * Sort order
     */
    @Column("sort")
    private Integer sort;

    /**
     * Data scope (1=all, 2=custom, 3=dept, 4=dept and below, 5=self)
     */
    @Column("data_scope")
    private Integer dataScope;

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
