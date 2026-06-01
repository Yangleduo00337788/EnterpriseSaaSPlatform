package com.flowx.workflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * Flow deploy DTO for deploying/updating BPMN
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowDeployDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Definition ID
     */
    @NotNull(message = "流程定义ID不能为空")
    private Long definitionId;

    /**
     * BPMN XML content
     */
    private String bpmnXml;
}
