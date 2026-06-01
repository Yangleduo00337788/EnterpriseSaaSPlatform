package com.flowx.message.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.message.vo.MsgTemplateVO;

/**
 * Message template service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface MsgTemplateService {

    /**
     * Get template by ID
     *
     * @param templateId template ID
     * @return template VO
     */
    MsgTemplateVO getTemplateById(Long templateId);

    /**
     * Get template by template code
     *
     * @param templateCode template code
     * @return template VO or null
     */
    MsgTemplateVO getTemplateByCode(String templateCode);

    /**
     * Create new template
     *
     * @param vo template data
     * @return created template ID
     */
    Long createTemplate(MsgTemplateVO vo);

    /**
     * Update existing template
     *
     * @param templateId template ID
     * @param vo         template data
     */
    void updateTemplate(Long templateId, MsgTemplateVO vo);

    /**
     * Delete template (soft delete)
     *
     * @param templateId template ID
     */
    void deleteTemplate(Long templateId);

    /**
     * List templates with pagination
     *
     * @param pageNum    page number
     * @param pageSize   page size
     * @param templateType optional type filter
     * @return paginated template list
     */
    PageResult<MsgTemplateVO> listTemplates(Integer pageNum, Integer pageSize, String templateType);
}
