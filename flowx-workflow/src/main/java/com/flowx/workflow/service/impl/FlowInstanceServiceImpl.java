package com.flowx.workflow.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.common.util.SecurityUtil;
import com.flowx.workflow.dto.FlowInstanceDTO;
import com.flowx.workflow.dto.FlowInstanceQueryDTO;
import com.flowx.workflow.entity.FlowDefinition;
import com.flowx.workflow.entity.FlowInstance;
import com.flowx.workflow.entity.FlowTask;
import com.flowx.workflow.mapper.FlowDefinitionMapper;
import com.flowx.workflow.mapper.FlowInstanceMapper;
import com.flowx.workflow.mapper.FlowTaskLogMapper;
import com.flowx.workflow.mapper.FlowTaskMapper;
import com.flowx.workflow.service.FlowInstanceService;
import com.flowx.workflow.vo.FlowInstanceVO;
import com.flowx.workflow.vo.FlowTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Flow instance service implementation
 * <p>
 * Integrates with Flowable's RuntimeService for process instance management
 * and HistoryService for historical data queries.
 * </p>
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowInstanceServiceImpl implements FlowInstanceService {

    private final FlowInstanceMapper instanceMapper;
    private final FlowDefinitionMapper definitionMapper;
    private final FlowTaskMapper taskMapper;
    private final FlowTaskLogMapper taskLogMapper;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long startProcess(FlowInstanceDTO dto) {
        AssertUtil.notNull(dto, "流程实例信息不能为空");
        AssertUtil.notNull(dto.getDefinitionId(), "流程定义ID不能为空");
        AssertUtil.notBlank(dto.getTitle(), "流程标题不能为空");

        // Get definition
        FlowDefinition definition = definitionMapper.selectOneById(dto.getDefinitionId());
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());

        if (definition.getStatus() == null || definition.getStatus() != 1) {
            throw new BizException("流程定义未激活，无法启动流程");
        }

        Long currentUserId = SecurityUtil.getUserId();

        // Prepare process variables
        Map<String, Object> variables = dto.getVariables();
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("initiatorId", currentUserId);
        variables.put("title", dto.getTitle());

        // Start process in Flowable
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                definition.getDefinitionKey(),
                dto.getBusinessKey(),
                variables
        );

        log.info("Started Flowable process instance: processInstanceId={}, key={}",
                processInstance.getId(), definition.getDefinitionKey());

        // Save instance to DB
        FlowInstance instance = new FlowInstance();
        instance.setDefinitionId(dto.getDefinitionId());
        instance.setBusinessKey(dto.getBusinessKey());
        instance.setBusinessType(dto.getBusinessType());
        instance.setTitle(dto.getTitle());
        instance.setInitiatorId(currentUserId);
        instance.setProcessInstanceId(processInstance.getId());
        instance.setStartTime(LocalDateTime.now());
        instance.setStatus(0); // Running

        // Serialize variables to JSON
        if (!CollectionUtils.isEmpty(dto.getVariables())) {
            try {
                instance.setVariables(objectMapper.writeValueAsString(dto.getVariables()));
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize process variables", e);
            }
        }

        instanceMapper.insert(instance);

        // Create task records for current active tasks
        List<org.flowable.task.api.Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();
        for (org.flowable.task.api.Task task : activeTasks) {
            FlowTask flowTask = new FlowTask();
            flowTask.setInstanceId(instance.getId());
            flowTask.setTaskId(task.getId());
            flowTask.setTaskName(task.getName());
            flowTask.setTaskKey(task.getTaskDefinitionKey());
            flowTask.setAssigneeId(task.getAssignee() != null ? Long.parseLong(task.getAssignee()) : null);
            flowTask.setStatus(0); // Pending
            taskMapper.insert(flowTask);
        }

        log.info("Saved flow instance: instanceId={}", instance.getId());
        return instance.getId();
    }

    @Override
    public PageResult<FlowInstanceVO> getInstances(FlowInstanceQueryDTO queryDTO) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (queryDTO.getDefinitionId() != null) {
            wrapper.eq("definition_id", queryDTO.getDefinitionId());
        }
        if (queryDTO.getInitiatorId() != null) {
            wrapper.eq("initiator_id", queryDTO.getInitiatorId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like("title", queryDTO.getTitle());
        }

        wrapper.orderBy("create_time", false);
        Page<FlowInstance> result = instanceMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);

        List<FlowInstanceVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public FlowInstanceVO getInstanceDetail(Long instanceId) {
        AssertUtil.notNull(instanceId, "流程实例ID不能为空");
        FlowInstance instance = instanceMapper.selectOneById(instanceId);
        AssertUtil.notNull(instance, ResultCodeEnum.WORKFLOW_INSTANCE_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_INSTANCE_NOT_FOUND.getMessage());

        FlowInstanceVO vo = convertToVO(instance);

        // Load task history
        QueryWrapper taskWrapper = QueryWrapper.create();
        taskWrapper.eq("instance_id", instanceId);
        taskWrapper.orderBy("create_time", true);
        List<FlowTask> tasks = taskMapper.selectList(taskWrapper);

        List<FlowTaskVO> taskHistory = tasks.stream()
                .map(this::convertTaskToVO)
                .collect(Collectors.toList());
        vo.setTaskHistory(taskHistory);

        // Parse variables JSON
        if (StringUtils.hasText(instance.getVariables())) {
            try {
                Map<String, Object> variables = objectMapper.readValue(
                        instance.getVariables(),
                        new TypeReference<Map<String, Object>>() {}
                );
                vo.setVariables(variables);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse process variables for instance {}", instanceId, e);
            }
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelInstance(Long instanceId) {
        AssertUtil.notNull(instanceId, "流程实例ID不能为空");
        FlowInstance instance = instanceMapper.selectOneById(instanceId);
        AssertUtil.notNull(instance, ResultCodeEnum.WORKFLOW_INSTANCE_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_INSTANCE_NOT_FOUND.getMessage());

        if (instance.getStatus() != null && instance.getStatus() != 0) {
            throw new BizException("只有运行中的流程才能取消");
        }

        // Cancel in Flowable
        if (StringUtils.hasText(instance.getProcessInstanceId())) {
            try {
                runtimeService.deleteProcessInstance(instance.getProcessInstanceId(), "用户取消");
                log.info("Cancelled Flowable process instance: {}", instance.getProcessInstanceId());
            } catch (Exception e) {
                log.warn("Failed to cancel Flowable process instance: {}", instance.getProcessInstanceId(), e);
            }
        }

        // Update instance status
        instance.setStatus(3); // Cancelled
        instance.setEndTime(LocalDateTime.now());
        instanceMapper.updateById(instance);

        // Update pending tasks
        QueryWrapper taskWrapper = QueryWrapper.create();
        taskWrapper.eq("instance_id", instanceId);
        taskWrapper.in("status", 0, 1); // Pending or Claimed
        List<FlowTask> pendingTasks = taskMapper.selectList(taskWrapper);
        for (FlowTask task : pendingTasks) {
            task.setStatus(2); // Mark as completed (cancelled)
            task.setCompleteTime(LocalDateTime.now());
            task.setComment("流程已取消");
            taskMapper.updateById(task);
        }

        log.info("Cancelled flow instance: {}", instanceId);
    }

    /**
     * Convert entity to VO
     */
    private FlowInstanceVO convertToVO(FlowInstance instance) {
        FlowInstanceVO vo = new FlowInstanceVO();
        BeanUtils.copyProperties(instance, vo);

        // Load definition name
        if (instance.getDefinitionId() != null) {
            FlowDefinition definition = definitionMapper.selectOneById(instance.getDefinitionId());
            if (definition != null) {
                vo.setDefinitionName(definition.getDefinitionName());
            }
        }

        return vo;
    }

    /**
     * Convert task entity to VO
     */
    private FlowTaskVO convertTaskToVO(FlowTask task) {
        FlowTaskVO vo = new FlowTaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }
}
