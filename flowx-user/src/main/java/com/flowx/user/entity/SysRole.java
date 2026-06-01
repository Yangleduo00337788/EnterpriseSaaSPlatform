package com.flowx.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Role name
     */
    @TableField("role_name")
    private String roleName;

    /**
     * Role key (unique identifier)
     */
    @TableField("role_key")
    private String roleKey;

    /**
     * Sort order
     */
    @TableField("sort")
    private Integer sort;

    /**
     * Data scope (1=all, 2=custom, 3=dept, 4=dept and below, 5=self)
     */
    @TableField("data_scope")
    private Integer dataScope;

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
