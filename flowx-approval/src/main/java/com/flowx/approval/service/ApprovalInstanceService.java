package com.flowx.approval.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.approval.dto.ApprovalQueryDTO;
import com.flowx.approval.dto.ApprovalSubmitDTO;
import com.flowx.approval.vo.ApprovalInstanceVO;

/**
 * Approval instance service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface ApprovalInstanceService {

    /**
     * Submit a new approval
     *
     * @param dto approval submit DTO
     * @return created approval instance ID
     */
    Long submit(ApprovalSubmitDTO dto);

    /**
     * Get approvals submitted by current user
     *
     * @param queryDTO query parameters
     * @return paginated approval list
     */
    PageResult<ApprovalInstanceVO> getMyApprovals(ApprovalQueryDTO queryDTO);

    /**
     * Get pending approvals for current user to review
     *
     * @param queryDTO query parameters
     * @return paginated approval list
     */
    PageResult<ApprovalInstanceVO> getPendingApprovals(ApprovalQueryDTO queryDTO);

    /**
     * Get approval detail
     *
     * @param id approval instance ID
     * @return approval instance VO
     */
    ApprovalInstanceVO getApprovalDetail(Long id);

    /**
     * Withdraw an approval (cancel the flow)
     *
     * @param id approval instance ID
     */
    void withdraw(Long id);

    /**
     * Send reminder to current assignee
     *
     * @param id approval instance ID
     */
    void remind(Long id);
}
