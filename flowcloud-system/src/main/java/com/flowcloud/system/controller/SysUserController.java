package com.flowcloud.system.controller;

import com.flowcloud.common.result.PageResult;
import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.UserDTO;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.service.SysUserService;
import com.flowcloud.system.service.UserImportExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.flowcloud.system.vo.UserOptionVO;

import java.util.List;
import java.util.Map;

@Tag(name = "员工管理")
@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;
    private final RoleAuthService roleAuthService;
    private final UserImportExportService userImportExportService;

    @Operation(summary = "员工选项（选人用）")
    @GetMapping("/options")
    public Result<List<UserOptionVO>> options() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_VIEW,
                PermissionCodes.SYSTEM_DEPT, PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        return Result.ok(userService.listOptions());
    }

    @Operation(summary = "员工列表")
    @GetMapping
    public Result<PageResult<UserVO>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_VIEW);
        return Result.ok(userService.pageUsers(keyword, deptId, pageNum, pageSize));
    }

    @Operation(summary = "员工详情")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_VIEW);
        return Result.ok(userService.getById(id));
    }

    @Operation(summary = "创建员工")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        userService.createUser(dto);
        return Result.ok();
    }

    @Operation(summary = "更新员工")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        dto.setId(id);
        userService.updateUser(dto);
        return Result.ok();
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        userService.deleteUser(id);
        return Result.ok();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        userService.resetPassword(id, body.get("password"));
        return Result.ok();
    }

    @Operation(summary = "切换启用/禁用状态")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        userService.toggleStatus(id);
        return Result.ok();
    }

    @Operation(summary = "导出员工 Excel")
    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) throws java.io.IOException {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        userImportExportService.exportUsers(response);
    }

    @Operation(summary = "导入员工 Excel")
    @PostMapping("/import")
    public Result<Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) throws java.io.IOException {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_USER, PermissionCodes.SYSTEM_USER_EDIT);
        return Result.ok(userImportExportService.importUsers(file));
    }
}
