package com.flowx.report.service.impl;

import com.flowx.report.dto.DateRangeDTO;
import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.ApprovalReportService;
import com.flowx.report.vo.ApprovalStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Approval report service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalReportServiceImpl implements ApprovalReportService {

    private final RptReportMapper reportMapper;

    @Override
    public ApprovalStatsVO getApprovalStats(DateRangeDTO dateRange) {
        LocalDateTime startDate = toStartDateTime(dateRange.getStartDate());
        LocalDateTime endDate = toEndDateTime(dateRange.getEndDate());

        List<Map<String, Object>> statusStats = reportMapper.getApprovalStatsByStatus(startDate, endDate);
        Double avgProcessTime = reportMapper.getAvgProcessTime(startDate, endDate);
        List<Map<String, Object>> dailyTrend = reportMapper.getDailyApprovalTrend(startDate, endDate);

        return buildApprovalStatsVO(statusStats, avgProcessTime, dailyTrend);
    }

    @Override
    public ApprovalStatsVO getMyApprovalStats(Long userId, DateRangeDTO dateRange) {
        LocalDateTime startDate = toStartDateTime(dateRange.getStartDate());
        LocalDateTime endDate = toEndDateTime(dateRange.getEndDate());

        List<Map<String, Object>> statusStats = reportMapper.getMyApprovalStatsByStatus(userId, startDate, endDate);
        Double avgProcessTime = reportMapper.getMyAvgProcessTime(userId, startDate, endDate);
        List<Map<String, Object>> dailyTrend = reportMapper.getMyDailyApprovalTrend(userId, startDate, endDate);

        return buildApprovalStatsVO(statusStats, avgProcessTime, dailyTrend);
    }

    /**
     * Build ApprovalStatsVO from raw data
     */
    private ApprovalStatsVO buildApprovalStatsVO(
            List<Map<String, Object>> statusStats,
            Double avgProcessTime,
            List<Map<String, Object>> dailyTrend) {

        long totalCount = 0;
        long approvedCount = 0;
        long rejectedCount = 0;
        long pendingCount = 0;

        for (Map<String, Object> stat : statusStats) {
            Object statusObj = stat.get("status");
            Object countObj = stat.get("count");
            if (statusObj == null || countObj == null) {
                continue;
            }
            int status = Integer.parseInt(statusObj.toString());
            long count = Long.parseLong(countObj.toString());
            totalCount += count;

            // Status: 0=pending, 1=in_progress, 2=approved, 3=rejected
            switch (status) {
                case 0:
                case 1:
                    pendingCount += count;
                    break;
                case 2:
                    approvedCount += count;
                    break;
                case 3:
                    rejectedCount += count;
                    break;
                default:
                    break;
            }
        }

        return ApprovalStatsVO.builder()
                .totalCount(totalCount)
                .approvedCount(approvedCount)
                .rejectedCount(rejectedCount)
                .pendingCount(pendingCount)
                .avgProcessTime(avgProcessTime != null ? Math.round(avgProcessTime * 100.0) / 100.0 : 0.0)
                .dailyTrend(dailyTrend)
                .build();
    }

    /**
     * Convert LocalDate to start of day LocalDateTime
     */
    private LocalDateTime toStartDateTime(LocalDate date) {
        if (date == null) {
            return LocalDateTime.now().minusMonths(1);
        }
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * Convert LocalDate to end of day LocalDateTime
     */
    private LocalDateTime toEndDateTime(LocalDate date) {
        if (date == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.of(date, LocalTime.MAX);
    }
}
