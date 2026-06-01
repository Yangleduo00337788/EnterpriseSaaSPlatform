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

import java.util.List;

/**
 * Role management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/roles")
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
     * Update existing role
     *
     * @param id  role ID
     * @param dto role update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateRole(@PathVariable("id") Long id, @Valid @RequestBody RoleDTO dto) {
        roleService.updateRole(id, dto);
        return R.ok();
    }

    /**
     * Delete role
     *
     * @param id role ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
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
