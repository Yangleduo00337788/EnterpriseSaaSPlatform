package com.flowx.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Dictionary type entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Dictionary name
     */
    @TableField("dict_name")
    private String dictName;

    /**
     * Dictionary type (unique)
     */
    @TableField("dict_type")
    private String dictType;

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
