package com.flowx.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_position")
public class SysPosition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Position name
     */
    @TableField("position_name")
    private String positionName;

    /**
     * Position code (unique identifier)
     */
    @TableField("position_code")
    private String positionCode;

    /**
     * Sort order
     */
    @TableField("sort")
    private Integer sort;

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
