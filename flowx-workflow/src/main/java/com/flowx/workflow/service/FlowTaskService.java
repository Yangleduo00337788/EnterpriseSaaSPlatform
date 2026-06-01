package com.flowx.workflow.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.workflow.dto.FlowTaskCompleteDTO;
import com.flowx.workflow.dto.FlowTaskQueryDTO;
import com.flowx.workflow.vo.FlowTaskVO;

/**
 * Flow task service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface FlowTaskService {

    /**
     * Get tasks assigned to current user with pagination
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    PageResult<FlowTaskVO> getMyTasks(FlowTaskQueryDTO queryDTO);

    /**
     * Get todo tasks for current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    PageResult<FlowTaskVO> getMyTodoTasks(FlowTaskQueryDTO queryDTO);

    /**
     * Get done tasks for current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    PageResult<FlowTaskVO> getMyDoneTasks(FlowTaskQueryDTO queryDTO);

    /**
     * Claim a task
     *
     * @param taskId task ID
     */
    void claimTask(Long taskId);

    /**
     * Complete a task with variables and comment
     *
     * @param dto task complete DTO
     */
    void completeTask(FlowTaskCompleteDTO dto);

    /**
     * Delegate a task to another user
     *
     * @param taskId     task ID
     * @param targetUserId target user ID
     */
    void delegateTask(Long taskId, Long targetUserId);
}
