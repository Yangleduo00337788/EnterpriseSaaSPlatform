package com.flowx.report.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.report.entity.RptReportConfig;
import com.flowx.report.service.ReportConfigService;
import com.flowx.report.vo.ReportConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Report configuration controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/reports/configs")
@RequiredArgsConstructor
public class ReportConfigController {

    private final ReportConfigService reportConfigService;

    /**
     * Get report config by ID
     *
     * @param id config ID
     * @return config VO
     */
    @GetMapping("/{id}")
    public R<ReportConfigVO> getConfigById(@PathVariable("id") Long id) {
        ReportConfigVO vo = reportConfigService.getConfigById(id);
        return R.ok(vo);
    }

    /**
     * List report configs with pagination
     *
     * @param pageNum    page number
     * @param pageSize   page size
     * @param reportType optional type filter
     * @return paginated config list
     */
    @GetMapping("/list")
    public R<PageResult<ReportConfigVO>> listConfigs(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "reportType", required = false) String reportType) {
        PageResult<ReportConfigVO> result = reportConfigService.listConfigs(pageNum, pageSize, reportType);
        return R.ok(result);
    }

    /**
     * Create new report config
     *
     * @param config report config data
     * @return created config ID
     */
    @PostMapping
    public R<Long> createConfig(@RequestBody RptReportConfig config) {
        Long configId = reportConfigService.createConfig(config);
        return R.ok(configId);
    }

    /**
     * Update existing report config
     *
     * @param id     config ID
     * @param config report config data
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateConfig(@PathVariable("id") Long id, @RequestBody RptReportConfig config) {
        reportConfigService.updateConfig(id, config);
        return R.ok();
    }

    /**
     * Delete report config
     *
     * @param id config ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable("id") Long id) {
        reportConfigService.deleteConfig(id);
        return R.ok();
    }
}
