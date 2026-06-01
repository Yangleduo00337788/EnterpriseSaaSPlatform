package com.flowx.user.mapper;

import com.flowx.user.entity.SysUser;
import com.flowx.user.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Set;

/**
 * System user mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * Select user by username
     *
     * @param username username
     * @return user entity or null
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * Select user by email
     *
     * @param email email
     * @return user entity or null
     */
    SysUser selectUserByEmail(@Param("email") String email);

    /**
     * Select user by phone
     *
     * @param phone phone number
     * @return user entity or null
     */
    SysUser selectUserByPhone(@Param("phone") String phone);

    /**
     * Select roles assigned to user
     *
     * @param userId user ID
     * @return list of roles
     */
    List<SysRole> selectUserRoles(@Param("userId") Long userId);

    /**
     * Select permission identifiers for user
     *
     * @param userId user ID
     * @return set of permission strings
     */
    Set<String> selectUserPermissions(@Param("userId") Long userId);
}
