package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.RoleDTO;
import com.flowcloud.system.entity.SysRole;
import com.flowcloud.system.entity.SysRolePermission;
import com.flowcloud.system.entity.SysUserRole;
import com.flowcloud.system.mapper.SysRoleMapper;
import com.flowcloud.system.mapper.SysRolePermissionMapper;
import com.flowcloud.system.mapper.SysUserRoleMapper;
import com.flowcloud.system.service.RoleService;
import com.flowcloud.system.support.DataScopeType;
import com.flowcloud.system.vo.RoleOptionVO;
import com.flowcloud.system.vo.RoleVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public List<RoleOptionVO> listOptions() {
        return roleMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where(SysRole::getTenantId).eq(TenantContext.getTenantId())
                                .and(SysRole::getStatus).eq(1)
                                .orderBy(SysRole::getSort, true))
                .stream()
                .map(this::toOptionVO)
                .toList();
    }

    @Override
    public List<RoleVO> listAll() {
        return roleMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where(SysRole::getTenantId).eq(TenantContext.getTenantId())
                                .orderBy(SysRole::getSort, true))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public RoleVO getById(Long id) {
        SysRole role = getRoleOrThrow(id);
        RoleVO vo = toVO(role);
        vo.setPermissionIds(loadPermissionIds(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleDTO dto) {
        ensureRoleCodeUnique(dto.getRoleCode(), null);
        SysRole role = new SysRole();
        role.setTenantId(TenantContext.getTenantId());
        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setDataScope(StringUtils.hasText(dto.getDataScope()) ? dto.getDataScope() : DataScopeType.SELF);
        role.setSort(dto.getSort() != null ? dto.getSort() : 0);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        roleMapper.insert(role);
        saveRolePermissions(role.getId(), dto.getPermissionIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO dto) {
        SysRole role = getRoleOrThrow(dto.getId());
        if ("admin".equals(role.getRoleCode())) {
            throw new BusinessException("系统内置管理员角色不可修改");
        }
        ensureRoleCodeUnique(dto.getRoleCode(), dto.getId());
        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        if (StringUtils.hasText(dto.getDataScope())) {
            role.setDataScope(dto.getDataScope());
        }
        if (dto.getSort() != null) {
            role.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        roleMapper.update(role);
        rolePermissionMapper.deleteByQuery(
                QueryWrapper.create().where(SysRolePermission::getRoleId).eq(role.getId()));
        saveRolePermissions(role.getId(), dto.getPermissionIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysRole role = getRoleOrThrow(id);
        if ("admin".equals(role.getRoleCode()) || "approver".equals(role.getRoleCode()) || "employee".equals(role.getRoleCode())) {
            throw new BusinessException("系统内置角色不可删除");
        }
        long userCount = userRoleMapper.selectCountByQuery(
                QueryWrapper.create().where(SysUserRole::getRoleId).eq(id));
        if (userCount > 0) {
            throw new BusinessException("角色已分配给用户，无法删除");
        }
        rolePermissionMapper.deleteByQuery(
                QueryWrapper.create().where(SysRolePermission::getRoleId).eq(id));
        roleMapper.deleteById(id);
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (Long permissionId : permissionIds) {
            SysRolePermission mapping = new SysRolePermission();
            mapping.setRoleId(roleId);
            mapping.setPermissionId(permissionId);
            rolePermissionMapper.insert(mapping);
        }
    }

    private List<Long> loadPermissionIds(Long roleId) {
        return rolePermissionMapper.selectListByQuery(
                        QueryWrapper.create().where(SysRolePermission::getRoleId).eq(roleId))
                .stream()
                .map(SysRolePermission::getPermissionId)
                .toList();
    }

    private SysRole getRoleOrThrow(Long id) {
        SysRole role = roleMapper.selectOneById(id);
        if (role == null || !role.getTenantId().equals(TenantContext.getTenantId())) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    private void ensureRoleCodeUnique(String roleCode, Long excludeId) {
        QueryWrapper query = QueryWrapper.create()
                .where(SysRole::getTenantId).eq(TenantContext.getTenantId())
                .and(SysRole::getRoleCode).eq(roleCode);
        if (excludeId != null) {
            query.and(SysRole::getId).ne(excludeId);
        }
        if (roleMapper.selectCountByQuery(query) > 0) {
            throw new BusinessException("角色编码已存在");
        }
    }

    private RoleOptionVO toOptionVO(SysRole role) {
        RoleOptionVO vo = new RoleOptionVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDataScope(role.getDataScope());
        return vo;
    }

    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        vo.setDataScope(role.getDataScope());
        vo.setSort(role.getSort());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}
