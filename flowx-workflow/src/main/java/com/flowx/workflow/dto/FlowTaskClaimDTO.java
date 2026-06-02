package com.flowx.workflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * Flow task claim DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowTaskClaimDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Task ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
}
