package com.flowx.approval.service;

import com.flowx.approval.dto.ApprovalTypeDTO;
import com.flowx.approval.vo.ApprovalTypeVO;

import java.util.List;

/**
 * Approval type service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface ApprovalTypeService {

    /**
     * Get approval type by ID
     *
     * @param typeId type ID
     * @return type VO
     */
    ApprovalTypeVO getTypeById(Long typeId);

    /**
     * Create new approval type
     *
     * @param dto type creation DTO
     * @return created type ID
     */
    Long createType(ApprovalTypeDTO dto);

    /**
     * Update existing approval type
     *
     * @param typeId type ID
     * @param dto    type update DTO
     */
    void updateType(Long typeId, ApprovalTypeDTO dto);

    /**
     * Delete approval type (soft delete)
     *
     * @param typeId type ID
     */
    void deleteType(Long typeId);

    /**
     * List all approval types
     *
     * @return list of type VOs
     */
    List<ApprovalTypeVO> listTypes();
}
