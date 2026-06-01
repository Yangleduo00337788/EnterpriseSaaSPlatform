package com.flowx.ai.service;

import com.flowx.ai.dto.PromptTemplateDTO;
import com.flowx.ai.vo.AiPromptTemplateVO;
import com.flowx.common.core.result.PageResult;

/**
 * AI prompt template service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface AiPromptTemplateService {

    /**
     * Get template by ID
     *
     * @param templateId template ID
     * @return template VO
     */
    AiPromptTemplateVO getTemplateById(Long templateId);

    /**
     * Get template by code
     *
     * @param templateCode template code
     * @return template VO or null
     */
    AiPromptTemplateVO getTemplateByCode(String templateCode);

    /**
     * Create new template
     *
     * @param dto template data
     * @return created template ID
     */
    Long createTemplate(PromptTemplateDTO dto);

    /**
     * Update existing template
     *
     * @param templateId template ID
     * @param dto        template data
     */
    void updateTemplate(Long templateId, PromptTemplateDTO dto);

    /**
     * Delete template (soft delete)
     *
     * @param templateId template ID
     */
    void deleteTemplate(Long templateId);

    /**
     * List templates with pagination
     *
     * @param pageNum      page number
     * @param pageSize     page size
     * @param templateType optional type filter
     * @return paginated template list
     */
    PageResult<AiPromptTemplateVO> listTemplates(Integer pageNum, Integer pageSize, String templateType);
}
