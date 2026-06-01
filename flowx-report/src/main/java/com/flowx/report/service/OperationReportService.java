package com.flowx.report.service;

import com.flowx.report.dto.DateRangeDTO;
import com.flowx.report.vo.OperationStatsVO;

/**
 * Operation report service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface OperationReportService {

    /**
     * Get operation statistics
     *
     * @param dateRange date range
     * @return operation statistics
     */
    OperationStatsVO getOperationStats(DateRangeDTO dateRange);
}
