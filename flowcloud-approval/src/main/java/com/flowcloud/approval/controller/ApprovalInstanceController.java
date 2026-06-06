package com.flowcloud.approval.controller;

import com.flowcloud.approval.dto.SubmitApprovalDTO;
import com.flowcloud.approval.service.ApprovalInstanceService;
import com.flowcloud.approval.vo.InstanceVO;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.common.result.Result;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "审批实例")
@RestController
@RequestMapping("/api/approval/instances")
@RequiredArgsConstructor
public class ApprovalInstanceController {

    private final ApprovalInstanceService instanceService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "我的申请")
    @GetMapping("/my")
    public Result<PageResult<InstanceVO>> mySubmissions(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requirePermission(PermissionCodes.APPROVAL_MY);
        return Result.ok(instanceService.pageMySubmissions(status, pageNum, pageSize));
    }

    @Operation(summary = "全部审批")
    @GetMapping
    public Result<PageResult<InstanceVO>> pageAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requireAnyPermission(PermissionCodes.APPROVAL_ALL, PermissionCodes.APPROVAL_INSTANCE_VIEW_ALL);
        return Result.ok(instanceService.pageAll(status, category, pageNum, pageSize));
    }

    @Operation(summary = "审批详情")
    @GetMapping("/{id}")
    public Result<InstanceVO> detail(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(
                PermissionCodes.APPROVAL_MY,
                PermissionCodes.APPROVAL_PENDING,
                PermissionCodes.APPROVAL_TASK_HANDLE,
                PermissionCodes.APPROVAL_ALL,
                PermissionCodes.APPROVAL_INSTANCE_VIEW_ALL
        );
        return Result.ok(instanceService.getDetail(id));
    }

    @Operation(summary = "提交审批")
    @PostMapping
    public Result<Long> submit(@Valid @RequestBody SubmitApprovalDTO dto) {
        roleAuthService.requirePermission(PermissionCodes.APPROVAL_SUBMIT);
        return Result.ok(instanceService.submit(dto));
    }

    @Operation(summary = "撤销审批")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        roleAuthService.requirePermission(PermissionCodes.APPROVAL_MY);
        instanceService.cancel(id);
        return Result.ok();
    }
}
