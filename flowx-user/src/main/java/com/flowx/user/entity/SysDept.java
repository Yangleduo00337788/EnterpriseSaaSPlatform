package com.flowx.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * System department entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Department name
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * Parent department ID (0 for root)
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * Sort order
     */
    @TableField("sort")
    private Integer sort;

    /**
     * Department leader
     */
    @TableField("leader")
    private String leader;

    /**
     * Contact phone
     */
    @TableField("phone")
    private String phone;

    /**
     * Contact email
     */
    @TableField("email")
    private String email;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Display order number
     */
    @TableField("order_num")
    private Integer orderNum;
}
