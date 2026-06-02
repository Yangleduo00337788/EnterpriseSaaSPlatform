package com.flowx.user.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.user.dto.RoleDTO;
import com.flowx.user.dto.RoleQueryDTO;
import com.flowx.user.service.RoleService;
import com.flowx.user.vo.RoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Role management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Get role by ID
     *
     * @param id role ID
     * @return role VO
     */
    @GetMapping("/{id}")
    public R<RoleVO> getRoleById(@PathVariable("id") Long id) {
        RoleVO roleVO = roleService.getRoleById(id);
        return R.ok(roleVO);
    }

    /**
     * List roles with pagination
     *
     * @param queryDTO query parameters
     * @return paginated role list
     */
    @GetMapping("/list")
    public R<PageResult<RoleVO>> listRoles(RoleQueryDTO queryDTO) {
        PageResult<RoleVO> result = roleService.listRoles(queryDTO);
        return R.ok(result);
    }

    /**
     * Create new role
     *
     * @param dto role creation DTO
     * @return created role ID
     */
    @PostMapping
    public R<Long> createRole(@Valid @RequestBody RoleDTO dto) {
        Long roleId = roleService.createRole(dto);
        return R.ok(roleId);
    }

    /**
     * Update existing role (id from request body)
     *
     * @param dto role update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateRole(@Valid @RequestBody RoleDTO dto) {
        roleService.updateRole(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete roles by IDs (comma-separated)
     *
     * @param ids role IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteRoles(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            roleService.deleteRole(id);
        }
        return R.ok();
    }

    /**
     * Assign menus to role
     *
     * @param id      role ID
     * @param request menu assignment request containing menu IDs
     * @return success response
     */
    @PutMapping("/{id}/menus")
    public R<Void> assignMenus(@PathVariable("id") Long id, @RequestBody MenuAssignRequest request) {
        roleService.assignMenus(id, request.getMenuIds());
        return R.ok();
    }

    /**
     * Menu assignment request DTO
     */
    @lombok.Data
    public static class MenuAssignRequest {
        private List<Long> menuIds;
    }
}