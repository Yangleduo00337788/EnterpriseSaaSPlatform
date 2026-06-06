package com.flowcloud.notification.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.event.ApprovalEvent;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.notification.dto.MessageTemplateDTO;
import com.flowcloud.notification.entity.SysMessageTemplate;
import com.flowcloud.notification.mapper.SysMessageTemplateMapper;
import com.flowcloud.notification.service.MessageTemplateService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageTemplateServiceImpl implements MessageTemplateService {

    private final SysMessageTemplateMapper templateMapper;

    @Override
    public List<SysMessageTemplate> listAll() {
        return templateMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysMessageTemplate::getTenantId).eq(TenantContext.getTenantId())
                        .orderBy(SysMessageTemplate::getTemplateCode, true));
    }

    @Override
    public SysMessageTemplate getById(Long id) {
        SysMessageTemplate template = templateMapper.selectOneById(id);
        if (template == null || !template.getTenantId().equals(TenantContext.getTenantId())) {
            throw new BusinessException("消息模板不存在");
        }
        return template;
    }

    @Override
    public void create(MessageTemplateDTO dto) {
        SysMessageTemplate template = new SysMessageTemplate();
        template.setTenantId(TenantContext.getTenantId());
        fillTemplate(template, dto);
        templateMapper.insert(template);
    }

    @Override
    public void update(MessageTemplateDTO dto) {
        SysMessageTemplate template = getById(dto.getId());
        fillTemplate(template, dto);
        templateMapper.update(template);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        templateMapper.deleteById(id);
    }

    @Override
    public String renderTitle(ApprovalEvent event) {
        SysMessageTemplate template = findTemplate(event.getEventType().name());
        if (template != null) {
            return render(template.getTitleTemplate(), event);
        }
        return defaultTitle(event);
    }

    @Override
    public String renderContent(ApprovalEvent event) {
        SysMessageTemplate template = findTemplate(event.getEventType().name());
        if (template != null) {
            return render(template.getContentTemplate(), event);
        }
        return defaultContent(event);
    }

    private SysMessageTemplate findTemplate(String eventType) {
        return templateMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SysMessageTemplate::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysMessageTemplate::getEventType).eq(eventType)
                        .and(SysMessageTemplate::getStatus).eq(1)
                        .limit(1));
    }

    private String render(String template, ApprovalEvent event) {
        String commentPart = (event.getComment() != null && !event.getComment().isBlank())
                ? "：" + event.getComment() : "";
        return template
                .replace("{operator}", nullToEmpty(event.getOperatorName()))
                .replace("{title}", nullToEmpty(event.getInstanceTitle()))
                .replace("{comment}", commentPart);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void fillTemplate(SysMessageTemplate template, MessageTemplateDTO dto) {
        template.setTemplateCode(dto.getTemplateCode());
        template.setTemplateName(dto.getTemplateName());
        template.setEventType(dto.getEventType());
        template.setTitleTemplate(dto.getTitleTemplate());
        template.setContentTemplate(dto.getContentTemplate());
        template.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }

    private String defaultTitle(ApprovalEvent event) {
        return switch (event.getEventType()) {
            case TASK_ASSIGNED -> "新的审批待处理";
            case APPROVED -> "审批已通过";
            case REJECTED -> "审批已驳回";
            case CANCELLED -> "审批已撤销";
            case REMIND -> "审批催办提醒";
        };
    }

    private String defaultContent(ApprovalEvent event) {
        return switch (event.getEventType()) {
            case TASK_ASSIGNED -> String.format("「%s」提交了「%s」，请及时审批。",
                    event.getOperatorName(), event.getInstanceTitle());
            case APPROVED -> String.format("您的「%s」已通过全部审批。", event.getInstanceTitle());
            case REJECTED -> {
                String commentPart = StringUtils.hasText(event.getComment()) ? "：" + event.getComment() : "";
                yield String.format("您的「%s」已被驳回%s。", event.getInstanceTitle(), commentPart);
            }
            case CANCELLED -> String.format("「%s」已被撤销。", event.getInstanceTitle());
            case REMIND -> String.format("「%s」催办了「%s」，请尽快处理。",
                    event.getOperatorName(), event.getInstanceTitle());
        };
    }
}
