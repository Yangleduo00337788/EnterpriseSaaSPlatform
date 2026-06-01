package com.flowx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.infrastructure.redis.CacheManager;
import com.flowx.user.convert.RoleConvert;
import com.flowx.user.convert.UserConvert;
import com.flowx.user.dto.UserDTO;
import com.flowx.user.dto.UserQueryDTO;
import com.flowx.user.entity.SysRole;
import com.flowx.user.entity.SysUser;
import com.flowx.user.entity.SysUserRole;
import com.flowx.user.mapper.SysDeptMapper;
import com.flowx.user.mapper.SysPositionMapper;
import com.flowx.user.mapper.SysUserMapper;
import com.flowx.user.mapper.SysUserRoleMapper;
import com.flowx.user.service.UserService;
import com.flowx.user.vo.RoleVO;
import com.flowx.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysDeptMapper deptMapper;
    private final SysPositionMapper positionMapper;
    private final UserConvert userConvert;
    private final RoleConvert roleConvert;
    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO getUserById(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        SysUser user = userMapper.selectById(userId);
        AssertUtil.notNull(user, ResultCodeEnum.USER_NOT_FOUND.getCode(), ResultCodeEnum.USER_NOT_FOUND.getMessage());
        return buildUserVO(user);
    }

    @Override
    public UserVO getUserByUsername(String username) {
        AssertUtil.notBlank(username, "用户名不能为空");
        SysUser user = userMapper.selectUserByUsername(username);
        if (user == null) {
            return null;
        }
        return buildUserVO(user);
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserVO getUserInfo(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        return (UserVO) cacheManager.getUserInfo(userId, () -> {
            SysUser user = userMapper.selectById(userId);
            if (user == null) {
                return null;
            }
            return buildUserVO(user);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO dto) {
        AssertUtil.notNull(dto, "用户信息不能为空");
        AssertUtil.notBlank(dto.getUsername(), "用户名不能为空");

        // Check username uniqueness
        SysUser existing = userMapper.selectUserByUsername(dto.getUsername());
        if (existing != null) {
            throw new BizException(ResultCodeEnum.DUPLICATE_USERNAME);
        }

        // Check email uniqueness if provided
        if (StringUtils.hasText(dto.getEmail())) {
            existing = userMapper.selectUserByEmail(dto.getEmail());
            if (existing != null) {
                throw new BizException(ResultCodeEnum.DUPLICATE_EMAIL);
            }
        }

        // Check phone uniqueness if provided
        if (StringUtils.hasText(dto.getPhone())) {
            existing = userMapper.selectUserByPhone(dto.getPhone());
            if (existing != null) {
                throw new BizException(ResultCodeEnum.DUPLICATE_PHONE);
            }
        }

        SysUser user = userConvert.toEntity(dto);

        // Encode password
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            // Default password
            user.setPassword(passwordEncoder.encode("flowx123456"));
        }

        // Set default status if not provided
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        userMapper.insert(user);
        log.info("Created user: {}", user.getUsername());

        // Assign roles if provided
        if (!CollectionUtils.isEmpty(dto.getRoleIds())) {
            assignRoles(user.getId(), dto.getRoleIds());
        }

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UserDTO dto) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        AssertUtil.notNull(dto, "用户信息不能为空");

        SysUser user = userMapper.selectById(userId);
        AssertUtil.notNull(user, ResultCodeEnum.USER_NOT_FOUND.getCode(), ResultCodeEnum.USER_NOT_FOUND.getMessage());

        // Check username uniqueness if changed
        if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(user.getUsername())) {
            SysUser existing = userMapper.selectUserByUsername(dto.getUsername());
            if (existing != null) {
                throw new BizException(ResultCodeEnum.DUPLICATE_USERNAME);
            }
        }

        // Check email uniqueness if changed
        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(user.getEmail())) {
            SysUser existing = userMapper.selectUserByEmail(dto.getEmail());
            if (existing != null) {
                throw new BizException(ResultCodeEnum.DUPLICATE_EMAIL);
            }
        }

        // Check phone uniqueness if changed
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
            SysUser existing = userMapper.selectUserByPhone(dto.getPhone());
            if (existing != null) {
                throw new BizException(ResultCodeEnum.DUPLICATE_PHONE);
            }
        }

        userConvert.updateEntity(dto, user);
        userMapper.updateById(user);
        log.info("Updated user: {}", userId);

        // Evict cache
        cacheManager.evictUserInfo(userId);

        // Reassign roles if provided
        if (dto.getRoleIds() != null) {
            assignRoles(userId, dto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        SysUser user = userMapper.selectById(userId);
        AssertUtil.notNull(user, ResultCodeEnum.USER_NOT_FOUND.getCode(), ResultCodeEnum.USER_NOT_FOUND.getMessage());

        // Soft delete - MyBatis-Plus @TableLogic handles this
        userMapper.deleteById(userId);

        // Delete user-role associations
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        userRoleMapper.delete(wrapper);

        // Evict cache
        cacheManager.evictUserInfo(userId);
        log.info("Deleted user: {}", userId);
    }

    @Override
    public PageResult<UserVO> listUsers(UserQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");

        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        QueryWrapper<SysUser> wrapper = buildUserQueryWrapper(queryDTO);

        Page<SysUser> userPage = userMapper.selectPage(page, wrapper);
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::buildUserVO)
                .collect(Collectors.toList());

        return PageResult.of(userPage.getTotal(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        AssertUtil.notNull(userId, "用户ID不能为空");

        // Remove existing user-role associations
        QueryWrapper<SysUserRole> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId);
        userRoleMapper.delete(deleteWrapper);

        // Insert new associations
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
            log.info("Assigned roles {} to user {}", roleIds, userId);
        }

        // Evict cache
        cacheManager.evictUserInfo(userId);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        AssertUtil.notBlank(newPassword, "新密码不能为空");

        SysUser user = userMapper.selectById(userId);
        AssertUtil.notNull(user, ResultCodeEnum.USER_NOT_FOUND.getCode(), ResultCodeEnum.USER_NOT_FOUND.getMessage());

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        log.info("Reset password for user: {}", userId);

        // Evict cache
        cacheManager.evictUserInfo(userId);
    }

    /**
     * Build UserVO with department, position, and role details
     */
    private UserVO buildUserVO(SysUser user) {
        UserVO vo = userConvert.toVO(user);

        // Set department name
        if (user.getDeptId() != null) {
            var dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        // Set position name
        if (user.getPositionId() != null) {
            var position = positionMapper.selectById(user.getPositionId());
            if (position != null) {
                vo.setPositionName(position.getPositionName());
            }
        }

        // Set roles
        List<SysRole> roles = userMapper.selectUserRoles(user.getId());
        if (!CollectionUtils.isEmpty(roles)) {
            List<RoleVO> roleVOs = roleConvert.toVOList(roles);
            vo.setRoles(roleVOs);
        } else {
            vo.setRoles(new ArrayList<>());
        }

        return vo;
    }

    /**
     * Build query wrapper from UserQueryDTO
     */
    private QueryWrapper<SysUser> buildUserQueryWrapper(UserQueryDTO queryDTO) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like("username", queryDTO.getUsername());
        }
        if (StringUtils.hasText(queryDTO.getNickname())) {
            wrapper.like("nickname", queryDTO.getNickname());
        }
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.like("phone", queryDTO.getPhone());
        }
        if (StringUtils.hasText(queryDTO.getEmail())) {
            wrapper.like("email", queryDTO.getEmail());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }
        if (queryDTO.getDeptId() != null) {
            wrapper.eq("dept_id", queryDTO.getDeptId());
        }
        if (queryDTO.getRoleId() != null) {
            wrapper.inSql("id", "SELECT user_id FROM sys_user_role WHERE role_id = " + queryDTO.getRoleId());
        }

        wrapper.orderByDesc("create_time");
        return wrapper;
    }
}
