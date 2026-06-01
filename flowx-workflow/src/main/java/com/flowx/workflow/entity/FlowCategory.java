package com.flowx.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Flow category entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_category")
public class FlowCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Category name
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * Category code (unique)
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * Sort order
     */
    @TableField("sort")
    private Integer sort;

    /**
     * Icon
     */
    @TableField("icon")
    private String icon;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;
}
