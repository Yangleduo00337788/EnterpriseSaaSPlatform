package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.StorageSettingsDTO;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.service.StorageService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.StorageSettingsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统设置")
@RestController
@RequestMapping("/api/system/settings")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final StorageService storageService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "获取当前租户存储设置")
    @GetMapping("/storage")
    public Result<StorageSettingsVO> getStorageSettings() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_TENANT, PermissionCodes.SYSTEM_TENANT_VIEW);
        return Result.ok(storageService.getCurrentSettings());
    }

    @Operation(summary = "更新当前租户存储设置")
    @PutMapping("/storage")
    public Result<Void> updateStorageSettings(@Valid @RequestBody StorageSettingsDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_TENANT, PermissionCodes.SYSTEM_TENANT_EDIT);
        storageService.updateCurrentSettings(dto);
        return Result.ok();
    }

    @Operation(summary = "测试当前租户存储配置连通性")
    @PostMapping("/storage/test")
    public Result<String> testStorageSettings(@Valid @RequestBody StorageSettingsDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_TENANT, PermissionCodes.SYSTEM_TENANT_EDIT);
        return Result.ok(storageService.testConnection(dto));
    }
}
