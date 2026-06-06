package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.DeptDTO;
import com.flowcloud.system.service.DeptService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.DeptVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "组织架构")
@RestController
@RequestMapping("/api/system/depts")
@RequiredArgsConstructor
public class SysDeptController {

    private final DeptService deptService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "部门树")
    @GetMapping
    public Result<List<DeptVO>> listTree() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DEPT, PermissionCodes.SYSTEM_DEPT_VIEW);
        return Result.ok(deptService.listTree());
    }

    @Operation(summary = "新增部门")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody DeptDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DEPT, PermissionCodes.SYSTEM_DEPT_EDIT);
        deptService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DeptDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DEPT, PermissionCodes.SYSTEM_DEPT_EDIT);
        dto.setId(id);
        deptService.update(dto);
        return Result.ok();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DEPT, PermissionCodes.SYSTEM_DEPT_EDIT);
        deptService.delete(id);
        return Result.ok();
    }
}