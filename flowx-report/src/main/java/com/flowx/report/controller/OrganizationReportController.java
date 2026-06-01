package com.flowx.report.controller;

import com.flowx.common.core.result.R;
import com.flowx.report.service.OrganizationReportService;
import com.flowx.report.vo.OrganizationStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Organization report controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/reports/organizations")
@RequiredArgsConstructor
public class OrganizationReportController {

    private final OrganizationReportService organizationReportService;

    /**
     * Get organization statistics
     *
     * @return organization statistics
     */
    @GetMapping("/stats")
    public R<OrganizationStatsVO> getOrganizationStats() {
        OrganizationStatsVO stats = organizationReportService.getOrganizationStats();
        return R.ok(stats);
    }
}
