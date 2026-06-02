package com.flowx.approval.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowx.approval.dto.ApprovalQueryDTO;
import com.flowx.approval.dto.ApprovalSubmitDTO;
import com.flowx.approval.entity.ApprovalInstance;
import com.flowx.approval.entity.ApprovalType;
import com.flowx.approval.mapper.ApprovalInstanceMapper;
import com.flowx.approval.mapper.ApprovalTypeMapper;
import com.flowx.approval.service.ApprovalInstanceService;
import com.flowx.approval.vo.ApprovalInstanceVO;
import com.flowx.common.core.enums.ApprovalStatusEnum;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.common.util.SecurityUtil;
import com.flowx.workflow.dto.FlowInstanceDTO;
import com.flowx.workflow.service.FlowDefinitionService;
import com.flowx.workflow.service.FlowInstanceService;
import com.flowx.workflow.vo.FlowDefinitionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Approval instance service implementation
 * <p>
 * Integrates with FlowInstanceService to start and manage workflow processes.
 * </p>
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalInstanceServiceImpl implements ApprovalInstanceService {

    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalTypeMapper typeMapper;
    private final FlowInstanceService flowInstanceService;
    private final FlowDefinitionService flowDefinitionService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(ApprovalSubmitDTO dto) {
        AssertUtil.notNull(dto, "审批信息不能为空");
        AssertUtil.notNull(dto.getTypeId(), "审批类型不能为空");
        AssertUtil.notBlank(dto.getTitle(), "审批标题不能为空");

        Long currentUserId = SecurityUtil.getUserId();

        // Get approval type
        ApprovalType approvalType = typeMapper.selectOneById(dto.getTypeId());
        AssertUtil.notNull(approvalType, ResultCodeEnum.NOT_FOUND.getCode(), "审批类型不存在");

        if (approvalType.getStatus() == null || approvalType.getStatus() != 1) {
            throw new BizException("审批类型已禁用");
        }

        // Get flow definition by key
        FlowDefinitionVO definition = flowDefinitionService.getDefinitionByKey(approvalType.getFlowKey());
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(), "关联的流程定义不存在");

        // Create flow instance via FlowInstanceService
        FlowInstanceDTO flowInstanceDTO = new FlowInstanceDTO();
        flowInstanceDTO.setDefinitionId(definition.getId());
        flowInstanceDTO.setTitle(dto.getTitle());
        flowInstanceDTO.setBusinessType(approvalType.getTypeCode());
        flowInstanceDTO.setVariables(buildProcessVariables(dto, currentUserId));

        Long flowInstanceId = flowInstanceService.startProcess(flowInstanceDTO);

        // Create approval instance
        ApprovalInstance instance = new ApprovalInstance();
        instance.setTypeId(dto.getTypeId());
        instance.setTitle(dto.getTitle());
        instance.setInitiatorId(currentUserId);
        instance.setFlowInstanceId(flowInstanceId);
        instance.setStatus(ApprovalStatusEnum.PENDING.getCode());
        instance.setSubmitTime(LocalDateTime.now());

        if (dto.getUrgencyLevel() != null) {
            instance.setUrgencyLevel(dto.getUrgencyLevel());
        } else {
            instance.setUrgencyLevel(0); // Normal
        }

        // Serialize form data to JSON
        if (!CollectionUtils.isEmpty(dto.getFormData())) {
            try {
                instance.setFormData(objectMapper.writeValueAsString(dto.getFormData()));
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize form data", e);
            }
        }

        instanceMapper.insert(instance);
        log.info("Submitted approval: type={}, title={}, userId={}", approvalType.getTypeName(), dto.getTitle(), currentUserId);
        return instance.getId();
    }

    @Override
    public PageResult<ApprovalInstanceVO> getMyApprovals(ApprovalQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getUserId();
        QueryWrapper wrapper = QueryWrapper.create();

        wrapper.eq("initiator_id", currentUserId);

        if (queryDTO.getTypeId() != null) {
            wrapper.eq("type_id", queryDTO.getTypeId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like("title", queryDTO.getTitle());
        }

        wrapper.orderBy("submit_time", false);
        Page<ApprovalInstance> result = instanceMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);

        List<ApprovalInstanceVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<ApprovalInstanceVO> getPendingApprovals(ApprovalQueryDTO queryDTO) {
        Long currentUserId = SecurityUtil.getUserId();

        // Get task IDs assigned to current user from Flowable
        List<Task> myTasks = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(currentUserId))
                .list();

        if (CollectionUtils.isEmpty(myTasks)) {
            return PageResult.empty();
        }

        // Get process instance IDs from tasks
        List<String> processInstanceIds = myTasks.stream()
                .map(Task::getProcessInstanceId)
                .distinct()
                .collect(Collectors.toList());

        // Query approval instances by process instance IDs
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.in("process_instance_id", processInstanceIds);
        wrapper.eq("status", ApprovalStatusEnum.PENDING.getCode());

        if (queryDTO.getTypeId() != null) {
            wrapper.eq("type_id", queryDTO.getTypeId());
        }
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like("title", queryDTO.getTitle());
        }

        wrapper.orderBy("submit_time", false);
        Page<ApprovalInstance> result = instanceMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);

        List<ApprovalInstanceVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public ApprovalInstanceVO getApprovalDetail(Long id) {
        AssertUtil.notNull(id, "审批实例ID不能为空");
        ApprovalInstance instance = instanceMapper.selectOneById(id);
        AssertUtil.notNull(instance, ResultCodeEnum.APPROVAL_NOT_FOUND.getCode(),
                ResultCodeEnum.APPROVAL_NOT_FOUND.getMessage());

        ApprovalInstanceVO vo = convertToVO(instance);

        // Parse form data JSON
        if (StringUtils.hasText(instance.getFormData())) {
            try {
                Map<String, Object> formData = objectMapper.readValue(
                        instance.getFormData(),
                        new TypeReference<Map<String, Object>>() {}
                );
                vo.setFormData(formData);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse form data for approval {}", id, e);
            }
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long id) {
        AssertUtil.notNull(id, "审批实例ID不能为空");
        Long currentUserId = SecurityUtil.getUserId();

        ApprovalInstance instance = instanceMapper.selectOneById(id);
        AssertUtil.notNull(instance, ResultCodeEnum.APPROVAL_NOT_FOUND.getCode(),
                ResultCodeEnum.APPROVAL_NOT_FOUND.getMessage());

        // Only initiator can withdraw
        if (!currentUserId.equals(instance.getInitiatorId())) {
            throw new BizException("只有发起人才能撤回审批");
        }

        if (instance.getStatus() != null && instance.getStatus() != ApprovalStatusEnum.PENDING.getCode()) {
            throw new BizException("只有待审批的记录才能撤回");
        }

        // Cancel the flow instance
        if (instance.getFlowInstanceId() != null) {
            try {
                flowInstanceService.cancelInstance(instance.getFlowInstanceId());
            } catch (Exception e) {
                log.warn("Failed to cancel flow instance: {}", instance.getFlowInstanceId(), e);
            }
        }

        // Update approval status
        instance.setStatus(ApprovalStatusEnum.WITHDRAWN.getCode());
        instance.setCompleteTime(LocalDateTime.now());
        instanceMapper.updateById(instance);

        log.info("Withdrawn approval: id={}, userId={}", id, currentUserId);
    }

    @Override
    public void remind(Long id) {
        AssertUtil.notNull(id, "审批实例ID不能为空");
        Long currentUserId = SecurityUtil.getUserId();

        ApprovalInstance instance = instanceMapper.selectOneById(id);
        AssertUtil.notNull(instance, ResultCodeEnum.APPROVAL_NOT_FOUND.getCode(),
                ResultCodeEnum.APPROVAL_NOT_FOUND.getMessage());

        if (instance.getStatus() != null && instance.getStatus() != ApprovalStatusEnum.PENDING.getCode()) {
            throw new BizException("只有待审批的记录才能催办");
        }

        // Get current assignee from Flowable task
        if (StringUtils.hasText(instance.getProcessInstanceId())) {
            try {
                List<Task> tasks = taskService.createTaskQuery()
                        .processInstanceId(instance.getProcessInstanceId())
                        .list();

                for (Task task : tasks) {
                    String assigneeId = task.getAssignee();
                    if (StringUtils.hasText(assigneeId)) {
                        log.info("Sending reminder to user {} for approval {}", assigneeId, id);
                        // TODO: Integrate with message service to send notification
                        // messageService.sendReminder(Long.parseLong(assigneeId), "您有一条待审批的记录需要处理: " + instance.getTitle());
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to send reminder for approval {}", id, e);
            }
        }

        log.info("Sent reminder for approval: id={}, userId={}", id, currentUserId);
    }

    /**
     * Build process variables from approval submit DTO
     */
    private Map<String, Object> buildProcessVariables(ApprovalSubmitDTO dto, Long userId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiatorId", userId);
        variables.put("title", dto.getTitle());
        variables.put("typeId", dto.getTypeId());

        if (!CollectionUtils.isEmpty(dto.getFormData())) {
            variables.putAll(dto.getFormData());
        }

        return variables;
    }

    /**
     * Convert entity to VO
     */
    private ApprovalInstanceVO convertToVO(ApprovalInstance instance) {
        ApprovalInstanceVO vo = new ApprovalInstanceVO();
        BeanUtils.copyProperties(instance, vo);

        // Load type name
        if (instance.getTypeId() != null) {
            ApprovalType type = typeMapper.selectOneById(instance.getTypeId());
            if (type != null) {
                vo.setTypeName(type.getTypeName());
            }
        }

        return vo;
    }
}
