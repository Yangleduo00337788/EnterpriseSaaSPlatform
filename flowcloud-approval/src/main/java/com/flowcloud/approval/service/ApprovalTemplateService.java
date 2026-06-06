package com.flowcloud.approval.service;

import com.flowcloud.approval.dto.TemplateDTO;
import com.flowcloud.approval.vo.TemplateVO;
import com.flowcloud.approval.vo.TemplateVersionVO;

import java.util.List;

public interface ApprovalTemplateService {

    /** 模板列表（管理态：所有状态；发起态：仅 active） */
    List<TemplateVO> listTemplates(String category);

    /** 所有状态的模板（管理用） */
    List<TemplateVO> listAllTemplates(String category);

    TemplateVO getById(Long id);

    void create(TemplateDTO dto);

    void update(TemplateDTO dto);

    void delete(Long id);

    /** 发布模板：draft/disabled → active，创建版本快照 */
    void publish(Long id, String remark);

    /** 停用模板：active → disabled */
    void disable(Long id);

    /** 版本历史 */
    List<TemplateVersionVO> listVersions(Long templateId);
}