package com.flowcloud.system.service.impl;

import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.MenuDTO;
import com.flowcloud.system.entity.SysPermission;
import com.flowcloud.system.entity.SysRolePermission;
import com.flowcloud.system.mapper.SysPermissionMapper;
import com.flowcloud.system.mapper.SysRolePermissionMapper;
import com.flowcloud.system.service.MenuService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.vo.MenuVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final Set<String> SUPPORTED_TYPES = Set.of("menu", "button");

    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final RoleAuthService roleAuthService;

    @Override
    public List<MenuVO> listTree() {
        List<SysPermission> permissions = permissionMapper.selectListByQuery(
                QueryWrapper.create()
                        .orderBy(SysPermission::getSort, true)
                        .orderBy(SysPermission::getCreateTime, true)
        );
        return buildTree(permissions);
    }

    @Override
    public List<MenuVO> listCurrentUserTree() {
        List<SysPermission> menuPermissions = permissionMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysPermission::getPermType).eq("menu")
                        .and(SysPermission::getStatus).eq(1)
                        .orderBy(SysPermission::getSort, true)
                        .orderBy(SysPermission::getCreateTime, true)
        );
        if (menuPermissions.isEmpty()) {
            return List.of();
        }
        if (roleAuthService.isAdmin()) {
            return buildTree(menuPermissions);
        }

        Set<String> grantedPermissions = roleAuthService.getCurrentUserPermissions();
        if (grantedPermissions.isEmpty()) {
            return List.of();
        }

        Map<Long, SysPermission> permissionMap = new LinkedHashMap<>();
        for (SysPermission permission : menuPermissions) {
            permissionMap.put(permission.getId(), permission);
        }

        Set<Long> visibleIds = new java.util.LinkedHashSet<>();
        for (SysPermission permission : menuPermissions) {
            if (!grantedPermissions.contains(permission.getPermCode())) {
                continue;
            }
            collectAncestors(permission, permissionMap, visibleIds);
        }
        if (visibleIds.isEmpty()) {
            return List.of();
        }

        List<SysPermission> visibleMenus = menuPermissions.stream()
                .filter(permission -> visibleIds.contains(permission.getId()))
                .toList();
        return buildTree(visibleMenus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MenuDTO dto) {
        String normalizedType = normalizeAndValidateType(dto.getPermType());
        validateParent(dto.getParentId(), normalizedType, null);
        ensurePermCodeUnique(dto.getPermCode(), null);

        SysPermission permission = new SysPermission();
        permission.setParentId(normalizeParentId(dto.getParentId()));
        permission.setPermCode(dto.getPermCode().trim());
        permission.setPermName(dto.getPermName().trim());
        permission.setPermType(normalizedType);
        permission.setPath(normalizePath(dto.getPath(), normalizedType));
        permission.setIcon(StringUtils.hasText(dto.getIcon()) ? dto.getIcon().trim() : null);
        permission.setSort(dto.getSort() == null ? 0 : dto.getSort());
        permission.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        permissionMapper.insert(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MenuDTO dto) {
        SysPermission permission = getPermissionOrThrow(dto.getId());
        String normalizedType = normalizeAndValidateType(dto.getPermType());
        validateParent(dto.getParentId(), normalizedType, permission.getId());
        ensurePermCodeUnique(dto.getPermCode(), permission.getId());

        permission.setParentId(normalizeParentId(dto.getParentId()));
        permission.setPermCode(dto.getPermCode().trim());
        permission.setPermName(dto.getPermName().trim());
        permission.setPermType(normalizedType);
        permission.setPath(normalizePath(dto.getPath(), normalizedType));
        permission.setIcon(StringUtils.hasText(dto.getIcon()) ? dto.getIcon().trim() : null);
        permission.setSort(dto.getSort() == null ? 0 : dto.getSort());
        permission.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        permissionMapper.update(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysPermission permission = getPermissionOrThrow(id);
        long childCount = permissionMapper.selectCountByQuery(
                QueryWrapper.create().where(SysPermission::getParentId).eq(id)
        );
        if (childCount > 0) {
            throw new BusinessException("请先删除子菜单");
        }
        rolePermissionMapper.deleteByQuery(
                QueryWrapper.create().where(SysRolePermission::getPermissionId).eq(permission.getId())
        );
        permissionMapper.deleteById(id);
    }

    private MenuVO toVO(SysPermission permission) {
        MenuVO vo = new MenuVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setPermCode(permission.getPermCode());
        vo.setPermName(permission.getPermName());
        vo.setPermType(permission.getPermType());
        vo.setPath(permission.getPath());
        vo.setIcon(permission.getIcon());
        vo.setSort(permission.getSort());
        vo.setStatus(permission.getStatus());
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }

    private List<MenuVO> buildTree(List<SysPermission> permissions) {
        Map<Long, MenuVO> nodeMap = new LinkedHashMap<>();
        for (SysPermission permission : permissions) {
            nodeMap.put(permission.getId(), toVO(permission));
        }
        List<MenuVO> roots = new ArrayList<>();
        for (SysPermission permission : permissions) {
            MenuVO current = nodeMap.get(permission.getId());
            Long parentId = permission.getParentId() == null ? 0L : permission.getParentId();
            if (parentId == 0L) {
                roots.add(current);
                continue;
            }
            MenuVO parent = nodeMap.get(parentId);
            if (parent == null) {
                roots.add(current);
                continue;
            }
            parent.getChildren().add(current);
        }
        roots.forEach(this::sortChildren);
        return roots;
    }

    private void collectAncestors(
            SysPermission permission,
            Map<Long, SysPermission> permissionMap,
            Set<Long> visibleIds
    ) {
        SysPermission current = permission;
        while (current != null) {
            visibleIds.add(current.getId());
            Long parentId = current.getParentId();
            if (parentId == null || parentId == 0L) {
                return;
            }
            current = permissionMap.get(parentId);
        }
    }

    private void sortChildren(MenuVO node) {
        node.getChildren().sort(Comparator.comparing(item -> item.getSort() == null ? 0 : item.getSort()));
        node.getChildren().forEach(this::sortChildren);
    }

    private SysPermission getPermissionOrThrow(Long id) {
        SysPermission permission = permissionMapper.selectOneById(id);
        if (permission == null) {
            throw new BusinessException("菜单不存在");
        }
        return permission;
    }

    private void ensurePermCodeUnique(String permCode, Long excludeId) {
        if (!StringUtils.hasText(permCode)) {
            throw new BusinessException("权限编码不能为空");
        }
        QueryWrapper query = QueryWrapper.create()
                .where(SysPermission::getPermCode).eq(permCode.trim());
        if (excludeId != null) {
            query.and(SysPermission::getId).ne(excludeId);
        }
        if (permissionMapper.selectCountByQuery(query) > 0) {
            throw new BusinessException("权限编码已存在");
        }
    }

    private void validateParent(Long parentId, String permType, Long currentId) {
        Long normalizedParentId = normalizeParentId(parentId);
        if ("button".equals(permType) && normalizedParentId == 0L) {
            throw new BusinessException("按钮必须挂载在菜单下");
        }
        if (currentId != null && currentId.equals(normalizedParentId)) {
            throw new BusinessException("上级菜单不能选择自己");
        }
        if (normalizedParentId == 0L) {
            return;
        }
        SysPermission parent = getPermissionOrThrow(normalizedParentId);
        if ("button".equals(parent.getPermType())) {
            throw new BusinessException("按钮节点不能作为上级菜单");
        }
        if (currentId != null && isDescendant(normalizedParentId, currentId)) {
            throw new BusinessException("上级菜单不能选择当前节点的子级");
        }
    }

    private boolean isDescendant(Long candidateParentId, Long currentId) {
        Long cursor = candidateParentId;
        while (cursor != null && cursor != 0L) {
            if (cursor.equals(currentId)) {
                return true;
            }
            SysPermission current = permissionMapper.selectOneById(cursor);
            if (current == null) {
                return false;
            }
            cursor = current.getParentId();
        }
        return false;
    }

    private Long normalizeParentId(Long parentId) {
        return parentId == null ? 0L : parentId;
    }

    private String normalizeAndValidateType(String permType) {
        if (!StringUtils.hasText(permType)) {
            throw new BusinessException("菜单类型不能为空");
        }
        String normalized = permType.trim().toLowerCase();
        if (!SUPPORTED_TYPES.contains(normalized)) {
            throw new BusinessException("仅支持菜单和按钮类型");
        }
        return normalized;
    }

    private String normalizePath(String path, String permType) {
        if ("menu".equals(permType) && !StringUtils.hasText(path)) {
            throw new BusinessException("菜单路由不能为空");
        }
        if (!StringUtils.hasText(path)) {
            return null;
        }
        return path.trim();
    }
}
