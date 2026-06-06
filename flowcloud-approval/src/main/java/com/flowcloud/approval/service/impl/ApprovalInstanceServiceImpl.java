package com.flowcloud.approval.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.flowcloud.approval.dto.FlowNodeDTO;
import com.flowcloud.approval.dto.SubmitApprovalDTO;
import com.flowcloud.approval.entity.*;
import com.flowcloud.approval.enums.ApprovalStatus;
import com.flowcloud.approval.enums.TaskStatus;
import com.flowcloud.approval.mapper.*;
import com.flowcloud.approval.service.ApprovalInstanceService;
import com.flowcloud.approval.vo.InstanceVO;
import com.flowcloud.approval.vo.RecordVO;
import com.flowcloud.approval.vo.TaskVO;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.event.ApprovalEvent;
import com.flowcloud.common.event.AuditEvent;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.entity.SysUserRole;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.mapper.SysUserRoleMapper;
import com.flowcloud.system.service.RoleAuthService;
import org.springframework.context.ApplicationEventPublisher;
import com.flowcloud.system.support.PermissionCodes;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalInstanceServiceImpl implements ApprovalInstanceService {

    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalTemplateMapper templateMapper;
    private final ApprovalTaskMapper taskMapper;
    private final ApprovalRecordMapper recordMapper;
    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final RoleAuthService roleAuthService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PageResult<InstanceVO> pageMySubmissions(String status, int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(ApprovalInstance::getTenantId).eq(TenantContext.getTenantId())
                .and(ApprovalInstance::getApplicantId).eq(TenantContext.getUserId());
        if (StringUtils.hasText(status)) {
            query.and(ApprovalInstance::getStatus).eq(status);
        }
        query.orderBy(ApprovalInstance::getCreateTime, false);
        Page<ApprovalInstance> page = instanceMapper.paginate(pageNum, pageSize, query);
        List<InstanceVO> vos = page.getRecords().stream().map(this::toSimpleVO).toList();
        return PageResult.of(vos, page.getTotalRow(), pageNum, pageSize);
    }

    @Override
    public PageResult<InstanceVO> pageAll(String status, String category, int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(ApprovalInstance::getTenantId).eq(TenantContext.getTenantId());
        if (StringUtils.hasText(status)) {
            query.and(ApprovalInstance::getStatus).eq(status);
        }
        if (StringUtils.hasText(category)) {
            query.and(ApprovalInstance::getCategory).eq(category);
        }
        query.orderBy(ApprovalInstance::getCreateTime, false);
        Page<ApprovalInstance> page = instanceMapper.paginate(pageNum, pageSize, query);
        List<InstanceVO> vos = page.getRecords().stream().map(this::toSimpleVO).toList();
        return PageResult.of(vos, page.getTotalRow(), pageNum, pageSize);
    }

    @Override
    public InstanceVO getDetail(Long id) {
        ApprovalInstance instance = instanceMapper.selectOneById(id);
        if (instance == null) {
            throw new BusinessException("审批单不存在");
        }
        ensureCanView(instance);
        InstanceVO vo = toSimpleVO(instance);

        List<ApprovalRecord> records = recordMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalRecord::getInstanceId).eq(id)
                        .orderBy(ApprovalRecord::getCreateTime, true));
        vo.setRecords(records.stream().map(r -> {
            RecordVO rv = new RecordVO();
            rv.setNodeIndex(r.getNodeIndex());
            rv.setNodeName(r.getNodeName());
            rv.setOperatorId(r.getOperatorId());
            rv.setOperatorName(r.getOperatorName());
            rv.setAction(r.getAction());
            rv.setComment(r.getComment());
            rv.setCreateTime(r.getCreateTime());
            return rv;
        }).toList());

        List<ApprovalTask> tasks = taskMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getInstanceId).eq(id)
                        .orderBy(ApprovalTask::getNodeIndex, true));
        vo.setTasks(tasks.stream().map(this::toTaskVO).toList());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(SubmitApprovalDTO dto) {
        ApprovalTemplate template = templateMapper.selectOneById(dto.getTemplateId());
        if (template == null) {
            throw new BusinessException("审批模板不存在");
        }

        SysUser applicant = userMapper.selectOneById(TenantContext.getUserId());
        if (applicant == null) {
            throw new BusinessException("用户不存在");
        }

        List<FlowNodeDTO> flowNodes = JSONUtil.toList(template.getFlowConfig(), FlowNodeDTO.class);
        if (flowNodes == null || flowNodes.isEmpty()) {
            throw new BusinessException("审批流程未配置");
        }

        ApprovalInstance instance = new ApprovalInstance();
        instance.setTenantId(TenantContext.getTenantId());
        instance.setInstanceNo("AP" + IdUtil.getSnowflakeNextIdStr());
        instance.setTemplateId(template.getId());
        instance.setTemplateName(template.getTemplateName());
        instance.setCategory(template.getCategory());
        instance.setTitle(dto.getTitle());
        instance.setApplicantId(applicant.getId());
        instance.setApplicantName(applicant.getRealName());
        instance.setDeptId(applicant.getDeptId());
        instance.setFormData(dto.getFormData());
        instance.setFlowConfigSnapshot(template.getFlowConfig());
        instance.setStatus(ApprovalStatus.PENDING.getCode());
        instance.setCurrentNode(0);
        instance.setSubmitTime(LocalDateTime.now());
        instanceMapper.insert(instance);

        createTasksForNode(instance, flowNodes.get(0), 0);

        ApprovalRecord record = new ApprovalRecord();
        record.setTenantId(TenantContext.getTenantId());
        record.setInstanceId(instance.getId());
        record.setNodeIndex(-1);
        record.setNodeName("发起申请");
        record.setOperatorId(applicant.getId());
        record.setOperatorName(applicant.getRealName());
        record.setAction("submit");
        record.setComment("提交审批");
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);

        eventPublisher.publishEvent(AuditEvent.of(
                applicant.getId(), instance.getTenantId(),
                "SUBMIT_APPROVAL", "approval",
                "提交审批[" + instance.getTemplateName() + "]：" + instance.getTitle(), null));

        return instance.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        ApprovalInstance instance = instanceMapper.selectOneById(id);
        if (instance == null) {
            throw new BusinessException("审批单不存在");
        }
        if (!instance.getApplicantId().equals(TenantContext.getUserId())) {
            throw new BusinessException("只能撤销自己的审批单");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(instance.getStatus())) {
            throw new BusinessException("当前状态不可撤销");
        }
        instance.setStatus(ApprovalStatus.CANCELLED.getCode());
        instance.setFinishTime(LocalDateTime.now());
        instanceMapper.update(instance);

        List<ApprovalTask> pendingTasks = taskMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getInstanceId).eq(id)
                        .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode()));
        SysUser operator = userMapper.selectOneById(TenantContext.getUserId());
        String operatorName = operator != null ? operator.getRealName() : "";
        for (ApprovalTask pendingTask : pendingTasks) {
            eventPublisher.publishEvent(new ApprovalEvent(
                    this, ApprovalEvent.Type.CANCELLED,
                    instance.getTenantId(), pendingTask.getApproverId(),
                    instance.getTitle(), instance.getId(), instance.getCategory(),
                    operatorName, null));
        }
        taskMapper.deleteByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getInstanceId).eq(id)
                        .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode()));
    }

    void createTasksForNode(ApprovalInstance instance, FlowNodeDTO node, int nodeIndex) {
        instance.setCurrentNode(nodeIndex);
        List<Long> approverIds = resolveApproverIds(node, instance);
        if (!approverIds.isEmpty()) {
            String approverNames = approverIds.stream()
                    .map(id -> {
                        SysUser user = userMapper.selectOneById(id);
                        return user != null ? user.getRealName() : "";
                    })
                    .collect(Collectors.joining(","));
            instance.setCurrentApprovers(approverNames);
        }
        instanceMapper.update(instance);

        if (approverIds.isEmpty()) {
            return;
        }
        for (Long approverId : approverIds) {
            SysUser approver = userMapper.selectOneById(approverId);
            if (approver == null) {
                continue;
            }
            ApprovalTask task = new ApprovalTask();
            task.setTenantId(instance.getTenantId());
            task.setInstanceId(instance.getId());
            task.setInstanceNo(instance.getInstanceNo());
            task.setTitle(instance.getTitle());
            task.setNodeIndex(nodeIndex);
            task.setNodeName(node.getName());
            task.setApproverId(approverId);
            task.setApproverName(approver.getRealName());
            task.setStatus(TaskStatus.PENDING.getCode());
            taskMapper.insert(task);

            eventPublisher.publishEvent(new ApprovalEvent(
                    this, ApprovalEvent.Type.TASK_ASSIGNED,
                    instance.getTenantId(), approverId,
                    instance.getTitle(), instance.getId(), instance.getCategory(),
                    instance.getApplicantName(), null
            ));
        }
    }

    private List<Long> resolveApproverIds(FlowNodeDTO node, ApprovalInstance instance) {
        // 自审节点
        if ("self".equalsIgnoreCase(node.getType())) {
            return List.of(instance.getApplicantId());
        }

        String source = node.getApproverSource();

        // 部门负责人
        if ("dept_leader".equalsIgnoreCase(source)) {
            SysUser applicant = userMapper.selectOneById(instance.getApplicantId());
            if (applicant == null || applicant.getDeptId() == null) return List.of();
            SysDept dept = deptMapper.selectOneById(applicant.getDeptId());
            if (dept == null || dept.getLeaderUserId() == null) return List.of();
            return List.of(dept.getLeaderUserId());
        }

        // 直属上级
        if ("manager".equalsIgnoreCase(source)) {
            SysUser applicant = userMapper.selectOneById(instance.getApplicantId());
            if (applicant == null || applicant.getManagerId() == null) return List.of();
            return List.of(applicant.getManagerId());
        }

        // 按角色（approverIds 存角色 ID 列表）
        if ("role".equalsIgnoreCase(source)) {
            if (node.getApproverIds() == null || node.getApproverIds().isEmpty()) return List.of();
            List<Long> userIds = userRoleMapper.selectListByQuery(
                            QueryWrapper.create()
                                    .where(SysUserRole::getRoleId).in(node.getApproverIds()))
                    .stream().map(SysUserRole::getUserId).distinct().toList();
            return userIds;
        }

        // 默认：直接指定用户
        if (node.getApproverIds() == null || node.getApproverIds().isEmpty()) {
            return List.of();
        }
        return node.getApproverIds();
    }

    private InstanceVO toSimpleVO(ApprovalInstance instance) {
        InstanceVO vo = new InstanceVO();
        vo.setId(instance.getId());
        vo.setInstanceNo(instance.getInstanceNo());
        vo.setTemplateId(instance.getTemplateId());
        vo.setTemplateName(instance.getTemplateName());
        vo.setCategory(instance.getCategory());
        vo.setTitle(instance.getTitle());
        vo.setApplicantId(instance.getApplicantId());
        vo.setApplicantName(instance.getApplicantName());
        vo.setFormData(instance.getFormData());
        vo.setStatus(instance.getStatus());
        vo.setStatusLabel(getStatusLabel(instance.getStatus()));
        vo.setCurrentNode(instance.getCurrentNode());
        vo.setSubmitTime(instance.getSubmitTime());
        vo.setFinishTime(instance.getFinishTime());
        vo.setCreateTime(instance.getCreateTime());
        return vo;
    }

    private TaskVO toTaskVO(ApprovalTask task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setInstanceId(task.getInstanceId());
        vo.setInstanceNo(task.getInstanceNo());
        vo.setTitle(task.getTitle());
        vo.setNodeIndex(task.getNodeIndex());
        vo.setNodeName(task.getNodeName());
        vo.setApproverId(task.getApproverId());
        vo.setApproverName(task.getApproverName());
        vo.setStatus(task.getStatus());
        vo.setStatusLabel(getTaskStatusLabel(task.getStatus()));
        vo.setComment(task.getComment());
        vo.setHandleTime(task.getHandleTime());
        vo.setCreateTime(task.getCreateTime());
        return vo;
    }

    private void ensureCanView(ApprovalInstance instance) {
        if (instance.getApplicantId() != null && instance.getApplicantId().equals(TenantContext.getUserId())) {
            return;
        }
        boolean assignedToCurrentUser = taskMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getInstanceId).eq(instance.getId())
                        .and(ApprovalTask::getApproverId).eq(TenantContext.getUserId())) > 0;
        if (assignedToCurrentUser) {
            return;
        }
        if (roleAuthService.hasPermission(PermissionCodes.APPROVAL_ALL)
                || roleAuthService.hasPermission(PermissionCodes.APPROVAL_INSTANCE_VIEW_ALL)) {
            return;
        }
        throw new BusinessException("无权查看该审批单");
    }

    private String getStatusLabel(String status) {
        return Arrays.stream(ApprovalStatus.values())
                .filter(s -> s.getCode().equals(status))
                .map(ApprovalStatus::getLabel)
                .findFirst().orElse(status);
    }

    private String getTaskStatusLabel(String status) {
        return Arrays.stream(TaskStatus.values())
                .filter(s -> s.getCode().equals(status))
                .map(TaskStatus::getLabel)
                .findFirst().orElse(status);
    }
}
