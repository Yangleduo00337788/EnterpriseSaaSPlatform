package com.flowcloud.report.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.report.service.ReportService;
import com.flowcloud.report.vo.DashboardVO;
import com.flowcloud.report.vo.ReportAnalyticsVO;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据报表")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "仪表盘数据")
    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        roleAuthService.requireAnyPermission(PermissionCodes.DASHBOARD, PermissionCodes.REPORT);
        return Result.ok(reportService.getDashboard());
    }

    @Operation(summary = "报表分析")
    @GetMapping("/analytics")
    public Result<ReportAnalyticsVO> analytics() {
        roleAuthService.requireAnyPermission(PermissionCodes.REPORT, PermissionCodes.DASHBOARD);
        return Result.ok(reportService.getAnalytics());
    }
}
