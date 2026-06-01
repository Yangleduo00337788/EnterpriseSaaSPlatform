package com.flowx.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Approval submit DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ApprovalSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Approval type ID
     */
    @NotNull(message = "审批类型不能为空")
    private Long typeId;

    /**
     * Approval title
     */
    @NotBlank(message = "审批标题不能为空")
    private String title;

    /**
     * Urgency level (0=normal, 1=urgent, 2=very urgent)
     */
    private Integer urgencyLevel;

    /**
     * Form data
     */
    private Map<String, Object> formData;
}
