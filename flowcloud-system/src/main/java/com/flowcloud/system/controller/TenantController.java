package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.TenantProfileDTO;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.service.TenantService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.TenantProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "租户中心")
@RestController
@RequestMapping("/api/system/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "当前租户详情")
    @GetMapping("/current")
    public Result<TenantProfileVO> current() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_TENANT, PermissionCodes.SYSTEM_TENANT_VIEW);
        return Result.ok(tenantService.getCurrentTenant());
    }

    @Operation(summary = "更新当前租户")
    @PutMapping("/current")
    public Result<Void> update(@Valid @RequestBody TenantProfileDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_TENANT, PermissionCodes.SYSTEM_TENANT_EDIT);
        tenantService.updateCurrentTenant(dto);
        return Result.ok();
    }
}