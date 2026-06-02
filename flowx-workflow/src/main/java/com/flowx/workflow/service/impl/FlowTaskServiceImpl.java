package com.flowx.workflow.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.exception.NotFoundException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.common.util.SecurityUtil;
import com.flowx.workflow.dto.FlowTaskCompleteDTO;
import com.flowx.workflow.dto.FlowTaskQueryDTO;
import com.flowx.workflow.entity.FlowInstance;
import com.flowx.workflow.entity.FlowTask;
import com.flowx.workflow.entity.FlowTaskLog;
import com.flowx.workflow.mapper.FlowInstanceMapper;
import com.flowx.workflow.mapper.FlowTaskLogMapper;
import com.flowx.workflow.mapper.FlowTaskMapper;
import com.flowx.workflow.service.FlowTaskService;
import com.flowx.workflow.vo.FlowTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Flow task service implementation
 * <p>
 * Integrates with Flowable's TaskService for task operations
 * and HistoryService for historical task queries.
 * </p>
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowTaskServiceImpl implements FlowTaskService {

    private final FlowTaskMapper taskMapper;
    private final FlowTaskLogMapper taskLogMapper;
    private final FlowInstanceMapper instanceMapper;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    @Override
    public PageResult<FlowTaskVO> getMyTasks(FlowTaskQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getUserId();
        QueryWrapper wrapper = QueryWrapper.create();

        wrapper.eq("assignee_id", currentUserId);

        if (queryDTO.getInstanceId() != null) {
            wrapper.eq("instance_id", queryDTO.getInstanceId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }

        wrapper.orderBy("create_time", false);
        Page<FlowTask> result = taskMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);

        List<FlowTaskVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<FlowTaskVO> getMyTodoTasks(FlowTaskQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getUserId();
        QueryWrapper wrapper = QueryWrapper.create();

        wrapper.eq("assignee_id", currentUserId);
        wrapper.in("status", 0, 1); // Pending or Claimed

        if (queryDTO.getInstanceId() != null) {
            wrapper.eq("instance_id", queryDTO.getInstanceId());
        }

        wrapper.orderBy("create_time", false);
        Page<FlowTask> result = taskMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);

        List<FlowTaskVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<FlowTaskVO> getMyDoneTasks(FlowTaskQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getUserId();
        QueryWrapper wrapper = QueryWrapper.create();

        wrapper.eq("assignee_id", currentUserId);
        wrapper.eq("status", 2); // Completed

        if (queryDTO.getInstanceId() != null) {
            wrapper.eq("instance_id", queryDTO.getInstanceId());
        }

        wrapper.orderBy("complete_time", false);
        Page<FlowTask> result = taskMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);

        List<FlowTaskVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimTask(Long taskId) {
        AssertUtil.notNull(taskId, "任务ID不能为空");
        Long currentUserId = SecurityUtil.getUserId();

        FlowTask flowTask = taskMapper.selectOneById(taskId);
        AssertUtil.notNull(flowTask, ResultCodeEnum.NOT_FOUND.getCode(), "任务不存在");

        if (flowTask.getStatus() != null && flowTask.getStatus() != 0) {
            throw new BizException("只能认领待处理的任务");
        }

        // Claim in Flowable
        if (StringUtils.hasText(flowTask.getTaskId())) {
            try {
                taskService.claim(flowTask.getTaskId(), String.valueOf(currentUserId));
                log.info("Claimed Flowable task: {}", flowTask.getTaskId());
            } catch (Exception e) {
                log.warn("Failed to claim Flowable task: {}", flowTask.getTaskId(), e);
            }
        }

        // Update task in DB
        flowTask.setAssigneeId(currentUserId);
        flowTask.setClaimTime(LocalDateTime.now());
        flowTask.setStatus(1); // Claimed
        taskMapper.updateById(flowTask);

        // Create task log
        createTaskLog(flowTask, currentUserId, "claim", null);

        log.info("Claimed task: taskId={}, userId={}", taskId, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(FlowTaskCompleteDTO dto) {
        AssertUtil.notNull(dto, "任务完成信息不能为空");
        AssertUtil.notNull(dto.getTaskId(), "任务ID不能为空");
        AssertUtil.notNull(dto.getApproved(), "审批结果不能为空");

        Long currentUserId = SecurityUtil.getUserId();

        FlowTask flowTask = taskMapper.selectOneById(dto.getTaskId());
        AssertUtil.notNull(flowTask, ResultCodeEnum.NOT_FOUND.getCode(), "任务不存在");

        if (flowTask.getStatus() != null && flowTask.getStatus() >= 2) {
            throw new BizException("任务已完成，不能重复操作");
        }

        // Prepare variables
        Map<String, Object> variables = dto.getVariables();
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("approved", dto.getApproved());
        variables.put("comment", dto.getComment());

        // Complete in Flowable
        if (StringUtils.hasText(flowTask.getTaskId())) {
            try {
                if (Boolean.TRUE.equals(dto.getApproved())) {
                    taskService.complete(flowTask.getTaskId(), variables);
                } else {
                    // For rejection, we pass a variable to trigger the rejection flow
                    variables.put("rejected", true);
                    taskService.complete(flowTask.getTaskId(), variables);
                }
                log.info("Completed Flowable task: {}, approved={}", flowTask.getTaskId(), dto.getApproved());
            } catch (Exception e) {
                log.error("Failed to complete Flowable task: {}", flowTask.getTaskId(), e);
                throw new BizException("任务完成失败: " + e.getMessage());
            }
        }

        // Update task in DB
        flowTask.setCompleteTime(LocalDateTime.now());
        flowTask.setStatus(2); // Completed
        flowTask.setComment(dto.getComment());
        taskMapper.updateById(flowTask);

        // Create task log
        String operationType = Boolean.TRUE.equals(dto.getApproved()) ? "approve" : "reject";
        createTaskLog(flowTask, currentUserId, operationType, dto.getComment());

        // Check if process instance is completed
        checkAndCompleteInstance(flowTask.getInstanceId());

        log.info("Completed task: taskId={}, approved={}", dto.getTaskId(), dto.getApproved());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(Long taskId, Long targetUserId) {
        AssertUtil.notNull(taskId, "任务ID不能为空");
        AssertUtil.notNull(targetUserId, "目标用户ID不能为空");

        Long currentUserId = SecurityUtil.getUserId();

        FlowTask flowTask = taskMapper.selectOneById(taskId);
        AssertUtil.notNull(flowTask, ResultCodeEnum.NOT_FOUND.getCode(), "任务不存在");

        if (flowTask.getStatus() != null && flowTask.getStatus() >= 2) {
            throw new BizException("已完成的任务不能转办");
        }

        // Delegate in Flowable
        if (StringUtils.hasText(flowTask.getTaskId())) {
            try {
                taskService.delegateTask(flowTask.getTaskId(), String.valueOf(targetUserId));
                log.info("Delegated Flowable task: {} to user {}", flowTask.getTaskId(), targetUserId);
            } catch (Exception e) {
                log.warn("Failed to delegate Flowable task: {}", flowTask.getTaskId(), e);
            }
        }

        // Update task in DB
        flowTask.setAssigneeId(targetUserId);
        flowTask.setStatus(3); // Delegated
        taskMapper.updateById(flowTask);

        // Create task log
        createTaskLog(flowTask, currentUserId, "delegate", "转办给用户: " + targetUserId);

        log.info("Delegated task: taskId={}, fromUser={}, toUser={}", taskId, currentUserId, targetUserId);
    }

    /**
     * Create task operation log
     */
    private void createTaskLog(FlowTask flowTask, Long operatorId, String operationType, String comment) {
        FlowTaskLog taskLog = new FlowTaskLog();
        taskLog.setInstanceId(flowTask.getInstanceId());
        taskLog.setTaskId(flowTask.getId());
        taskLog.setFlowableTaskId(flowTask.getTaskId());
        taskLog.setOperatorId(operatorId);
        taskLog.setOperationType(operationType);
        taskLog.setComment(comment);
        taskLog.setOperateTime(LocalDateTime.now());
        taskLogMapper.insert(taskLog);
    }

    /**
     * Check if all tasks in an instance are completed, and update instance status if so
     */
    private void checkAndCompleteInstance(Long instanceId) {
        if (instanceId == null) {
            return;
        }

        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("instance_id", instanceId);
        wrapper.in("status", 0, 1); // Pending or Claimed
        Long pendingCount = taskMapper.selectCount(wrapper);

        if (pendingCount == 0) {
            FlowInstance instance = instanceMapper.selectOneById(instanceId);
            if (instance != null && instance.getStatus() == 0) {
                instance.setStatus(1); // Completed
                instance.setEndTime(LocalDateTime.now());
                instanceMapper.updateById(instance);
                log.info("Flow instance completed: {}", instanceId);
            }
        }
    }

    /**
     * Convert entity to VO
     */
    private FlowTaskVO convertToVO(FlowTask task) {
        FlowTaskVO vo = new FlowTaskVO();
        BeanUtils.copyProperties(task, vo);

        // Load instance title
        if (task.getInstanceId() != null) {
            FlowInstance instance = instanceMapper.selectOneById(task.getInstanceId());
            if (instance != null) {
                vo.setInstanceTitle(instance.getTitle());
            }
        }

        return vo;
    }
}
