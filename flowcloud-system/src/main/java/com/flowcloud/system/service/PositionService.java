package com.flowcloud.system.service;

import com.flowcloud.system.dto.PositionDTO;
import com.flowcloud.system.vo.PositionVO;

import java.util.List;

public interface PositionService {

    List<PositionVO> listAll();

    void create(PositionDTO dto);

    void update(PositionDTO dto);

    void delete(Long id);

    /** 给用户分配岗位（覆盖写） */
    void assignUserPositions(Long userId, List<Long> positionIds);

    /** 查询用户已有岗位 */
    List<Long> getUserPositionIds(Long userId);
}