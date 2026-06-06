package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.ResultCode;
import com.flowcloud.common.security.JwtUtils;
import com.flowcloud.system.dto.LoginDTO;
import com.flowcloud.system.dto.RegisterDTO;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.entity.SysPermission;
import com.flowcloud.system.entity.SysRole;
import com.flowcloud.system.entity.SysRolePermission;
import com.flowcloud.system.entity.SysTenant;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.entity.SysUserRole;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.flowcloud.system.mapper.SysPermissionMapper;
import com.flowcloud.system.mapper.SysRoleMapper;
import com.flowcloud.system.mapper.SysRolePermissionMapper;
import com.flowcloud.system.mapper.SysTenantMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.mapper.SysUserRoleMapper;
import com.flowcloud.system.service.AuthService;
import com.flowcloud.system.service.TenantFeatureService;
import com.flowcloud.system.support.DataScopeType;
import com.flowcloud.system.vo.LoginVO;
import com.flowcloud.common.event.AuditEvent;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysTenantMapper tenantMapper;
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysDeptMapper deptMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ApplicationEventPublisher eventPublisher;
    private final TenantFeatureService tenantFeatureService;

    @Override
    public LoginVO login(LoginDTO dto) {
        SysTenant tenant = tenantMapper.selectOneByQuery(
                QueryWrapper.create().where(SysTenant::getTenantCode).eq(dto.getTenantCode()));
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        if (tenant.getStatus() != null && tenant.getStatus() == 0) {
            throw new BusinessException("企业已被禁用");
        }
        if (tenant.getExpireTime() != null && tenant.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("企业套餐已到期");
        }

        TenantContext.setTenantId(tenant.getId());
        SysUser user = userMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SysUser::getUsername).eq(dto.getUsername())
                        .and(SysUser::getTenantId).eq(tenant.getId()));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        eventPublisher.publishEvent(AuditEvent.success(
                this, tenant.getId(), user.getId(), user.getRealName(),
                "login", "user", String.valueOf(user.getId()), user.getUsername()));

        return buildLoginVO(user, tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        long count = tenantMapper.selectCountByQuery(
                QueryWrapper.create().where(SysTenant::getTenantCode).eq(dto.getTenantCode()));
        if (count > 0) {
            throw new BusinessException("企业编码已存在");
        }

        SysTenant tenant = new SysTenant();
        tenant.setTenantCode(dto.getTenantCode());
        tenant.setTenantName(dto.getTenantName());
        tenant.setContactName(dto.getContactName());
        tenant.setContactPhone(dto.getContactPhone());
        tenant.setContactEmail(dto.getContactEmail());
        tenant.setThemeColor("#3370FF");
        tenant.setStatus(1);
        tenant.setPlanType("basic");
        tenant.setMaxUsers(50);
        tenant.setExpireTime(LocalDateTime.now().plusYears(1));
        tenant.setPackageConfig("{\"storageGb\":10,\"workflowVersioning\":true}");
        tenant.setFeatureConfig("{\"approval\":true,\"report\":true,\"message\":true,\"tenantSettings\":true}");
        tenantMapper.insert(tenant);

        TenantContext.setTenantId(tenant.getId());

        SysDept rootDept = new SysDept();
        rootDept.setTenantId(tenant.getId());
        rootDept.setParentId(0L);
        rootDept.setDeptName(dto.getTenantName());
        rootDept.setLeader(dto.getRealName());
        rootDept.setAncestors("0");
        rootDept.setSort(0);
        rootDept.setStatus(1);
        deptMapper.insert(rootDept);

        SysRole adminRole = new SysRole();
        adminRole.setTenantId(tenant.getId());
        adminRole.setRoleCode("admin");
        adminRole.setRoleName("管理员");
        adminRole.setDescription("企业管理员");
        adminRole.setDataScope(DataScopeType.ALL);
        adminRole.setSort(0);
        adminRole.setStatus(1);
        roleMapper.insert(adminRole);

        SysRole approverRole = new SysRole();
        approverRole.setTenantId(tenant.getId());
        approverRole.setRoleCode("approver");
        approverRole.setRoleName("审批人");
        approverRole.setDescription("审批人员");
        approverRole.setDataScope(DataScopeType.DEPT);
        approverRole.setSort(1);
        approverRole.setStatus(1);
        roleMapper.insert(approverRole);

        SysRole employeeRole = new SysRole();
        employeeRole.setTenantId(tenant.getId());
        employeeRole.setRoleCode("employee");
        employeeRole.setRoleName("普通员工");
        employeeRole.setDescription("普通员工");
        employeeRole.setDataScope(DataScopeType.SELF);
        employeeRole.setSort(2);
        employeeRole.setStatus(1);
        roleMapper.insert(employeeRole);

        SysUser user = new SysUser();
        user.setTenantId(tenant.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getContactEmail());
        user.setPhone(dto.getContactPhone());
        user.setDeptId(rootDept.getId());
        user.setJobTitle("企业管理员");
        user.setWorkStatus("active");
        user.setStatus(1);
        user.setIsAdmin(1);
        userMapper.insert(user);

        rootDept.setLeaderUserId(user.getId());
        deptMapper.update(rootDept);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(adminRole.getId());
        userRoleMapper.insert(userRole);
    }

    @Override
    public LoginVO getCurrentUser() {
        Long userId = TenantContext.getUserId();
        Long tenantId = TenantContext.getTenantId();
        if (userId == null || tenantId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        SysUser user = userMapper.selectOneById(userId);
        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        if (user == null || tenant == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return buildLoginVO(user, tenant);
    }

    private LoginVO buildLoginVO(SysUser user, SysTenant tenant) {
        Set<String> roles = getUserRoles(user.getId());
        Set<String> permissions = getUserPermissions(user.getId());

        String token = jwtUtils.generateToken(user.getId(), tenant.getId(), user.getUsername());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setTenantId(tenant.getId());
        vo.setDeptId(user.getDeptId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setTenantName(tenant.getTenantName());
        vo.setLogo(tenant.getLogo());
        vo.setThemeColor(tenant.getThemeColor());
        vo.setDataScope(getUserDataScope(user));
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        vo.setEnabledFeatures(tenantFeatureService.getEnabledFeatures(tenant.getId()));
        return vo;
    }

    private Set<String> getUserRoles(Long userId) {
        SysUser user = userMapper.selectOneById(userId);
        Set<String> roles = new HashSet<>();
        if (user != null && user.getIsAdmin() != null && user.getIsAdmin() == 1) {
            roles.add("admin");
        }

        List<SysRole> dbRoles = loadRoles(userId);
        roles.addAll(dbRoles.stream().map(SysRole::getRoleCode).collect(Collectors.toSet()));
        return roles;
    }

    private Set<String> getUserPermissions(Long userId) {
        SysUser user = userMapper.selectOneById(userId);
        Set<String> permissions = new HashSet<>();
        if (user != null && user.getIsAdmin() != null && user.getIsAdmin() == 1) {
            permissions.add("*");
        }

        List<SysRole> roles = loadRoles(userId);
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
        permissions.addAll(dbPermissions.stream().map(SysPermission::getPermCode).collect(Collectors.toSet()));
        return permissions;
    }

    private String getUserDataScope(SysUser user) {
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
