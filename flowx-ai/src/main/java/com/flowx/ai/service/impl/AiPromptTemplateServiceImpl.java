package com.flowx.ai.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.ai.dto.PromptTemplateDTO;
import com.flowx.ai.entity.AiPromptTemplate;
import com.flowx.ai.mapper.AiPromptTemplateMapper;
import com.flowx.ai.service.AiPromptTemplateService;
import com.flowx.ai.vo.AiPromptTemplateVO;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI prompt template service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiPromptTemplateServiceImpl implements AiPromptTemplateService {

    private final AiPromptTemplateMapper templateMapper;

    @Override
    public AiPromptTemplateVO getTemplateById(Long templateId) {
        AssertUtil.notNull(templateId, "模板ID不能为空");
        AiPromptTemplate template = templateMapper.selectOneById(templateId);
        AssertUtil.notNull(template, ResultCodeEnum.NOT_FOUND.getCode(), "提示词模板不存在");
        return toVO(template);
    }

    @Override
    public AiPromptTemplateVO getTemplateByCode(String templateCode) {
        AssertUtil.notBlank(templateCode, "模板编码不能为空");
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("template_code", templateCode);
        AiPromptTemplate template = templateMapper.selectOne(wrapper);
        return template != null ? toVO(template) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(PromptTemplateDTO dto) {
        AssertUtil.notNull(dto, "模板信息不能为空");
        AssertUtil.notBlank(dto.getTemplateName(), "模板名称不能为空");
        AssertUtil.notBlank(dto.getTemplateCode(), "模板编码不能为空");
        AssertUtil.notBlank(dto.getPromptContent(), "提示词内容不能为空");

        // Check template code uniqueness
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("template_code", dto.getTemplateCode());
        Long count = templateMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException(ResultCodeEnum.DUPLICATE_DATA.getCode(), "模板编码已存在");
        }

        AiPromptTemplate template = new AiPromptTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setTemplateCode(dto.getTemplateCode());
        template.setTemplateType(dto.getTemplateType());
        template.setPromptContent(dto.getPromptContent());
        template.setVariables(dto.getVariables());
        template.setStatus(1);
        template.setUsageCount(0);

        templateMapper.insert(template);
        log.info("Created AI prompt template: {}", dto.getTemplateCode());
        return template.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(Long templateId, PromptTemplateDTO dto) {
        AssertUtil.notNull(templateId, "模板ID不能为空");
        AssertUtil.notNull(dto, "模板信息不能为空");

        AiPromptTemplate template = templateMapper.selectOneById(templateId);
        AssertUtil.notNull(template, ResultCodeEnum.NOT_FOUND.getCode(), "提示词模板不存在");

        // Check template code uniqueness if changed
        if (StringUtils.hasText(dto.getTemplateCode()) && !dto.getTemplateCode().equals(template.getTemplateCode())) {
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper.eq("template_code", dto.getTemplateCode());
            Long count = templateMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException(ResultCodeEnum.DUPLICATE_DATA.getCode(), "模板编码已存在");
            }
        }

        if (StringUtils.hasText(dto.getTemplateName())) {
            template.setTemplateName(dto.getTemplateName());
        }
        if (StringUtils.hasText(dto.getTemplateCode())) {
            template.setTemplateCode(dto.getTemplateCode());
        }
        if (dto.getTemplateType() != null) {
            template.setTemplateType(dto.getTemplateType());
        }
        if (StringUtils.hasText(dto.getPromptContent())) {
            template.setPromptContent(dto.getPromptContent());
        }
        if (dto.getVariables() != null) {
            template.setVariables(dto.getVariables());
        }

        templateMapper.updateById(template);
        log.info("Updated AI prompt template: {}", templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long templateId) {
        AssertUtil.notNull(templateId, "模板ID不能为空");
        AiPromptTemplate template = templateMapper.selectOneById(templateId);
        AssertUtil.notNull(template, ResultCodeEnum.NOT_FOUND.getCode(), "提示词模板不存在");

        templateMapper.deleteById(templateId);
        log.info("Deleted AI prompt template: {}", templateId);
    }

    @Override
    public PageResult<AiPromptTemplateVO> listTemplates(Integer pageNum, Integer pageSize, String templateType) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (StringUtils.hasText(templateType)) {
            wrapper.eq("template_type", templateType);
        }

        wrapper.orderBy("create_time", false);

        Page<AiPromptTemplate> templatePage = templateMapper.paginate(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10, wrapper);
        List<AiPromptTemplateVO> voList = templatePage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(templatePage.getTotalRow(), voList,
                pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
    }

    /**
     * Convert entity to VO
     */
    private AiPromptTemplateVO toVO(AiPromptTemplate entity) {
        AiPromptTemplateVO vo = new AiPromptTemplateVO();
        vo.setId(entity.getId());
        vo.setTemplateName(entity.getTemplateName());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTemplateType(entity.getTemplateType());
        vo.setPromptContent(entity.getPromptContent());
        vo.setVariables(entity.getVariables());
        vo.setStatus(entity.getStatus());
        vo.setUsageCount(entity.getUsageCount());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
