package com.flowcloud.notification.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.notification.dto.MessageTemplateDTO;
import com.flowcloud.notification.entity.SysMessageTemplate;
import com.flowcloud.notification.service.MessageTemplateService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "消息模板")
@RestController
@RequestMapping("/api/system/message-templates")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final MessageTemplateService templateService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "模板列表")
    @GetMapping
    public Result<List<SysMessageTemplate>> list() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MESSAGE_TEMPLATE);
        return Result.ok(templateService.listAll());
    }

    @Operation(summary = "模板详情")
    @GetMapping("/{id}")
    public Result<SysMessageTemplate> getById(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MESSAGE_TEMPLATE);
        return Result.ok(templateService.getById(id));
    }

    @Operation(summary = "创建模板")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody MessageTemplateDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MESSAGE_TEMPLATE, PermissionCodes.SYSTEM_MESSAGE_TEMPLATE_EDIT);
        templateService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "更新模板")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MessageTemplateDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MESSAGE_TEMPLATE, PermissionCodes.SYSTEM_MESSAGE_TEMPLATE_EDIT);
        dto.setId(id);
        templateService.update(dto);
        return Result.ok();
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_MESSAGE_TEMPLATE, PermissionCodes.SYSTEM_MESSAGE_TEMPLATE_EDIT);
        templateService.delete(id);
        return Result.ok();
    }
}
