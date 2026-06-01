package com.flowx.approval.controller;

import com.flowx.approval.dto.ApprovalQueryDTO;
import com.flowx.approval.dto.ApprovalSubmitDTO;
import com.flowx.approval.service.ApprovalInstanceService;
import com.flowx.approval.vo.ApprovalInstanceVO;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Approval instance management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/approval/instances")
@RequiredArgsConstructor
public class ApprovalInstanceController {

    private final ApprovalInstanceService instanceService;

    /**
     * Submit a new approval
     *
     * @param dto approval submit DTO
     * @return created approval instance ID
     */
    @PostMapping("/submit")
    public R<Long> submit(@Valid @RequestBody ApprovalSubmitDTO dto) {
        Long instanceId = instanceService.submit(dto);
        return R.ok(instanceId);
    }

    /**
     * List approvals submitted by current user
     *
     * @param queryDTO query parameters
     * @return paginated approval list
     */
    @GetMapping("/my")
    public R<PageResult<ApprovalInstanceVO>> getMyApprovals(ApprovalQueryDTO queryDTO) {
        PageResult<ApprovalInstanceVO> result = instanceService.getMyApprovals(queryDTO);
        return R.ok(result);
    }

    /**
     * List pending approvals for current user to review
     *
     * @param queryDTO query parameters
     * @return paginated approval list
     */
    @GetMapping("/pending")
    public R<PageResult<ApprovalInstanceVO>> getPendingApprovals(ApprovalQueryDTO queryDTO) {
        PageResult<ApprovalInstanceVO> result = instanceService.getPendingApprovals(queryDTO);
        return R.ok(result);
    }

    /**
     * Get approval detail
     *
     * @param id approval instance ID
     * @return approval instance VO
     */
    @GetMapping("/{id}")
    public R<ApprovalInstanceVO> getApprovalDetail(@PathVariable("id") Long id) {
        ApprovalInstanceVO vo = instanceService.getApprovalDetail(id);
        return R.ok(vo);
    }

    /**
     * Withdraw an approval
     *
     * @param id approval instance ID
     * @return success response
     */
    @PutMapping("/{id}/withdraw")
    public R<Void> withdraw(@PathVariable("id") Long id) {
        instanceService.withdraw(id);
        return R.ok();
    }

    /**
     * Send reminder to current assignee
     *
     * @param id approval instance ID
     * @return success response
     */
    @PutMapping("/{id}/remind")
    public R<Void> remind(@PathVariable("id") Long id) {
        instanceService.remind(id);
        return R.ok();
    }
}
