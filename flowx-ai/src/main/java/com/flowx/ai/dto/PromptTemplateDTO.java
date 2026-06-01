package com.flowx.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Prompt template DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class PromptTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Template name
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    /**
     * Template type
     */
    private String templateType;

    /**
     * Prompt content
     */
    @NotBlank(message = "提示词内容不能为空")
    private String promptContent;

    /**
     * Variables (JSON array)
     */
    private String variables;
}
