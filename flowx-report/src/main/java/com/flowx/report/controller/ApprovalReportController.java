package com.flowx.report.controller;

import com.flowx.common.core.result.R;
import com.flowx.report.dto.DateRangeDTO;
import com.flowx.report.service.ApprovalReportService;
import com.flowx.report.vo.ApprovalStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Approval report controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/reports/approvals")
@RequiredArgsConstructor
public class ApprovalReportController {

    private final ApprovalReportService approvalReportService;

    /**
     * Get approval statistics
     *
     * @param dateRange date range (optional, defaults to last 30 days)
     * @return approval statistics
     */
    @GetMapping("/stats")
    public R<ApprovalStatsVO> getApprovalStats(DateRangeDTO dateRange) {
        ApprovalStatsVO stats = approvalReportService.getApprovalStats(dateRange);
        return R.ok(stats);
    }

    /**
     * Get current user's approval statistics
     *
     * @param userId    current user ID
     * @param dateRange date range (optional)
     * @return approval statistics
     */
    @GetMapping("/my-stats")
    public R<ApprovalStatsVO> getMyApprovalStats(
            @RequestParam("userId") Long userId,
            DateRangeDTO dateRange) {
        ApprovalStatsVO stats = approvalReportService.getMyApprovalStats(userId, dateRange);
        return R.ok(stats);
    }
}
