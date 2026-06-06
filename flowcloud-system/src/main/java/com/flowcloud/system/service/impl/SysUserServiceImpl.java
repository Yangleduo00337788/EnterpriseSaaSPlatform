package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.common.result.ResultCode;
import com.flowcloud.system.dto.UserDTO;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.entity.SysRole;
import com.flowcloud.system.entity.SysTenant;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.entity.SysUserRole;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.flowcloud.system.mapper.SysRoleMapper;
import com.flowcloud.system.mapper.SysTenantMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.mapper.SysUserRoleMapper;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.service.SysUserService;
import com.flowcloud.system.support.DataScopeType;
import com.flowcloud.system.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.flowcloud.system.vo.UserOptionVO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;
    private final SysTenantMapper tenantMapper;
    private final RoleAuthService roleAuthService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> pageUsers(String keyword, Long deptId, int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(SysUser::getTenantId).eq(TenantContext.getTenantId());
        applyDataScope(query);
        if (StringUtils.hasText(keyword)) {
            query.and(SysUser::getUsername).like(keyword)
                    .or(SysUser::getRealName).like(keyword)
                    .or(SysUser::getPhone).like(keyword);
        }
        if (deptId != null) {
            query.and(SysUser::getDeptId).eq(deptId);
        }
        query.orderBy(SysUser::getCreateTime, false);

        Page<SysUser> page = userMapper.paginate(pageNum, pageSize, query);
        List<UserVO> vos = page.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(vos, page.getTotalRow(), pageNum, pageSize);
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = userMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        ensureAccessible(user);
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserDTO dto) {
        long count = userMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(SysUser::getUsername).eq(dto.getUsername())
                        .and(SysUser::getTenantId).eq(TenantContext.getTenantId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_USERNAME);
        }

        SysTenant tenant = tenantMapper.selectOneById(TenantContext.getTenantId());
        if (tenant != null && tenant.getMaxUsers() != null) {
            long tenantUsers = userMapper.selectCountByQuery(
                    QueryWrapper.create().where(SysUser::getTenantId).eq(TenantContext.getTenantId()));
            if (tenantUsers >= tenant.getMaxUsers()) {
                throw new BusinessException("当前租户人数已达到套餐上限");
            }
        }

        SysUser user = new SysUser();
        user.setTenantId(TenantContext.getTenantId());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "123456"));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDeptId(dto.getDeptId());
        user.setManagerId(dto.getManagerId());
        user.setJobTitle(dto.getJobTitle());
        user.setWorkStatus(StringUtils.hasText(dto.getWorkStatus()) ? dto.getWorkStatus() : "active");
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setIsAdmin(0);
        userMapper.insert(user);

        saveUserRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO dto) {
        SysUser user = userMapper.selectOneById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        ensureAccessible(user);
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDeptId(dto.getDeptId());
        user.setManagerId(dto.getManagerId());
        user.setJobTitle(dto.getJobTitle());
        user.setWorkStatus(dto.getWorkStatus());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        userMapper.update(user);

        userRoleMapper.deleteByQuery(
                QueryWrapper.create().where(SysUserRole::getUserId).eq(dto.getId()));
        saveUserRoles(dto.getId(), dto.getRoleIds());
    }

    @Override
    public void deleteUser(Long id) {
        if (id.equals(TenantContext.getUserId())) {
            throw new BusinessException("不能删除当前登录用户");
        }
        SysUser user = userMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        ensureAccessible(user);
        userMapper.deleteById(id);
        userRoleMapper.deleteByQuery(
                QueryWrapper.create().where(SysUserRole::getUserId).eq(id));
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        ensureAccessible(user);
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(newPassword) ? newPassword : "123456"));
        userMapper.update(user);
    }

    @Override
    public void toggleStatus(Long id) {
        SysUser user = userMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        ensureAccessible(user);
        user.setStatus(user.getStatus() != null && user.getStatus() == 1 ? 0 : 1);
        userMapper.update(user);
    }

    private void applyDataScope(QueryWrapper query) {
        String dataScope = roleAuthService.getCurrentDataScope();
        if (DataScopeType.ALL.equals(dataScope)) {
            return;
        }
        if (DataScopeType.DEPT.equals(dataScope)) {
            SysUser currentUser = userMapper.selectOneById(TenantContext.getUserId());
            Long currentDeptId = currentUser != null ? currentUser.getDeptId() : -1L;
            query.and(SysUser::getDeptId).eq(currentDeptId);
            return;
        }
        query.and(SysUser::getId).eq(TenantContext.getUserId());
    }

    private void ensureAccessible(SysUser user) {
        String dataScope = roleAuthService.getCurrentDataScope();
        if (DataScopeType.ALL.equals(dataScope)) {
            return;
        }
        if (DataScopeType.DEPT.equals(dataScope)) {
            SysUser currentUser = userMapper.selectOneById(TenantContext.getUserId());
            Long currentDeptId = currentUser != null ? currentUser.getDeptId() : null;
            if (currentDeptId != null && currentDeptId.equals(user.getDeptId())) {
                return;
            }
        }
        if (user.getId().equals(TenantContext.getUserId())) {
            return;
        }
        throw new BusinessException(ResultCode.FORBIDDEN);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<SysRole> roles = roleMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysRole::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysRole::getId).in(roleIds));
        for (SysRole role : roles) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(role.getId());
            userRoleMapper.insert(ur);
        }
    }

    @Override
    public List<UserOptionVO> listOptions() {
        List<SysUser> users = userMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysUser::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysUser::getStatus).eq(1)
                        .orderBy(SysUser::getRealName, true));
        return users.stream().map(u -> {
            UserOptionVO vo = new UserOptionVO();
            vo.setId(u.getId());
            vo.setRealName(u.getRealName());
            vo.setUsername(u.getUsername());
            vo.setJobTitle(u.getJobTitle());
            if (u.getDeptId() != null) {
                SysDept dept = deptMapper.selectOneById(u.getDeptId());
                if (dept != null) {
                    vo.setDeptName(dept.getDeptName());
                }
            }
            return vo;
        }).toList();
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setDeptId(user.getDeptId());
        vo.setManagerId(user.getManagerId());
        vo.setJobTitle(user.getJobTitle());
        vo.setWorkStatus(user.getWorkStatus());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());

        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectOneById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }
        if (user.getManagerId() != null) {
            SysUser manager = userMapper.selectOneById(user.getManagerId());
            if (manager != null) {
                vo.setManagerName(manager.getRealName());
            }
        }

        List<SysUserRole> userRoles = userRoleMapper.selectListByQuery(
                QueryWrapper.create().where(SysUserRole::getUserId).eq(user.getId()));
        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
            List<SysRole> roles = roleMapper.selectListByQuery(
                    QueryWrapper.create().where(SysRole::getId).in(roleIds));
            vo.setRoleIds(roles.stream().map(SysRole::getId).toList());
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).toList());
        }
        return vo;
    }
}