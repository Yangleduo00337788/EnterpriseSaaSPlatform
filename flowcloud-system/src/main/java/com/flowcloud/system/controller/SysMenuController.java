package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.MenuDTO;
import com.flowcloud.system.service.MenuService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.MenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class SysMenuController {

    private final MenuService menuService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "菜单树")
    @GetMapping
    public Result<List<MenuVO>> listTree() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MENU, PermissionCodes.SYSTEM_MENU_VIEW);
        return Result.ok(menuService.listTree());
    }

    @Operation(summary = "当前用户菜单树")
    @GetMapping("/current")
    public Result<List<MenuVO>> listCurrentUserTree() {
        return Result.ok(menuService.listCurrentUserTree());
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody MenuDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MENU, PermissionCodes.SYSTEM_MENU_EDIT);
        menuService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MenuDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MENU, PermissionCodes.SYSTEM_MENU_EDIT);
        dto.setId(id);
        menuService.update(dto);
        return Result.ok();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MENU, PermissionCodes.SYSTEM_MENU_EDIT);
        menuService.delete(id);
        return Result.ok();
    }
}
