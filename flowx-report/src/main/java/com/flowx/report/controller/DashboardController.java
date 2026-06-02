package com.flowx.report.controller;

import com.flowx.common.core.result.R;
import com.flowx.report.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/report/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public R<Map<String, Object>> getStats() {
        return R.ok(dashboardService.getDashboardStats());
    }
}