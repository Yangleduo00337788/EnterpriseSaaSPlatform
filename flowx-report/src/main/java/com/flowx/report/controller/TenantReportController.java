package com.flowx.report.controller;

import com.flowx.common.core.result.R;
import com.flowx.report.service.TenantReportService;
import com.flowx.report.vo.TenantStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tenant report controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/report/tenant")
@RequiredArgsConstructor
public class TenantReportController {

    private final TenantReportService tenantReportService;

    /**
     * Get tenant statistics
     *
     * @return tenant statistics
     */
    @GetMapping("/stats")
    public R<TenantStatsVO> getTenantStats() {
        TenantStatsVO stats = tenantReportService.getTenantStats();
        return R.ok(stats);
    }
}