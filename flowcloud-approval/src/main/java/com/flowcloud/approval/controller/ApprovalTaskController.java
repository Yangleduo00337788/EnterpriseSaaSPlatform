package com.flowcloud.approval.controller;

import com.flowcloud.approval.dto.TaskCompleteDTO;
import com.flowcloud.approval.service.ApprovalTaskService;
import com.flowcloud.approval.vo.TaskVO;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.common.result.Result;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "审批任务")
@RestController
@RequestMapping("/api/approval/tasks")
@RequiredArgsConstructor
public class ApprovalTaskController {

    private final ApprovalTaskService taskService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "待办任务")
    @GetMapping("/pending")
    public Result<PageResult<TaskVO>> pending(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requireAnyPermission(PermissionCodes.APPROVAL_PENDING, PermissionCodes.APPROVAL_TASK_HANDLE);
        return Result.ok(taskService.pagePendingTasks(pageNum, pageSize));
    }

    @Operation(summary = "已办任务")
    @GetMapping("/handled")
    public Result<PageResult<TaskVO>> handled(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requireAnyPermission(PermissionCodes.APPROVAL_PENDING, PermissionCodes.APPROVAL_TASK_HANDLE);
        return Result.ok(taskService.pageHandledTasks(pageNum, pageSize));
    }

    @Operation(summary = "处理任务")
    @PostMapping("/complete")
    public Result<Void> complete(@Valid @RequestBody TaskCompleteDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.APPROVAL_PENDING, PermissionCodes.APPROVAL_TASK_HANDLE);
        taskService.complete(dto);
        return Result.ok();
    }

    @Operation(summary = "催办任务")
    @PostMapping("/{id}/remind")
    public Result<Void> remind(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.APPROVAL_MY, PermissionCodes.APPROVAL_ALL);
        taskService.remind(id);
        return Result.ok();
    }
}
