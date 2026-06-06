package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.RoleDTO;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.service.RoleService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.RoleOptionVO;
import com.flowcloud.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final RoleService roleService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "角色选项")
    @GetMapping("/options")
    public Result<List<RoleOptionVO>> options() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_ROLE_VIEW);
        return Result.ok(roleService.listOptions());
    }

    @Operation(summary = "角色列表")
    @GetMapping
    public Result<List<RoleVO>> list() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_ROLE_VIEW);
        return Result.ok(roleService.listAll());
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_ROLE_VIEW);
        return Result.ok(roleService.getById(id));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody RoleDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_ROLE_EDIT);
        roleService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_ROLE_EDIT);
        dto.setId(id);
        roleService.update(dto);
        return Result.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_ROLE_EDIT);
        roleService.delete(id);
        return Result.ok();
    }
}
