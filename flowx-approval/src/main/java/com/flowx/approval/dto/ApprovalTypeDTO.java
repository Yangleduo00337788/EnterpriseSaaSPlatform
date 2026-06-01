package com.flowx.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Approval type create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ApprovalTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Type name
     */
    @NotBlank(message = "审批类型名称不能为空")
    @Size(max = 100, message = "审批类型名称长度不能超过100个字符")
    private String typeName;

    /**
     * Type code (unique)
     */
    @NotBlank(message = "审批类型编码不能为空")
    @Size(max = 100, message = "审批类型编码长度不能超过100个字符")
    private String typeCode;

    /**
     * Icon
     */
    @Size(max = 200, message = "图标长度不能超过200个字符")
    private String icon;

    /**
     * Associated flow key
     */
    @NotBlank(message = "关联流程标识不能为空")
    @Size(max = 100, message = "关联流程标识长度不能超过100个字符")
    private String flowKey;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Form schema (JSON)
     */
    private String formSchema;

    /**
     * Description
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
}
