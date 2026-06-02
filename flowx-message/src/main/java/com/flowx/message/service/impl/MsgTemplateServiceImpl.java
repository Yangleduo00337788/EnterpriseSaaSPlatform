package com.flowx.message.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.message.convert.MsgTemplateConvert;
import com.flowx.message.entity.MsgTemplate;
import com.flowx.message.mapper.MsgTemplateMapper;
import com.flowx.message.service.MsgTemplateService;
import com.flowx.message.vo.MsgTemplateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Message template service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MsgTemplateServiceImpl implements MsgTemplateService {

    private final MsgTemplateMapper templateMapper;
    private final MsgTemplateConvert templateConvert;

    @Override
    public MsgTemplateVO getTemplateById(Long templateId) {
        AssertUtil.notNull(templateId, "模板ID不能为空");
        MsgTemplate template = templateMapper.selectOneById(templateId);
        AssertUtil.notNull(template, ResultCodeEnum.NOT_FOUND.getCode(), "消息模板不存在");
        return templateConvert.toVO(template);
    }

    @Override
    public MsgTemplateVO getTemplateByCode(String templateCode) {
        AssertUtil.notBlank(templateCode, "模板编码不能为空");
        MsgTemplate template = templateMapper.selectByTemplateCode(templateCode);
        if (template == null) {
            return null;
        }
        return templateConvert.toVO(template);
    }

    @Override
    public Long createTemplate(MsgTemplateVO vo) {
        AssertUtil.notNull(vo, "模板信息不能为空");
        AssertUtil.notBlank(vo.getTemplateName(), "模板名称不能为空");
        AssertUtil.notBlank(vo.getTemplateCode(), "模板编码不能为空");

        // Check template code uniqueness
        MsgTemplate existing = templateMapper.selectByTemplateCode(vo.getTemplateCode());
        if (existing != null) {
            throw new BizException(ResultCodeEnum.DUPLICATE_DATA.getCode(), "模板编码已存在");
        }

        MsgTemplate template = templateConvert.toEntity(vo);

        // Set default status if not provided
        if (template.getStatus() == null) {
            template.setStatus(1);
        }

        templateMapper.insert(template);
        log.info("Created message template: {}", template.getTemplateCode());
        return template.getId();
    }

    @Override
    public void updateTemplate(Long templateId, MsgTemplateVO vo) {
        AssertUtil.notNull(templateId, "模板ID不能为空");
        AssertUtil.notNull(vo, "模板信息不能为空");

        MsgTemplate template = templateMapper.selectOneById(templateId);
        AssertUtil.notNull(template, ResultCodeEnum.NOT_FOUND.getCode(), "消息模板不存在");

        // Check template code uniqueness if changed
        if (StringUtils.hasText(vo.getTemplateCode()) && !vo.getTemplateCode().equals(template.getTemplateCode())) {
            MsgTemplate existing = templateMapper.selectByTemplateCode(vo.getTemplateCode());
            if (existing != null) {
                throw new BizException(ResultCodeEnum.DUPLICATE_DATA.getCode(), "模板编码已存在");
            }
        }

        templateConvert.updateEntity(vo, template);
        templateMapper.updateById(template);
        log.info("Updated message template: {}", templateId);
    }

    @Override
    public void deleteTemplate(Long templateId) {
        AssertUtil.notNull(templateId, "模板ID不能为空");
        MsgTemplate template = templateMapper.selectOneById(templateId);
        AssertUtil.notNull(template, ResultCodeEnum.NOT_FOUND.getCode(), "消息模板不存在");

        templateMapper.deleteById(templateId);
        log.info("Deleted message template: {}", templateId);
    }

    @Override
    public PageResult<MsgTemplateVO> listTemplates(Integer pageNum, Integer pageSize, String templateType) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (StringUtils.hasText(templateType)) {
            wrapper.eq("template_type", templateType);
        }

        wrapper.orderBy("create_time", false);

        Page<MsgTemplate> templatePage = templateMapper.paginate(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10, wrapper);
        List<MsgTemplateVO> voList = templateConvert.toVOList(templatePage.getRecords());

        return PageResult.of(templatePage.getTotalRow(), voList,
                pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
    }
}
