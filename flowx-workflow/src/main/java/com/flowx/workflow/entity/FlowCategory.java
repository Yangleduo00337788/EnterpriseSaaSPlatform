package com.flowx.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("flow_category")
public class FlowCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Category name
     */
    @Column("category_name")
    private String categoryName;

    /**
     * Category code (unique)
     */
    @Column("category_code")
    private String categoryCode;

    /**
     * Sort order
     */
    @Column("sort")
    private Integer sort;

    /**
     * Icon
     */
    @Column("icon")
    private String icon;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;
}
