package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.ResultCode;
import com.flowcloud.system.entity.SysPermission;
import com.flowcloud.system.entity.SysRole;
import com.flowcloud.system.entity.SysRolePermission;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.entity.SysUserRole;
import com.flowcloud.system.mapper.SysPermissionMapper;
import com.flowcloud.system.mapper.SysRoleMapper;
import com.flowcloud.system.mapper.SysRolePermissionMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.mapper.SysUserRoleMapper;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.DataScopeType;
import com.flowcloud.system.support.PermissionCodes;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleAuthServiceImpl implements RoleAuthService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;

    @Override
    public Set<String> getCurrentUserRoles() {
        SysUser user = getCurrentUser();
        if (user == null) {
            return Set.of();
        }

        Set<String> roles = new HashSet<>();
        if (user.getIsAdmin() != null && user.getIsAdmin() == 1) {
            roles.add("admin");
        }
        roles.addAll(loadRoles(user.getId()).stream().map(SysRole::getRoleCode).collect(Collectors.toSet()));
        return roles;
    }

    @Override
    public Set<String> getCurrentUserPermissions() {
        SysUser user = getCurrentUser();
        if (user == null) {
            return Set.of();
        }

        Set<String> permissions = new HashSet<>();
        if (user.getIsAdmin() != null && user.getIsAdmin() == 1) {
            permissions.add("*");
        }
        List<SysRole> roles = loadRoles(user.getId());
        if (roles.isEmpty()) {
            return permissions;
        }
        List<Long> roleIds = roles.stream().map(SysRole::getId).toList();
        List<SysRolePermission> mappings = rolePermissionMapper.selectListByQuery(
                QueryWrapper.create().where(SysRolePermission::getRoleId).in(roleIds));
        if (mappings.isEmpty()) {
            return permissions;
        }
        List<Long> permissionIds = mappings.stream().map(SysRolePermission::getPermissionId).distinct().toList();
        List<SysPermission> dbPermissions = permissionMapper.selectListByQuery(
                QueryWrapper.create().where(SysPermission::getId).in(permissionIds));
        permissions.addAll(dbPermissions.stream()
                .map(SysPermission::getPermCode)
                .collect(Collectors.toSet()));
        return permissions;
    }

    @Override
    public String getCurrentDataScope() {
        SysUser user = getCurrentUser();
        if (user == null) {
            return DataScopeType.SELF;
        }
        if (user.getIsAdmin() != null && user.getIsAdmin() == 1) {
            return DataScopeType.ALL;
        }

        List<SysRole> roles = loadRoles(user.getId());
        boolean hasDept = false;
        for (SysRole role : roles) {
            if (DataScopeType.ALL.equals(role.getDataScope())) {
                return DataScopeType.ALL;
            }
            if (DataScopeType.DEPT.equals(role.getDataScope())) {
                hasDept = true;
            }
        }
        return hasDept ? DataScopeType.DEPT : DataScopeType.SELF;
    }

    @Override
    public boolean isAdmin() {
        return getCurrentUserRoles().contains("admin");
    }

    @Override
    public boolean isApprover() {
        Set<String> roles = getCurrentUserRoles();
        return roles.contains("admin") || roles.contains("approver")
                || hasPermission(PermissionCodes.APPROVAL_TASK_HANDLE)
                || hasPermission(PermissionCodes.APPROVAL_PENDING);
    }

    @Override
    public boolean hasPermission(String permission) {
        Set<String> permissions = getCurrentUserPermissions();
        return permissions.contains("*") || permissions.contains(permission);
    }

    @Override
    public void requirePermission(String permission) {
        if (!hasPermission(permission)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    @Override
    public void requireAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return;
            }
        }
        throw new BusinessException(ResultCode.FORBIDDEN);
    }

    @Override
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    @Override
    public void requireApprover() {
        if (!isApprover()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    private SysUser getCurrentUser() {
        Long userId = TenantContext.getUserId();
        if (userId == null) {
            return null;
        }
        return userMapper.selectOneById(userId);
    }

    private List<SysRole> loadRoles(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectListByQuery(
                QueryWrapper.create().where(SysUserRole::getUserId).eq(userId));
        if (userRoles.isEmpty()) {
            return List.of();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).distinct().toList();
        return roleMapper.selectListByQuery(QueryWrapper.create().where(SysRole::getId).in(roleIds));
    }
}
