package com.flowx.approval.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flowx.approval.entity.ApprovalInstance;
import com.flowx.approval.mapper.ApprovalInstanceMapper;
import com.flowx.common.core.enums.ApprovalStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * Approval process listener
 * <p>
 * Implements Flowable's ExecutionListener and TaskListener to update
 * ApprovalInstance status when tasks are completed or process ends.
 * </p>
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Component("approvalProcessListener")
@RequiredArgsConstructor
public class ApprovalProcessListener implements ExecutionListener, TaskListener {

    private final ApprovalInstanceMapper approvalInstanceMapper;

    @Override
    public void notify(DelegateExecution execution) {
        // Execution listener - triggered on process events
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();

        log.info("ApprovalProcessListener execution event: {}, processInstanceId={}", eventName, processInstanceId);

        if ("end".equals(eventName)) {
            // Process ended - update approval instance
            handleProcessEnd(execution);
        }
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Task listener - triggered on task events
        String eventName = delegateTask.getEventName();
        String taskId = delegateTask.getId();
        String processInstanceId = delegateTask.getProcessInstanceId();

        log.info("ApprovalProcessListener task event: {}, taskId={}, processInstanceId={}",
                eventName, taskId, processInstanceId);

        if ("complete".equals(eventName)) {
            handleTaskComplete(delegateTask);
        }
    }

    /**
     * Handle task completion - update approval instance status based on task variables
     */
    private void handleTaskComplete(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();

        // Find approval instance by process instance ID
        QueryWrapper<ApprovalInstance> wrapper = new QueryWrapper<>();
        wrapper.eq("process_instance_id", processInstanceId);
        ApprovalInstance approvalInstance = approvalInstanceMapper.selectOne(wrapper);

        if (approvalInstance == null) {
            log.warn("Approval instance not found for processInstanceId={}", processInstanceId);
            return;
        }

        // Check if rejected
        Object rejectedVar = delegateTask.getVariable("rejected");
        Object approvedVar = delegateTask.getVariable("approved");

        boolean rejected = rejectedVar != null && Boolean.TRUE.equals(rejectedVar);
        boolean approved = approvedVar != null && Boolean.TRUE.equals(approvedVar);

        if (rejected) {
            approvalInstance.setStatus(ApprovalStatusEnum.REJECTED.getCode());
            approvalInstance.setCompleteTime(LocalDateTime.now());
            approvalInstanceMapper.updateById(approvalInstance);
            log.info("Approval instance {} rejected", approvalInstance.getId());
        } else if (approved) {
            approvalInstance.setStatus(ApprovalStatusEnum.APPROVED.getCode());
            approvalInstance.setCompleteTime(LocalDateTime.now());
            approvalInstanceMapper.updateById(approvalInstance);
            log.info("Approval instance {} approved", approvalInstance.getId());
        }
    }

    /**
     * Handle process end - set completeTime if not already set
     */
    private void handleProcessEnd(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();

        QueryWrapper<ApprovalInstance> wrapper = new QueryWrapper<>();
        wrapper.eq("process_instance_id", processInstanceId);
        ApprovalInstance approvalInstance = approvalInstanceMapper.selectOne(wrapper);

        if (approvalInstance == null) {
            log.warn("Approval instance not found for processInstanceId={}", processInstanceId);
            return;
        }

        // Set complete time if not already set
        if (approvalInstance.getCompleteTime() == null) {
            approvalInstance.setCompleteTime(LocalDateTime.now());
        }

        // Check process end state from variables
        Object approvedVar = execution.getVariable("approved");
        if (approvedVar != null && Boolean.TRUE.equals(approvedVar)) {
            approvalInstance.setStatus(ApprovalStatusEnum.APPROVED.getCode());
        }

        approvalInstanceMapper.updateById(approvalInstance);
        log.info("Approval instance {} process ended with status: {}",
                approvalInstance.getId(), approvalInstance.getStatus());
    }
}
