package com.flowx.approval.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Approval type entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_type")
public class ApprovalType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Type name
     */
    @TableField("type_name")
    private String typeName;

    /**
     * Type code (unique)
     */
    @TableField("type_code")
    private String typeCode;

    /**
     * Icon
     */
    @TableField("icon")
    private String icon;

    /**
     * Associated flow key
     */
    @TableField("flow_key")
    private String flowKey;

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
     * Form schema (JSON)
     */
    @TableField("form_schema")
    private String formSchema;

    /**
     * Description
     */
    @TableField("description")
    private String description;
}
