package com.flowx.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Flow instance start DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowInstanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Flow definition ID
     */
    @NotNull(message = "流程定义ID不能为空")
    private Long definitionId;

    /**
     * Business key (关联业务ID)
     */
    private String businessKey;

    /**
     * Business type
     */
    private String businessType;

    /**
     * Instance title
     */
    @NotBlank(message = "流程标题不能为空")
    private String title;

    /**
     * Process variables
     */
    private Map<String, Object> variables;
}
