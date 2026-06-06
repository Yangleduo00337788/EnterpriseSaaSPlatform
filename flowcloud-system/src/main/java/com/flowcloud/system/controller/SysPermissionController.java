package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.service.PermissionService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/system/permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final PermissionService permissionService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "权限树")
    @GetMapping("/tree")
    public Result<List<PermissionVO>> tree() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_ROLE, PermissionCodes.SYSTEM_ROLE_VIEW);
        return Result.ok(permissionService.listTree());
    }
}
