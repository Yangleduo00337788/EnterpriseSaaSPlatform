package com.flowx.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("ai_prompt_template")
public class AiPromptTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template name
     */
    @TableField("template_name")
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    @TableField("template_code")
    private String templateCode;

    /**
     * Template type
     */
    @TableField("template_type")
    private String templateType;

    /**
     * Prompt content
     */
    @TableField("prompt_content")
    private String promptContent;

    /**
     * Variables (JSON array)
     */
    @TableField("variables")
    private String variables;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Usage count
     */
    @TableField("usage_count")
    private Integer usageCount;
}
