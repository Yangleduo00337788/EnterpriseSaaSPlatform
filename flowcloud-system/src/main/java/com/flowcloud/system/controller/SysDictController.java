package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.DictTypeDTO;
import com.flowcloud.system.service.DictService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.vo.DictDataVO;
import com.flowcloud.system.vo.DictTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统字典")
@RestController
@RequestMapping("/api/system/dicts")
@RequiredArgsConstructor
public class SysDictController {

    private final DictService dictService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "字典类型列表")
    @GetMapping
    public Result<List<DictTypeVO>> list() {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DICT);
        return Result.ok(dictService.listTypes());
    }

    @Operation(summary = "按编码获取字典项")
    @GetMapping("/code/{dictCode}")
    public Result<List<DictDataVO>> listByCode(@PathVariable String dictCode) {
        return Result.ok(dictService.listByCode(dictCode));
    }

    @Operation(summary = "字典详情")
    @GetMapping("/{id}")
    public Result<DictTypeVO> getById(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DICT);
        return Result.ok(dictService.getById(id));
    }

    @Operation(summary = "创建字典")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody DictTypeDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DICT, PermissionCodes.SYSTEM_DICT_EDIT);
        dictService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "更新字典")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DictTypeDTO dto) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DICT, PermissionCodes.SYSTEM_DICT_EDIT);
        dto.setId(id);
        dictService.update(dto);
        return Result.ok();
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_DICT, PermissionCodes.SYSTEM_DICT_EDIT);
        dictService.delete(id);
        return Result.ok();
    }
}
