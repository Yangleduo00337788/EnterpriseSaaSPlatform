package com.flowx.auth.service.impl;

import com.flowx.common.constant.StatusEnum;
import com.flowx.common.exception.BusinessException;
import com.flowx.common.exception.ErrorCode;
import com.flowx.common.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * UserDetailsService implementation for Spring Security
 *
 * @author FlowX Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // TODO: Inject UserMapper or UserService
    // private final UserMapper userMapper;
    // private final RoleMapper roleMapper;
    // private final PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Query user from database
        // User user = userMapper.selectByUsername(username);
        // if (user == null) {
        //     log.warn("User not found: {}", username);
        //     throw new UsernameNotFoundException("User not found: " + username);
        // }

        // TODO: Check user status
        // if (StatusEnum.DISABLE.getCode().equals(user.getStatus())) {
        //     throw new BusinessException(ErrorCode.USER_DISABLED);
        // }

        // TODO: Load roles from database
        // List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        // List<String> roleCodes = roles.stream()
        //         .map(Role::getCode)
        //         .collect(Collectors.toList());

        // TODO: Load permissions from database
        // List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
        // List<String> permissionCodes = permissions.stream()
        //         .map(Permission::getCode)
        //         .collect(Collectors.toList());

        // Placeholder implementation - replace with actual database queries
        log.info("Loading user details for: {}", username);

        // Mock data for development - remove in production
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        List<String> permissions = new ArrayList<>();
        permissions.add("user:view");
        permissions.add("user:edit");

        // Build SecurityUser
        return SecurityUser.builder()
                .userId(1L) // TODO: user.getId()
                .username(username)
                .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH") // TODO: user.getPassword()
                .tenantId(1L) // TODO: user.getTenantId()
                .nickname(username) // TODO: user.getNickname()
                .status(StatusEnum.ENABLE.getCode()) // TODO: user.getStatus()
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
