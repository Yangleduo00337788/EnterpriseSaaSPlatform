package com.flowx.report.service.impl;

import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.TenantReportService;
import com.flowx.report.vo.TenantStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Tenant report service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantReportServiceImpl implements TenantReportService {

    private final RptReportMapper reportMapper;

    @Override
    public TenantStatsVO getTenantStats() {
        Long totalTenants = reportMapper.getTotalTenants();
        Long activeTenants = reportMapper.getActiveTenants();
        List<Map<String, Object>> packageDistribution = reportMapper.getPackageDistribution();
        List<Map<String, Object>> monthlyGrowth = reportMapper.getMonthlyTenantGrowth();

        return TenantStatsVO.builder()
                .totalTenants(totalTenants != null ? totalTenants : 0L)
                .activeTenants(activeTenants != null ? activeTenants : 0L)
                .packageDistribution(packageDistribution)
                .monthlyGrowth(monthlyGrowth)
                .build();
    }
}
