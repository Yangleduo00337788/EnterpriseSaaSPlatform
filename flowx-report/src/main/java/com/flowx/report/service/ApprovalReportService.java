package com.flowx.report.service;

import com.flowx.report.dto.DateRangeDTO;
import com.flowx.report.vo.ApprovalStatsVO;

/**
 * Approval report service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface ApprovalReportService {

    /**
     * Get approval statistics
     *
     * @param dateRange date range
     * @return approval statistics
     */
    ApprovalStatsVO getApprovalStats(DateRangeDTO dateRange);

    /**
     * Get approval statistics for a specific user
     *
     * @param userId    user ID
     * @param dateRange date range
     * @return approval statistics
     */
    ApprovalStatsVO getMyApprovalStats(Long userId, DateRangeDTO dateRange);
}
