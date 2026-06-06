package com.flowcloud.system.service;

import com.flowcloud.system.vo.PermissionVO;

import java.util.List;

public interface PermissionService {

    List<PermissionVO> listTree();
}
