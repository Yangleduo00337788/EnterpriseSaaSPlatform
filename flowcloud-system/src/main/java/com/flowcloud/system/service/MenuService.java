package com.flowcloud.system.service;

import com.flowcloud.system.dto.MenuDTO;
import com.flowcloud.system.vo.MenuVO;

import java.util.List;

public interface MenuService {

    List<MenuVO> listTree();

    List<MenuVO> listCurrentUserTree();

    void create(MenuDTO dto);

    void update(MenuDTO dto);

    void delete(Long id);
}
