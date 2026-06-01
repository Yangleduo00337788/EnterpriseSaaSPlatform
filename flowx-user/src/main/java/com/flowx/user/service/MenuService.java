package com.flowx.user.service;

import com.flowx.user.dto.MenuDTO;
import com.flowx.user.vo.MenuVO;

import java.util.List;
import java.util.Set;

/**
 * Menu service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface MenuService {

    /**
     * Get menu by ID
     *
     * @param menuId menu ID
     * @return menu VO
     */
    MenuVO getMenuById(Long menuId);

    /**
     * Create new menu
     *
     * @param dto menu creation DTO
     * @return created menu ID
     */
    Long createMenu(MenuDTO dto);

    /**
     * Update existing menu
     *
     * @param menuId menu ID
     * @param dto    menu update DTO
     */
    void updateMenu(Long menuId, MenuDTO dto);

    /**
     * Delete menu (soft delete)
     *
     * @param menuId menu ID
     */
    void deleteMenu(Long menuId);

    /**
     * List all menus (flat)
     *
     * @return list of menu VOs
     */
    List<MenuVO> listMenus();

    /**
     * Get menu tree structure
     *
     * @return tree-structured menu VOs
     */
    List<MenuVO> getMenuTree();

    /**
     * Get menus assigned to user (through roles)
     *
     * @param userId user ID
     * @return list of menu VOs
     */
    List<MenuVO> getMenusByUserId(Long userId);

    /**
     * Get permission identifiers for user
     *
     * @param userId user ID
     * @return set of permission strings
     */
    Set<String> getPermissionsByUserId(Long userId);
}
