package com.flowx.user.service;

import com.flowx.user.dto.PositionDTO;
import com.flowx.user.vo.PositionVO;

import java.util.List;

/**
 * Position service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface PositionService {

    /**
     * Get position by ID
     *
     * @param positionId position ID
     * @return position VO
     */
    PositionVO getPositionById(Long positionId);

    /**
     * Create new position
     *
     * @param dto position creation DTO
     * @return created position ID
     */
    Long createPosition(PositionDTO dto);

    /**
     * Update existing position
     *
     * @param positionId position ID
     * @param dto        position update DTO
     */
    void updatePosition(Long positionId, PositionDTO dto);

    /**
     * Delete position (soft delete)
     *
     * @param positionId position ID
     */
    void deletePosition(Long positionId);

    /**
     * List all positions
     *
     * @return list of position VOs
     */
    List<PositionVO> listPositions();
}
