package com.flowx.report.service;

import com.flowx.report.vo.EmployeeStatsVO;

/**
 * Employee report service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface EmployeeReportService {

    /**
     * Get employee statistics
     *
     * @return employee statistics
     */
    EmployeeStatsVO getEmployeeStats();
}
