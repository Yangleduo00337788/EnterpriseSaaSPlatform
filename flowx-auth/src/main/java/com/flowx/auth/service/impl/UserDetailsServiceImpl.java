package com.flowx.auth.service.impl;

import com.flowx.common.core.enums.StatusEnum;
import com.flowx.common.core.base.SecurityUser;
import com.flowx.user.entity.SysRole;
import com.flowx.user.entity.SysUser;
import com.flowx.user.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    public UserDetailsServiceImpl(@Lazy SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectUserByUsername(username);
        if (user == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("Account is disabled: " + username);
        }

        // Query roles
        List<SysRole> roleList = sysUserMapper.selectUserRoles(user.getId());
        Set<String> roles = roleList.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());

        // Query permissions
        Set<String> permissions = sysUserMapper.selectUserPermissions(user.getId());
        if (permissions == null) {
            permissions = new HashSet<>();
        }

        return SecurityUser.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .tenantId(user.getTenantId())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}