package com.flowx.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Department name
     */
    @Column("dept_name")
    private String deptName;

    /**
     * Parent department ID (0 for root)
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * Sort order
     */
    @Column("sort")
    private Integer sort;

    /**
     * Department leader
     */
    @Column("leader")
    private String leader;

    /**
     * Contact phone
     */
    @Column("phone")
    private String phone;

    /**
     * Contact email
     */
    @Column("email")
    private String email;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Display order number
     */
    @Column("order_num")
    private Integer orderNum;
}
