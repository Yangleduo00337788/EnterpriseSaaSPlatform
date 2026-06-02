package com.flowx.user.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.user.dto.UserDTO;
import com.flowx.user.dto.UserQueryDTO;
import com.flowx.user.service.UserService;
import com.flowx.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * User management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get user by ID
     *
     * @param id user ID
     * @return user VO
     */
    @GetMapping("/{id}")
    public R<UserVO> getUserById(@PathVariable("id") Long id) {
        UserVO userVO = userService.getUserById(id);
        return R.ok(userVO);
    }

    /**
     * List users with pagination
     *
     * @param queryDTO query parameters
     * @return paginated user list
     */
    @GetMapping("/list")
    public R<PageResult<UserVO>> listUsers(UserQueryDTO queryDTO) {
        PageResult<UserVO> result = userService.listUsers(queryDTO);
        return R.ok(result);
    }

    /**
     * Create new user
     *
     * @param dto user creation DTO
     * @return created user ID
     */
    @PostMapping
    public R<Long> createUser(@Valid @RequestBody UserDTO dto) {
        Long userId = userService.createUser(dto);
        return R.ok(userId);
    }

    /**
     * Update existing user (id from request body)
     *
     * @param dto user update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateUser(@Valid @RequestBody UserDTO dto) {
        userService.updateUser(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete users by IDs (comma-separated)
     *
     * @param ids user IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteUsers(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            userService.deleteUser(id);
        }
        return R.ok();
    }

    /**
     * Reset user password
     *
     * @param id      user ID
     * @param request password reset request containing new password
     * @return success response
     */
    @PutMapping("/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable("id") Long id, @RequestBody PasswordResetRequest request) {
        userService.resetPassword(id, request.getNewPassword());
        return R.ok();
    }

    /**
     * Assign roles to user
     *
     * @param id      user ID
     * @param request role assignment request containing role IDs
     * @return success response
     */
    @PutMapping("/{id}/roles")
    public R<Void> assignRoles(@PathVariable("id") Long id, @RequestBody RoleAssignRequest request) {
        userService.assignRoles(id, request.getRoleIds());
        return R.ok();
    }

    /**
     * Password reset request DTO
     */
    @lombok.Data
    public static class PasswordResetRequest {
        private String newPassword;
    }

    /**
     * Role assignment request DTO
     */
    @lombok.Data
    public static class RoleAssignRequest {
        private List<Long> roleIds;
    }
}