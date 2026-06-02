package com.flowx.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Dictionary data entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_dict_data")
public class SysDictData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Dictionary type
     */
    @Column("dict_type")
    private String dictType;

    /**
     * Dictionary label
     */
    @Column("dict_label")
    private String dictLabel;

    /**
     * Dictionary value
     */
    @Column("dict_value")
    private String dictValue;

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

    /**
     * CSS class
     */
    @Column("css_class")
    private String cssClass;

    /**
     * List class (tag type for front-end)
     */
    @Column("list_class")
    private String listClass;
}