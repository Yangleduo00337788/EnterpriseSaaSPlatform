package com.flowx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.infrastructure.redis.CacheManager;
import com.flowx.user.convert.RoleConvert;
import com.flowx.user.dto.RoleDTO;
import com.flowx.user.dto.RoleQueryDTO;
import com.flowx.user.entity.SysRole;
import com.flowx.user.entity.SysRoleMenu;
import com.flowx.user.mapper.SysRoleMapper;
import com.flowx.user.mapper.SysRoleMenuMapper;
import com.flowx.user.service.RoleService;
import com.flowx.user.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Role service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final RoleConvert roleConvert;
    private final CacheManager cacheManager;

    @Override
    public RoleVO getRoleById(Long roleId) {
        AssertUtil.notNull(roleId, "角色ID不能为空");
        SysRole role = roleMapper.selectById(roleId);
        AssertUtil.notNull(role, ResultCodeEnum.ROLE_NOT_FOUND.getCode(), ResultCodeEnum.ROLE_NOT_FOUND.getMessage());
        return buildRoleVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleDTO dto) {
        AssertUtil.notNull(dto, "角色信息不能为空");
        AssertUtil.notBlank(dto.getRoleName(), "角色名称不能为空");
        AssertUtil.notBlank(dto.getRoleKey(), "角色标识不能为空");

        // Check role key uniqueness
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_key", dto.getRoleKey());
        Long count = roleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("角色标识已存在");
        }

        SysRole role = roleConvert.toEntity(dto);

        // Set defaults
        if (role.getSort() == null) {
            role.setSort(0);
        }
        if (role.getDataScope() == null) {
            role.setDataScope(1);
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }

        roleMapper.insert(role);
        log.info("Created role: {}", role.getRoleName());

        // Assign menus if provided
        if (!CollectionUtils.isEmpty(dto.getMenuIds())) {
            assignMenus(role.getId(), dto.getMenuIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleDTO dto) {
        AssertUtil.notNull(roleId, "角色ID不能为空");
        AssertUtil.notNull(dto, "角色信息不能为空");

        SysRole role = roleMapper.selectById(roleId);
        AssertUtil.notNull(role, ResultCodeEnum.ROLE_NOT_FOUND.getCode(), ResultCodeEnum.ROLE_NOT_FOUND.getMessage());

        // Check role key uniqueness if changed
        if (StringUtils.hasText(dto.getRoleKey()) && !dto.getRoleKey().equals(role.getRoleKey())) {
            QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
            wrapper.eq("role_key", dto.getRoleKey());
            wrapper.ne("id", roleId);
            Long count = roleMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("角色标识已存在");
            }
        }

        roleConvert.updateEntity(dto, role);
        roleMapper.updateById(role);
        log.info("Updated role: {}", roleId);

        // Reassign menus if provided
        if (dto.getMenuIds() != null) {
            assignMenus(roleId, dto.getMenuIds());
        }

        // Evict cache for all users with this role
        cacheManager.evictRolePermissions(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        AssertUtil.notNull(roleId, "角色ID不能为空");
        SysRole role = roleMapper.selectById(roleId);
        AssertUtil.notNull(role, ResultCodeEnum.ROLE_NOT_FOUND.getCode(), ResultCodeEnum.ROLE_NOT_FOUND.getMessage());

        // Soft delete
        roleMapper.deleteById(roleId);

        // Delete role-menu associations
        QueryWrapper<SysRoleMenu> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("role_id", roleId);
        roleMenuMapper.delete(deleteWrapper);

        // Evict cache
        cacheManager.evictRolePermissions(roleId);
        log.info("Deleted role: {}", roleId);
    }

    @Override
    public PageResult<RoleVO> listRoles(RoleQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");

        Page<SysRole> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        QueryWrapper<SysRole> wrapper = buildRoleQueryWrapper(queryDTO);

        Page<SysRole> rolePage = roleMapper.selectPage(page, wrapper);
        List<RoleVO> voList = rolePage.getRecords().stream()
                .map(this::buildRoleVO)
                .collect(Collectors.toList());

        return PageResult.of(rolePage.getTotal(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        AssertUtil.notNull(roleId, "角色ID不能为空");

        // Remove existing role-menu associations
        QueryWrapper<SysRoleMenu> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("role_id", roleId);
        roleMenuMapper.delete(deleteWrapper);

        // Insert new associations
        if (!CollectionUtils.isEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
            log.info("Assigned menus {} to role {}", menuIds, roleId);
        }

        // Evict cache
        cacheManager.evictRolePermissions(roleId);
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        AssertUtil.notNull(roleId, "角色ID不能为空");

        QueryWrapper<SysRoleMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(roleMenus)) {
            return Collections.emptyList();
        }

        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    /**
     * Build RoleVO with menu IDs
     */
    private RoleVO buildRoleVO(SysRole role) {
        RoleVO vo = roleConvert.toVO(role);
        List<Long> menuIds = getRoleMenus(role.getId());
        vo.setMenuIds(menuIds);
        return vo;
    }

    /**
     * Build query wrapper from RoleQueryDTO
     */
    private QueryWrapper<SysRole> buildRoleQueryWrapper(RoleQueryDTO queryDTO) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getRoleName())) {
            wrapper.like("role_name", queryDTO.getRoleName());
        }
        if (StringUtils.hasText(queryDTO.getRoleKey())) {
            wrapper.like("role_key", queryDTO.getRoleKey());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }

        wrapper.orderByAsc("sort");
        return wrapper;
    }
}
