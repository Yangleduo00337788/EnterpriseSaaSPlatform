package com.flowcloud.approval.controller;

import com.flowcloud.approval.dto.TemplateDTO;
import com.flowcloud.approval.service.ApprovalTemplateService;
import com.flowcloud.approval.vo.TemplateVersionVO;
import com.flowcloud.approval.vo.TemplateVO;
import com.flowcloud.common.result.Result;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "审批模板")
@RestController
@RequestMapping("/api/approval/templates")
@RequiredArgsConstructor
public class ApprovalTemplateController {

    private final ApprovalTemplateService templateService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "模板列表（仅已发布，用于发起审批）")
    @GetMapping
    public Result<List<TemplateVO>> list(@RequestParam(required = false) String category) {
        return Result.ok(templateService.listTemplates(category));
    }

    @Operation(summary = "全部模板（管理态，含草稿/停用）")
    @GetMapping("/all")
    public Result<List<TemplateVO>> listAll(@RequestParam(required = false) String category) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        return Result.ok(templateService.listAllTemplates(category));
    }

    @Operation(summary = "模板详情")
    @GetMapping("/{id}")
    public Result<TemplateVO> getById(@PathVariable Long id) {
        return Result.ok(templateService.getById(id));
    }

    @Operation(summary = "创建模板（草稿）")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody TemplateDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        templateService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "更新模板（退回草稿）")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TemplateDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        dto.setId(id);
        templateService.update(dto);
        return Result.ok();
    }

    @Operation(summary = "发布模板")
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        String remark = body != null ? body.get("remark") : null;
        templateService.publish(id, remark);
        return Result.ok();
    }

    @Operation(summary = "停用模板")
    @PostMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        templateService.disable(id);
        return Result.ok();
    }

    @Operation(summary = "版本历史")
    @GetMapping("/{id}/versions")
    public Result<List<TemplateVersionVO>> versions(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        return Result.ok(templateService.listVersions(id));
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.TEMPLATE, PermissionCodes.APPROVAL_TEMPLATE_MANAGE);
        templateService.delete(id);
        return Result.ok();
    }
}