package com.flowcloud.system.service;

import com.flowcloud.common.result.PageResult;
import com.flowcloud.system.dto.UserDTO;
import com.flowcloud.system.vo.UserOptionVO;
import com.flowcloud.system.vo.UserVO;

import java.util.List;

public interface SysUserService {

    PageResult<UserVO> pageUsers(String keyword, Long deptId, int pageNum, int pageSize);

    UserVO getById(Long id);

    void createUser(UserDTO dto);

    void updateUser(UserDTO dto);

    void deleteUser(Long id);

    void resetPassword(Long id, String newPassword);

    void toggleStatus(Long id);

    List<UserOptionVO> listOptions();
}
