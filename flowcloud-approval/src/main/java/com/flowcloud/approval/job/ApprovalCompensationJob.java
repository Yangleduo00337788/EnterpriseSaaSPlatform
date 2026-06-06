package com.flowcloud.approval.job;

import com.flowcloud.approval.entity.ApprovalInstance;
import com.flowcloud.approval.entity.ApprovalTask;
import com.flowcloud.approval.enums.ApprovalStatus;
import com.flowcloud.approval.enums.TaskStatus;
import com.flowcloud.approval.mapper.ApprovalInstanceMapper;
import com.flowcloud.approval.mapper.ApprovalTaskMapper;
import com.flowcloud.approval.service.ApprovalTaskService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalCompensationJob {

    private final ApprovalTaskMapper taskMapper;
    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalTaskService taskService;

    @Value("${flowcloud.approval.remind-hours:48}")
    private int remindHours;

    @Value("${flowcloud.approval.timeout-days:7}")
    private int timeoutDays;

    @Scheduled(cron = "${flowcloud.approval.compensation-cron:0 0 */2 * * ?}")
    public void compensatePendingTasks() {
        LocalDateTime remindBefore = LocalDateTime.now().minusHours(remindHours);
        List<ApprovalTask> overdueTasks = taskMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode())
                        .and(ApprovalTask::getCreateTime).le(remindBefore));
        for (ApprovalTask task : overdueTasks) {
            try {
                taskService.remindAuto(task.getId());
            } catch (Exception e) {
                log.debug("自动催办跳过 taskId={}: {}", task.getId(), e.getMessage());
            }
        }

        LocalDateTime timeoutBefore = LocalDateTime.now().minusDays(timeoutDays);
        List<ApprovalInstance> timeoutInstances = instanceMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ApprovalInstance::getStatus).eq(ApprovalStatus.PENDING.getCode())
                        .and(ApprovalInstance::getSubmitTime).le(timeoutBefore));
        for (ApprovalInstance instance : timeoutInstances) {
            instance.setStatus(ApprovalStatus.CANCELLED.getCode());
            instance.setFinishTime(LocalDateTime.now());
            instanceMapper.update(instance);
            List<ApprovalTask> pendingTasks = taskMapper.selectListByQuery(
                    QueryWrapper.create()
                            .where(ApprovalTask::getInstanceId).eq(instance.getId())
                            .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode()));
            for (ApprovalTask task : pendingTasks) {
                task.setStatus(TaskStatus.REJECTED.getCode());
                task.setComment("超时自动关闭");
                task.setHandleTime(LocalDateTime.now());
                taskMapper.update(task);
            }
            log.info("超时关闭审批实例 instanceId={}", instance.getId());
        }
    }
}
