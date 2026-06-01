package com.flowx.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Dictionary type
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * Dictionary label
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * Dictionary value
     */
    @TableField("dict_value")
    private String dictValue;

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

    /**
     * CSS class
     */
    @TableField("css_class")
    private String cssClass;

    /**
     * List class (tag type for front-end)
     */
    @TableField("list_class")
    private String listClass;
}
