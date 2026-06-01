package com.flowx.report.service;

import com.flowx.report.vo.TenantStatsVO;

/**
 * Tenant report service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface TenantReportService {

    /**
     * Get tenant statistics
     *
     * @return tenant statistics
     */
    TenantStatsVO getTenantStats();
}
