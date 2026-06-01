package com.flowx.workflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Flow task complete DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowTaskCompleteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Task ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * Approved flag (true=approve, false=reject)
     */
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    /**
     * Comment
     */
    private String comment;

    /**
     * Task variables
     */
    private Map<String, Object> variables;
}
