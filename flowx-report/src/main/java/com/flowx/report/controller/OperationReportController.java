package com.flowx.report.controller;

import com.flowx.common.core.result.R;
import com.flowx.report.dto.DateRangeDTO;
import com.flowx.report.service.OperationReportService;
import com.flowx.report.vo.OperationStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Operation report controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/reports/operations")
@RequiredArgsConstructor
public class OperationReportController {

    private final OperationReportService operationReportService;

    /**
     * Get operation statistics
     *
     * @param dateRange date range (optional, defaults to last 7 days)
     * @return operation statistics
     */
    @GetMapping("/stats")
    public R<OperationStatsVO> getOperationStats(DateRangeDTO dateRange) {
        OperationStatsVO stats = operationReportService.getOperationStats(dateRange);
        return R.ok(stats);
    }
}
