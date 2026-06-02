package com.flowx.approval.controller;

import com.flowx.approval.dto.ApprovalQueryDTO;
import com.flowx.approval.dto.ApprovalTaskCompleteDTO;
import com.flowx.approval.service.ApprovalTaskService;
import com.flowx.approval.vo.ApprovalTaskVO;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Approval task management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/approval/task")
@RequiredArgsConstructor
public class ApprovalTaskController {

    private final ApprovalTaskService taskService;

    /**
     * Get todo tasks for current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    @GetMapping("/todo")
    public R<PageResult<ApprovalTaskVO>> getTodoTasks(ApprovalQueryDTO queryDTO) {
        PageResult<ApprovalTaskVO> result = taskService.getTodoTasks(queryDTO);
        return R.ok(result);
    }

    /**
     * Get done tasks for current user
     *
     * @param queryDTO query parameters
     * @return paginated task list
     */
    @GetMapping("/done")
    public R<PageResult<ApprovalTaskVO>> getDoneTasks(ApprovalQueryDTO queryDTO) {
        PageResult<ApprovalTaskVO> result = taskService.getDoneTasks(queryDTO);
        return R.ok(result);
    }

    /**
     * Get task detail
     *
     * @param id task ID
     * @return task VO
     */
    @GetMapping("/{id}")
    public R<ApprovalTaskVO> getTaskById(@PathVariable("id") Long id) {
        ApprovalTaskVO vo = taskService.getTaskById(id);
        return R.ok(vo);
    }

    /**
     * Approve a task
     *
     * @param id  task ID
     * @param dto task completion DTO (comment, etc.)
     * @return success response
     */
    @PutMapping("/{id}/approve")
    public R<Void> approve(@PathVariable("id") Long id, @Valid @RequestBody(required = false) ApprovalTaskCompleteDTO dto) {
        taskService.approve(id, dto);
        return R.ok();
    }

    /**
     * Reject a task
     *
     * @param id  task ID
     * @param dto task completion DTO (comment, etc.)
     * @return success response
     */
    @PutMapping("/{id}/reject")
    public R<Void> reject(@PathVariable("id") Long id, @Valid @RequestBody ApprovalTaskCompleteDTO dto) {
        taskService.reject(id, dto);
        return R.ok();
    }

    /**
     * Delegate a task to another user
     *
     * @param id           task ID
     * @param targetUserId target user ID
     * @return success response
     */
    @PutMapping("/{id}/delegate")
    public R<Void> delegate(@PathVariable("id") Long id, @RequestParam("targetUserId") Long targetUserId) {
        taskService.delegate(id, targetUserId);
        return R.ok();
    }
}