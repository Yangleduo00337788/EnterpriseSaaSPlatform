package com.flowx.user.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.user.dto.RoleDTO;
import com.flowx.user.dto.RoleQueryDTO;
import com.flowx.user.vo.RoleVO;

import java.util.List;

/**
 * Role service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * Get role by ID
     *
     * @param roleId role ID
     * @return role VO
     */
    RoleVO getRoleById(Long roleId);

    /**
     * Create new role
     *
     * @param dto role creation DTO
     * @return created role ID
     */
    Long createRole(RoleDTO dto);

    /**
     * Update existing role
     *
     * @param roleId role ID
     * @param dto    role update DTO
     */
    void updateRole(Long roleId, RoleDTO dto);

    /**
     * Delete role (soft delete)
     *
     * @param roleId role ID
     */
    void deleteRole(Long roleId);

    /**
     * List roles with pagination and filters
     *
     * @param queryDTO query parameters
     * @return paginated role list
     */
    PageResult<RoleVO> listRoles(RoleQueryDTO queryDTO);

    /**
     * Assign menus to role
     *
     * @param roleId  role ID
     * @param menuIds menu IDs to assign
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * Get menu IDs assigned to role
     *
     * @param roleId role ID
     * @return list of menu IDs
     */
    List<Long> getRoleMenus(Long roleId);
}
