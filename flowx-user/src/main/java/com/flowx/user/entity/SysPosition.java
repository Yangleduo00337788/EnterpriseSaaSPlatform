package com.flowx.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * System position entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_position")
public class SysPosition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Position name
     */
    @Column("position_name")
    private String positionName;

    /**
     * Position code (unique identifier)
     */
    @Column("position_code")
    private String positionCode;

    /**
     * Sort order
     */
    @Column("sort")
    private Integer sort;

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
