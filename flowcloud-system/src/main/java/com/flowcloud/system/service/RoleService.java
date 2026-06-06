package com.flowcloud.system.service;

import com.flowcloud.system.dto.RoleDTO;
import com.flowcloud.system.vo.RoleOptionVO;
import com.flowcloud.system.vo.RoleVO;

import java.util.List;

public interface RoleService {

    List<RoleOptionVO> listOptions();

    List<RoleVO> listAll();

    RoleVO getById(Long id);

    void create(RoleDTO dto);

    void update(RoleDTO dto);

    void delete(Long id);
}
