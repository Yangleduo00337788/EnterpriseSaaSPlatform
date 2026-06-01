package com.flowx.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI prompt template view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class AiPromptTemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Template ID
     */
    private Long id;

    /**
     * Template name
     */
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    private String templateCode;

    /**
     * Template type
     */
    private String templateType;

    /**
     * Prompt content
     */
    private String promptContent;

    /**
     * Variables (JSON array)
     */
    private String variables;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Usage count
     */
    private Integer usageCount;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
