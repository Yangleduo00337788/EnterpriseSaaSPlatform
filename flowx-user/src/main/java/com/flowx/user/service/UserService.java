package com.flowx.user.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.user.dto.UserDTO;
import com.flowx.user.dto.UserQueryDTO;
import com.flowx.user.vo.UserVO;

import java.util.List;

/**
 * User service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface UserService {

    /**
     * Get user by ID
     *
     * @param userId user ID
     * @return user VO
     */
    UserVO getUserById(Long userId);

    /**
     * Get user by username
     *
     * @param username username
     * @return user VO or null
     */
    UserVO getUserByUsername(String username);

    /**
     * Get full user info with dept, position, and roles
     *
     * @param userId user ID
     * @return user VO with full details
     */
    UserVO getUserInfo(Long userId);

    /**
     * Create new user
     *
     * @param dto user creation DTO
     * @return created user ID
     */
    Long createUser(UserDTO dto);

    /**
     * Update existing user
     *
     * @param userId user ID
     * @param dto    user update DTO
     */
    void updateUser(Long userId, UserDTO dto);

    /**
     * Delete user (soft delete)
     *
     * @param userId user ID
     */
    void deleteUser(Long userId);

    /**
     * List users with pagination and filters
     *
     * @param queryDTO query parameters
     * @return paginated user list
     */
    PageResult<UserVO> listUsers(UserQueryDTO queryDTO);

    /**
     * Assign roles to user
     *
     * @param userId user ID
     * @param roleIds role IDs to assign
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * Reset user password
     *
     * @param userId      user ID
     * @param newPassword new password (plain text, will be encrypted)
     */
    void resetPassword(Long userId, String newPassword);
}
