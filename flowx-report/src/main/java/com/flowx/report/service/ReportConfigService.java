package com.flowx.report.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.report.entity.RptReportConfig;
import com.flowx.report.vo.ReportConfigVO;

/**
 * Report configuration service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface ReportConfigService {

    /**
     * Get report config by ID
     *
     * @param configId config ID
     * @return config VO
     */
    ReportConfigVO getConfigById(Long configId);

    /**
     * Create new report config
     *
     * @param config report config entity
     * @return created config ID
     */
    Long createConfig(RptReportConfig config);

    /**
     * Update existing report config
     *
     * @param configId config ID
     * @param config   report config entity
     */
    void updateConfig(Long configId, RptReportConfig config);

    /**
     * Delete report config (soft delete)
     *
     * @param configId config ID
     */
    void deleteConfig(Long configId);

    /**
     * List report configs with pagination
     *
     * @param pageNum    page number
     * @param pageSize   page size
     * @param reportType optional type filter
     * @return paginated config list
     */
    PageResult<ReportConfigVO> listConfigs(Integer pageNum, Integer pageSize, String reportType);
}
