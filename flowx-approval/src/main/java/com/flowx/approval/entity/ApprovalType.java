package com.flowx.approval.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("approval_type")
public class ApprovalType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Type name
     */
    @Column("type_name")
    private String typeName;

    /**
     * Type code (unique)
     */
    @Column("type_code")
    private String typeCode;

    /**
     * Icon
     */
    @Column("icon")
    private String icon;

    /**
     * Associated flow key
     */
    @Column("flow_key")
    private String flowKey;

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
     * Form schema (JSON)
     */
    @Column("form_schema")
    private String formSchema;

    /**
     * Description
     */
    @Column("description")
    private String description;
}
