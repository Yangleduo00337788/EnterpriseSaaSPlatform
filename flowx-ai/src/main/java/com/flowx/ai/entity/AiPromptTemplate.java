package com.flowx.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI prompt template entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("ai_prompt_template")
public class AiPromptTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template name
     */
    @Column("template_name")
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    @Column("template_code")
    private String templateCode;

    /**
     * Template type
     */
    @Column("template_type")
    private String templateType;

    /**
     * Prompt content
     */
    @Column("prompt_content")
    private String promptContent;

    /**
     * Variables (JSON array)
     */
    @Column("variables")
    private String variables;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Usage count
     */
    @Column("usage_count")
    private Integer usageCount;
}
