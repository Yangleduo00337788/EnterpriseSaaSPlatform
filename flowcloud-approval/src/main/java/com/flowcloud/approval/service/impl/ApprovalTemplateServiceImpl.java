package com.flowcloud.approval.service.impl;

import cn.hutool.json.JSONUtil;
import com.flowcloud.approval.dto.FlowNodeDTO;
import com.flowcloud.approval.dto.TemplateDTO;
import com.flowcloud.approval.entity.ApprovalTemplate;
import com.flowcloud.approval.entity.ApprovalTemplateVersion;
import com.flowcloud.approval.mapper.ApprovalTemplateMapper;
import com.flowcloud.approval.mapper.ApprovalTemplateVersionMapper;
import com.flowcloud.approval.service.ApprovalTemplateService;
import com.flowcloud.approval.vo.TemplateVersionVO;
import com.flowcloud.approval.vo.TemplateVO;
import com.flowcloud.common.event.AuditEvent;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalTemplateServiceImpl implements ApprovalTemplateService {

    /** status 常量：0=草稿 1=已发布 2=已停用 */
    private static final int STATUS_DRAFT    = 0;
    private static final int STATUS_ACTIVE   = 1;
    private static final int STATUS_DISABLED = 2;

    private final ApprovalTemplateMapper templateMapper;
    private final ApprovalTemplateVersionMapper versionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<TemplateVO> listTemplates(String category) {
        QueryWrapper query = QueryWrapper.create()
                .where(ApprovalTemplate::getTenantId).eq(TenantContext.getTenantId())
                .and(ApprovalTemplate::getStatus).eq(STATUS_ACTIVE);
        if (StringUtils.hasText(category)) {
            query.and(ApprovalTemplate::getCategory).eq(category);
        }
        query.orderBy(ApprovalTemplate::getSort, true);
        return templateMapper.selectListByQuery(query).stream().map(this::toVO).toList();
    }

    @Override
    public List<TemplateVO> listAllTemplates(String category) {
        QueryWrapper query = QueryWrapper.create()
                .where(ApprovalTemplate::getTenantId).eq(TenantContext.getTenantId());
        if (StringUtils.hasText(category)) {
            query.and(ApprovalTemplate::getCategory).eq(category);
        }
        query.orderBy(ApprovalTemplate::getSort, true)
             .orderBy(ApprovalTemplate::getCreateTime, false);
        return templateMapper.selectListByQuery(query).stream().map(this::toVO).toList();
    }

    @Override
    public TemplateVO getById(Long id) {
        ApprovalTemplate template = templateMapper.selectOneById(id);
        if (template == null) throw new BusinessException("模板不存在");
        return toVO(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(TemplateDTO dto) {
        ApprovalTemplate template = new ApprovalTemplate();
        template.setTenantId(TenantContext.getTenantId());
        template.setStatus(STATUS_DRAFT);
        template.setPubVersion(0);
        fillTemplate(template, dto);
        templateMapper.insert(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TemplateDTO dto) {
        ApprovalTemplate template = templateMapper.selectOneById(dto.getId());
        if (template == null) throw new BusinessException("模板不存在");
        if (STATUS_DISABLED == template.getStatus()) throw new BusinessException("已停用的模板不可编辑");
        fillTemplate(template, dto);
        // 编辑后若原是已发布状态，自动退回草稿（需重新发布才能生效）
        if (STATUS_ACTIVE == template.getStatus()) {
            template.setStatus(STATUS_DRAFT);
        }
        templateMapper.update(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ApprovalTemplate template = templateMapper.selectOneById(id);
        if (template == null) return;
        if (STATUS_ACTIVE == template.getStatus()) throw new BusinessException("请先停用模板再删除");
        templateMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id, String remark) {
        ApprovalTemplate template = templateMapper.selectOneById(id);
        if (template == null) throw new BusinessException("模板不存在");
        if (!StringUtils.hasText(template.getFlowConfig())) throw new BusinessException("流程节点未配置");

        List<FlowNodeDTO> nodes = JSONUtil.toList(template.getFlowConfig(), FlowNodeDTO.class);
        validateFlowNodes(nodes);

        int nextVersion = (template.getPubVersion() == null ? 0 : template.getPubVersion()) + 1;

        ApprovalTemplateVersion snap = new ApprovalTemplateVersion();
        snap.setTenantId(template.getTenantId());
        snap.setTemplateId(template.getId());
        snap.setVersion(nextVersion);
        snap.setFlowConfig(template.getFlowConfig());
        snap.setFormSchema(template.getFormSchema());
        snap.setRemark(remark);
        snap.setCreateTime(LocalDateTime.now());
        versionMapper.insert(snap);

        template.setStatus(STATUS_ACTIVE);
        template.setPubVersion(nextVersion);
        templateMapper.update(template);

        eventPublisher.publishEvent(AuditEvent.of(
                TenantContext.getUserId(), TenantContext.getTenantId(),
                "PUBLISH_TEMPLATE", "approval",
                "发布审批模板[" + template.getTemplateName() + "]v" + nextVersion, null));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        ApprovalTemplate template = templateMapper.selectOneById(id);
        if (template == null) throw new BusinessException("模板不存在");
        template.setStatus(STATUS_DISABLED);
        templateMapper.update(template);

        eventPublisher.publishEvent(AuditEvent.of(
                TenantContext.getUserId(), TenantContext.getTenantId(),
                "DISABLE_TEMPLATE", "approval",
                "停用审批模板[" + template.getTemplateName() + "]", null));
    }

    @Override
    public List<TemplateVersionVO> listVersions(Long templateId) {
        return versionMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where(ApprovalTemplateVersion::getTemplateId).eq(templateId)
                                .orderBy(ApprovalTemplateVersion::getVersion, false))
                .stream().map(v -> {
                    TemplateVersionVO vo = new TemplateVersionVO();
                    vo.setId(v.getId());
                    vo.setTemplateId(v.getTemplateId());
                    vo.setVersion(v.getVersion());
                    vo.setFlowConfig(v.getFlowConfig());
                    vo.setFormSchema(v.getFormSchema());
                    vo.setRemark(v.getRemark());
                    vo.setCreateTime(v.getCreateTime());
                    return vo;
                }).toList();
    }

    private void fillTemplate(ApprovalTemplate template, TemplateDTO dto) {
        template.setTemplateCode(dto.getTemplateCode());
        template.setTemplateName(dto.getTemplateName());
        template.setCategory(dto.getCategory());
        template.setDescription(dto.getDescription());
        template.setFormSchema(dto.getFormSchema());
        if (dto.getFlowNodes() != null) {
            template.setFlowConfig(JSONUtil.toJsonStr(dto.getFlowNodes()));
        }
        if (dto.getSort() != null) template.setSort(dto.getSort());
    }

    private void validateFlowNodes(List<FlowNodeDTO> flowNodes) {
        if (flowNodes == null || flowNodes.isEmpty()) throw new BusinessException("至少配置一个审批节点");
        for (FlowNodeDTO node : flowNodes) {
            if ("self".equalsIgnoreCase(node.getType())) continue;
            String src = node.getApproverSource();
            // dept_leader / manager 不需要 approverIds
            if ("dept_leader".equalsIgnoreCase(src) || "manager".equalsIgnoreCase(src)) continue;
            if (node.getApproverIds() == null || node.getApproverIds().isEmpty()) {
                throw new BusinessException("节点「" + node.getName() + "」未配置审批人");
            }
        }
    }

    private TemplateVO toVO(ApprovalTemplate template) {
        TemplateVO vo = new TemplateVO();
        vo.setId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setCategory(template.getCategory());
        vo.setDescription(template.getDescription());
        vo.setFormSchema(template.getFormSchema());
        vo.setStatus(template.getStatus());
        vo.setStatusLabel(switch (template.getStatus() == null ? -1 : template.getStatus()) {
            case 0 -> "草稿";
            case 1 -> "已发布";
            case 2 -> "已停用";
            default -> "未知";
        });
        vo.setSort(template.getSort());
        vo.setPubVersion(template.getPubVersion());
        vo.setCreateTime(template.getCreateTime());
        if (StringUtils.hasText(template.getFlowConfig())) {
            vo.setFlowNodes(JSONUtil.toList(template.getFlowConfig(), FlowNodeDTO.class));
        }
        return vo;
    }
}