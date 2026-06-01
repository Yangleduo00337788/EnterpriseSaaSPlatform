package com.flowx.report.service.impl;

import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.OrganizationReportService;
import com.flowx.report.vo.OrganizationStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Organization report service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationReportServiceImpl implements OrganizationReportService {

    private final RptReportMapper reportMapper;

    @Override
    public OrganizationStatsVO getOrganizationStats() {
        Long totalDepts = reportMapper.getTotalDepts();
        Long totalPositions = reportMapper.getTotalPositions();
        List<Map<String, Object>> deptEmployeeCounts = reportMapper.getDeptEmployeeCounts();

        return OrganizationStatsVO.builder()
                .totalDepts(totalDepts != null ? totalDepts : 0L)
                .totalPositions(totalPositions != null ? totalPositions : 0L)
                .deptEmployeeCounts(deptEmployeeCounts)
                .build();
    }
}
