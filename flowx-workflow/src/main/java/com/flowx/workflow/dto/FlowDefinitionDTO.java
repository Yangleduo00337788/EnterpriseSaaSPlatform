package com.flowx.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Flow definition create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowDefinitionDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Definition key (unique identifier for the process)
     */
    @NotBlank(message = "流程定义标识不能为空")
    @Size(max = 100, message = "流程定义标识长度不能超过100个字符")
    private String definitionKey;

    /**
     * Definition name
     */
    @NotBlank(message = "流程定义名称不能为空")
    @Size(max = 200, message = "流程定义名称长度不能超过200个字符")
    private String definitionName;

    /**
     * Category ID
     */
    @NotNull(message = "流程分类不能为空")
    private Long categoryId;

    /**
     * Description
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    /**
     * BPMN XML content
     */
    private String bpmnXml;

    /**
     * Form JSON schema
     */
    private String formJson;
}
