package com.flowx.workflow.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.workflow.dto.FlowTaskCompleteDTO;
import com.flowx.workflow.dto.FlowTaskQueryDTO;
import com.flowx.workflow.service.FlowTaskService;
import com.flowx.workflow.vo.FlowTaskVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Flow task management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/workflow/task")
@RequiredArgsConstructor
public class FlowTaskController {

    private final FlowTaskService taskService;

    /**
     * Get tasks assigned to current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    @GetMapping("/my")
    public R<PageResult<FlowTaskVO>> getMyTasks(FlowTaskQueryDTO queryDTO) {
        PageResult<FlowTaskVO> result = taskService.getMyTasks(queryDTO);
        return R.ok(result);
    }

    /**
     * Get todo tasks for current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    @GetMapping("/todo")
    public R<PageResult<FlowTaskVO>> getMyTodoTasks(FlowTaskQueryDTO queryDTO) {
        PageResult<FlowTaskVO> result = taskService.getMyTodoTasks(queryDTO);
        return R.ok(result);
    }

    /**
     * Get done tasks for current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    @GetMapping("/done")
    public R<PageResult<FlowTaskVO>> getMyDoneTasks(FlowTaskQueryDTO queryDTO) {
        PageResult<FlowTaskVO> result = taskService.getMyDoneTasks(queryDTO);
        return R.ok(result);
    }

    /**
     * Claim a task
     *
     * @param taskId task ID
     * @return success response
     */
    @PutMapping("/{taskId}/claim")
    public R<Void> claimTask(@PathVariable("taskId") Long taskId) {
        taskService.claimTask(taskId);
        return R.ok();
    }

    /**
     * Complete a task
     *
     * @param dto task complete DTO
     * @return success response
     */
    @PostMapping("/complete")
    public R<Void> completeTask(@Valid @RequestBody FlowTaskCompleteDTO dto) {
        taskService.completeTask(dto);
        return R.ok();
    }

    /**
     * Delegate a task to another user
     *
     * @param taskId       task ID
     * @param targetUserId target user ID
     * @return success response
     */
    @PutMapping("/{taskId}/delegate")
    public R<Void> delegateTask(@PathVariable("taskId") Long taskId,
                                @RequestParam("targetUserId") Long targetUserId) {
        taskService.delegateTask(taskId, targetUserId);
        return R.ok();
    }
}