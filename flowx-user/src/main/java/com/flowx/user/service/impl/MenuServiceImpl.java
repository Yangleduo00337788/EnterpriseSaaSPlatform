package com.flowx.user.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.user.convert.MenuConvert;
import com.flowx.user.dto.MenuDTO;
import com.flowx.user.entity.SysMenu;
import com.flowx.user.mapper.SysMenuMapper;
import com.flowx.user.mapper.SysUserMapper;
import com.flowx.user.service.MenuService;
import com.flowx.user.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Menu service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper menuMapper;
    private final SysUserMapper userMapper;
    private final MenuConvert menuConvert;

    @Override
    public MenuVO getMenuById(Long menuId) {
        AssertUtil.notNull(menuId, "菜单ID不能为空");
        SysMenu menu = menuMapper.selectOneById(menuId);
        AssertUtil.notNull(menu, ResultCodeEnum.MENU_NOT_FOUND.getCode(), ResultCodeEnum.MENU_NOT_FOUND.getMessage());
        return menuConvert.toVO(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuDTO dto) {
        AssertUtil.notNull(dto, "菜单信息不能为空");
        AssertUtil.notBlank(dto.getMenuName(), "菜单名称不能为空");
        AssertUtil.notNull(dto.getMenuType(), "菜单类型不能为空");

        SysMenu menu = menuConvert.toEntity(dto);

        // Set defaults
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }

        menuMapper.insert(menu);
        log.info("Created menu: {}", menu.getMenuName());
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long menuId, MenuDTO dto) {
        AssertUtil.notNull(menuId, "菜单ID不能为空");
        AssertUtil.notNull(dto, "菜单信息不能为空");

        SysMenu menu = menuMapper.selectOneById(menuId);
        AssertUtil.notNull(menu, ResultCodeEnum.MENU_NOT_FOUND.getCode(), ResultCodeEnum.MENU_NOT_FOUND.getMessage());

        // Prevent setting parent to self
        if (dto.getParentId() != null && dto.getParentId().equals(menuId)) {
            throw new com.flowx.common.core.exception.BizException("父菜单不能是自身");
        }

        menuConvert.updateEntity(dto, menu);
        menuMapper.updateById(menu);
        log.info("Updated menu: {}", menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        AssertUtil.notNull(menuId, "菜单ID不能为空");
        SysMenu menu = menuMapper.selectOneById(menuId);
        AssertUtil.notNull(menu, ResultCodeEnum.MENU_NOT_FOUND.getCode(), ResultCodeEnum.MENU_NOT_FOUND.getMessage());

        // Check if menu has children
        QueryWrapper childWrapper = QueryWrapper.create();
        childWrapper.eq("parent_id", menuId);
        Long childCount = menuMapper.selectCount(childWrapper);
        if (childCount > 0) {
            throw new com.flowx.common.core.exception.BizException("存在子菜单，不允许删除");
        }

        // Soft delete
        menuMapper.deleteById(menuId);
        log.info("Deleted menu: {}", menuId);
    }

    @Override
    public List<MenuVO> listMenus() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.orderBy("parent_id", true).orderBy("sort", true);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return menuConvert.toVOList(menus);
    }

    @Override
    public List<MenuVO> getMenuTree() {
        List<SysMenu> allMenus = menuMapper.selectMenuTree();
        if (CollectionUtils.isEmpty(allMenus)) {
            return Collections.emptyList();
        }

        List<MenuVO> voList = menuConvert.toVOList(allMenus);
        return buildMenuTree(voList, 0L);
    }

    @Override
    public List<MenuVO> getMenusByUserId(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");

        Set<String> permissions = userMapper.selectUserPermissions(userId);
        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptyList();
        }

        // Get all menus that the user has access to through roles
        List<SysMenu> allMenus = menuMapper.selectMenuTree();
        if (CollectionUtils.isEmpty(allMenus)) {
            return Collections.emptyList();
        }

        // Filter menus by user permissions
        List<SysMenu> userMenus = allMenus.stream()
                .filter(menu -> permissions.contains(menu.getPermission()))
                .collect(Collectors.toList());

        List<MenuVO> voList = menuConvert.toVOList(userMenus);
        return buildMenuTree(voList, 0L);
    }

    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        Set<String> permissions = userMapper.selectUserPermissions(userId);
        return permissions != null ? permissions : Collections.emptySet();
    }

    /**
     * Build menu tree recursively
     */
    private List<MenuVO> buildMenuTree(List<MenuVO> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (MenuVO menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                List<MenuVO> children = buildMenuTree(menus, menu.getId());
                menu.setChildren(children);
                tree.add(menu);
            }
        }
        return tree;
    }
}
