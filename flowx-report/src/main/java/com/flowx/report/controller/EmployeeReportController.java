package com.flowx.report.controller;

import com.flowx.common.core.result.R;
import com.flowx.report.service.EmployeeReportService;
import com.flowx.report.vo.EmployeeStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Employee report controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/reports/employees")
@RequiredArgsConstructor
public class EmployeeReportController {

    private final EmployeeReportService employeeReportService;

    /**
     * Get employee statistics
     *
     * @return employee statistics
     */
    @GetMapping("/stats")
    public R<EmployeeStatsVO> getEmployeeStats() {
        EmployeeStatsVO stats = employeeReportService.getEmployeeStats();
        return R.ok(stats);
    }
}
