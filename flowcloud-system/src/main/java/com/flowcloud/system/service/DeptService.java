package com.flowcloud.system.service;

import com.flowcloud.system.dto.DeptDTO;
import com.flowcloud.system.vo.DeptVO;

import java.util.List;

public interface DeptService {

    List<DeptVO> listTree();

    void create(DeptDTO dto);

    void update(DeptDTO dto);

    void delete(Long id);
}