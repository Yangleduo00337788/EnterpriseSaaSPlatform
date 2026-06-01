package com.flowx.report.service.impl;

import com.flowx.report.dto.DateRangeDTO;
import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.OperationReportService;
import com.flowx.report.vo.OperationStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Operation report service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationReportServiceImpl implements OperationReportService {

    private final RptReportMapper reportMapper;

    @Override
    public OperationStatsVO getOperationStats(DateRangeDTO dateRange) {
        LocalDateTime startDate = toStartDateTime(dateRange.getStartDate());
        LocalDateTime endDate = toEndDateTime(dateRange.getEndDate());

        Long totalOperations = reportMapper.getTotalOperations();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayOperations = reportMapper.getTodayOperations(todayStart);

        List<Map<String, Object>> operationTypeDistribution = reportMapper.getOperationTypeDistribution(startDate, endDate);
        List<Map<String, Object>> hourlyTrend = reportMapper.getHourlyOperationTrend(startDate, endDate);

        return OperationStatsVO.builder()
                .totalOperations(totalOperations != null ? totalOperations : 0L)
                .todayOperations(todayOperations != null ? todayOperations : 0L)
                .operationTypeDistribution(operationTypeDistribution)
                .hourlyTrend(hourlyTrend)
                .build();
    }

    private LocalDateTime toStartDateTime(LocalDate date) {
        if (date == null) {
            return LocalDateTime.now().minusDays(7);
        }
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    private LocalDateTime toEndDateTime(LocalDate date) {
        if (date == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.of(date, LocalTime.MAX);
    }
}
