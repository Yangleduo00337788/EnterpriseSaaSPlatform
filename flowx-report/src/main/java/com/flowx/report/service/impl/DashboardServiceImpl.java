package com.flowx.report.service.impl;

import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RptReportMapper reportMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        // User stats
        Long totalUsers = reportMapper.getTotalEmployees();
        Long newUsersToday = reportMapper.getNewEmployeesThisMonth(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        Long totalDepts = reportMapper.getTotalDepts();
        Long totalPositions = reportMapper.getTotalPositions();

        // Tenant stats
        Long totalTenants = reportMapper.getTotalTenants();
        Long activeTenants = reportMapper.getActiveTenants();

        // Approval stats (last 30 days)
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<Map<String, Object>> approvalStats = reportMapper.getApprovalStatsByStatus(startDate, endDate);

        long totalApprovals = 0;
        long pendingApprovals = 0;
        long approvedApprovals = 0;
        long rejectedApprovals = 0;

        for (Map<String, Object> stat : approvalStats) {
            Object statusObj = stat.get("status");
            Object countObj = stat.get("count");
            if (statusObj == null || countObj == null) continue;
            int status = Integer.parseInt(statusObj.toString());
            long count = Long.parseLong(countObj.toString());
            totalApprovals += count;
            if (status == 0 || status == 1) pendingApprovals += count;
            else if (status == 2) approvedApprovals += count;
            else if (status == 3) rejectedApprovals += count;
        }

        double completionRate = totalApprovals > 0
                ? Math.round(approvedApprovals * 1000.0 / totalApprovals) / 10.0
                : 0;

        // Daily trend (last 7 days)
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        List<Map<String, Object>> dailyTrend = reportMapper.getDailyApprovalTrend(weekStart, endDate);

        // Today submissions
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayOperations = reportMapper.getTodayOperations(todayStart);

        // Build result
        result.put("totalUsers", totalUsers != null ? totalUsers : 0);
        result.put("newUsersToday", newUsersToday != null ? newUsersToday : 0);
        result.put("totalDepts", totalDepts != null ? totalDepts : 0);
        result.put("totalPositions", totalPositions != null ? totalPositions : 0);
        result.put("totalTenants", totalTenants != null ? totalTenants : 0);
        result.put("activeTenants", activeTenants != null ? activeTenants : 0);
        result.put("totalApprovals", totalApprovals);
        result.put("pendingApprovals", pendingApprovals);
        result.put("approvedApprovals", approvedApprovals);
        result.put("rejectedApprovals", rejectedApprovals);
        result.put("completionRate", completionRate);
        result.put("todaySubmissions", todayOperations != null ? todayOperations : 0);
        result.put("dailyTrend", dailyTrend);

        return result;
    }
}