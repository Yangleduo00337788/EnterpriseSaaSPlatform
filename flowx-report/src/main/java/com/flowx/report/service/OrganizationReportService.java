package com.flowx.report.service;

import com.flowx.report.vo.OrganizationStatsVO;

/**
 * Organization report service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface OrganizationReportService {

    /**
     * Get organization statistics
     *
     * @return organization statistics
     */
    OrganizationStatsVO getOrganizationStats();
}
