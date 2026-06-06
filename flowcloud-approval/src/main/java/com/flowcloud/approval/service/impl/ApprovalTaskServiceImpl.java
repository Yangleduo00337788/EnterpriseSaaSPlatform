package com.flowcloud.approval.service.impl;

import cn.hutool.json.JSONUtil;
import com.flowcloud.approval.dto.FlowNodeDTO;
import com.flowcloud.approval.dto.TaskCompleteDTO;
import com.flowcloud.approval.entity.*;
import com.flowcloud.approval.enums.ApprovalStatus;
import com.flowcloud.approval.enums.TaskStatus;
import com.flowcloud.approval.mapper.*;
import com.flowcloud.approval.service.ApprovalTaskService;
import com.flowcloud.approval.vo.TaskVO;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.event.ApprovalEvent;
import com.flowcloud.common.event.AuditEvent;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.service.RoleAuthService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalTaskServiceImpl implements ApprovalTaskService {

    private final ApprovalTaskMapper taskMapper;
    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalRecordMapper recordMapper;
    private final SysUserMapper userMapper;
    private final ApprovalInstanceServiceImpl instanceService;
    private final ApplicationEventPublisher eventPublisher;
    private final RoleAuthService roleAuthService;

    @Override
    public PageResult<TaskVO> pagePendingTasks(int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(ApprovalTask::getTenantId).eq(TenantContext.getTenantId())
                .and(ApprovalTask::getApproverId).eq(TenantContext.getUserId())
                .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode());
        query.orderBy(ApprovalTask::getCreateTime, false);
        Page<ApprovalTask> page = taskMapper.paginate(pageNum, pageSize, query);
        List<TaskVO> vos = page.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(vos, page.getTotalRow(), pageNum, pageSize);
    }

    @Override
    public PageResult<TaskVO> pageHandledTasks(int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(ApprovalTask::getTenantId).eq(TenantContext.getTenantId())
                .and(ApprovalTask::getApproverId).eq(TenantContext.getUserId())
                .and(ApprovalTask::getStatus).ne(TaskStatus.PENDING.getCode());
        query.orderBy(ApprovalTask::getHandleTime, false);
        Page<ApprovalTask> page = taskMapper.paginate(pageNum, pageSize, query);
        List<TaskVO> vos = page.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(vos, page.getTotalRow(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(TaskCompleteDTO dto) {
        ApprovalTask task = taskMapper.selectOneById(dto.getTaskId());
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!task.getApproverId().equals(TenantContext.getUserId())) {
            throw new BusinessException("无权处理此任务");
        }
        if (!TaskStatus.PENDING.getCode().equals(task.getStatus())) {
            throw new BusinessException("任务已处理");
        }

        SysUser operator = userMapper.selectOneById(TenantContext.getUserId());
        ApprovalInstance instance = instanceMapper.selectOneById(task.getInstanceId());
        if (instance == null || !ApprovalStatus.PENDING.getCode().equals(instance.getStatus())) {
            throw new BusinessException("审批单状态异常");
        }

        String action = dto.getAction();
        boolean approved = "approve".equals(action);
        task.setStatus(approved ? TaskStatus.APPROVED.getCode() : TaskStatus.REJECTED.getCode());
        task.setComment(dto.getComment());
        task.setHandleTime(LocalDateTime.now());
        taskMapper.update(task);

        ApprovalRecord record = new ApprovalRecord();
        record.setTenantId(TenantContext.getTenantId());
        record.setInstanceId(instance.getId());
        record.setNodeIndex(task.getNodeIndex());
        record.setNodeName(task.getNodeName());
        record.setOperatorId(operator.getId());
        record.setOperatorName(operator.getRealName());
        record.setAction(action);
        record.setComment(dto.getComment());
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);

        // 驳回：终止流程
        if (!approved) {
            instance.setStatus(ApprovalStatus.REJECTED.getCode());
            instance.setFinishTime(LocalDateTime.now());
            instanceMapper.update(instance);
            cancelPendingTasks(instance.getId());
            // 通知申请人
            eventPublisher.publishEvent(new ApprovalEvent(
                    this, ApprovalEvent.Type.REJECTED,
                    instance.getTenantId(), instance.getApplicantId(),
                    instance.getTitle(), instance.getId(), instance.getCategory(),
                    operator.getRealName(), dto.getComment()
            ));
            eventPublisher.publishEvent(AuditEvent.of(
                    operator.getId(), instance.getTenantId(),
                    "REJECT_TASK", "approval",
                    "驳回审批[" + instance.getTitle() + "]，意见：" + dto.getComment(), null));
            return;
        }

        // 同意：判断节点模式决定是否推进（使用提交时固化的快照，不依赖当前模板）
        String configJson = org.springframework.util.StringUtils.hasText(instance.getFlowConfigSnapshot())
                ? instance.getFlowConfigSnapshot() : "";
        List<FlowNodeDTO> flowNodes = JSONUtil.toList(configJson, FlowNodeDTO.class);
        FlowNodeDTO currentNode = flowNodes.stream()
                .filter(n -> n.getIndex() != null && n.getIndex() == task.getNodeIndex())
                .findFirst()
                .orElse(null);

        String nodeMode = currentNode != null ? currentNode.getNodeMode() : null;
        boolean shouldAdvance;

        if ("or-sign".equalsIgnoreCase(nodeMode)) {
            // 或签：任意一人同意即推进，取消本节点剩余任务
            cancelPendingTasksForNode(instance.getId(), task.getNodeIndex());
            shouldAdvance = true;
        } else {
            // 顺序/会签：需要本节点全部任务完成（且无驳回）
            long pendingCount = taskMapper.selectCountByQuery(
                    QueryWrapper.create()
                            .where(ApprovalTask::getInstanceId).eq(instance.getId())
                            .and(ApprovalTask::getNodeIndex).eq(task.getNodeIndex())
                            .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode()));
            shouldAdvance = (pendingCount == 0);
        }

        if (!shouldAdvance) {
            return;
        }

        int nextNodeIndex = task.getNodeIndex() + 1;
        if (nextNodeIndex < flowNodes.size()) {
            instanceService.createTasksForNode(instance, flowNodes.get(nextNodeIndex), nextNodeIndex);
        } else {
            // 最后一个节点完成 → 全部通过
            instance.setStatus(ApprovalStatus.APPROVED.getCode());
            instance.setFinishTime(LocalDateTime.now());
            instanceMapper.update(instance);
            // 通知申请人
            eventPublisher.publishEvent(new ApprovalEvent(
                    this, ApprovalEvent.Type.APPROVED,
                    instance.getTenantId(), instance.getApplicantId(),
                    instance.getTitle(), instance.getId(), instance.getCategory(),
                    operator.getRealName(), null
            ));
            eventPublisher.publishEvent(AuditEvent.of(
                    operator.getId(), instance.getTenantId(),
                    "APPROVE_TASK", "approval",
                    "审批通过[" + instance.getTitle() + "]", null));
        }
    }

    @Override
    public void remind(Long taskId) {
        ApprovalTask task = taskMapper.selectOneById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        ApprovalInstance instance = instanceMapper.selectOneById(task.getInstanceId());
        if (instance == null) {
            throw new BusinessException("审批单不存在");
        }
        Long currentUserId = TenantContext.getUserId();
        boolean isApplicant = instance.getApplicantId().equals(currentUserId);
        boolean isAdmin = roleAuthService.isAdmin();
        if (!isApplicant && !isAdmin) {
            throw new BusinessException("仅申请人或管理员可催办");
        }
        SysUser operator = userMapper.selectOneById(currentUserId);
        doRemind(task, instance, operator != null ? operator.getRealName() : "系统", currentUserId);
    }

    @Override
    public void remindAuto(Long taskId) {
        ApprovalTask task = taskMapper.selectOneById(taskId);
        if (task == null || !TaskStatus.PENDING.getCode().equals(task.getStatus())) {
            return;
        }
        ApprovalInstance instance = instanceMapper.selectOneById(task.getInstanceId());
        if (instance == null || !ApprovalStatus.PENDING.getCode().equals(instance.getStatus())) {
            return;
        }
        doRemind(task, instance, "系统自动催办", null);
    }

    private void doRemind(ApprovalTask task, ApprovalInstance instance, String operatorName, Long operatorId) {
        if (!TaskStatus.PENDING.getCode().equals(task.getStatus())) {
            throw new BusinessException("任务已处理，无需催办");
        }
        if (!ApprovalStatus.PENDING.getCode().equals(instance.getStatus())) {
            throw new BusinessException("审批单状态异常");
        }
        eventPublisher.publishEvent(new ApprovalEvent(
                this, ApprovalEvent.Type.REMIND,
                instance.getTenantId(), task.getApproverId(),
                instance.getTitle(), instance.getId(), instance.getCategory(),
                operatorName, null));
        if (operatorId != null) {
            eventPublisher.publishEvent(AuditEvent.of(
                    operatorId, instance.getTenantId(),
                    "REMIND_TASK", "approval",
                    "催办审批[" + instance.getTitle() + "]，审批人：" + task.getApproverName(), null));
        }
    }

    private void cancelPendingTasks(Long instanceId) {
        List<ApprovalTask> pendingTasks = taskMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getInstanceId).eq(instanceId)
                        .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode()));
        for (ApprovalTask t : pendingTasks) {
            t.setStatus(TaskStatus.REJECTED.getCode());
            t.setHandleTime(LocalDateTime.now());
            taskMapper.update(t);
        }
    }

    private void cancelPendingTasksForNode(Long instanceId, int nodeIndex) {
        List<ApprovalTask> pendingTasks = taskMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getInstanceId).eq(instanceId)
                        .and(ApprovalTask::getNodeIndex).eq(nodeIndex)
                        .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode()));
        for (ApprovalTask t : pendingTasks) {
            t.setStatus(TaskStatus.REJECTED.getCode());
            t.setHandleTime(LocalDateTime.now());
            taskMapper.update(t);
        }
    }

    private TaskVO toVO(ApprovalTask task) {
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
        vo.setStatusLabel(Arrays.stream(TaskStatus.values())
                .filter(s -> s.getCode().equals(task.getStatus()))
                .map(TaskStatus::getLabel)
                .findFirst().orElse(task.getStatus()));
        vo.setComment(task.getComment());
        vo.setHandleTime(task.getHandleTime());
        vo.setCreateTime(task.getCreateTime());
        return vo;
    }
}