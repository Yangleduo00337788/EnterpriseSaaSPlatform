package com.flowx.report.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Report configuration entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("rpt_report_config")
public class RptReportConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Report name
     */
    @Column("report_name")
    private String reportName;

    /**
     * Report code (unique identifier)
     */
    @Column("report_code")
    private String reportCode;

    /**
     * Report type
     */
    @Column("report_type")
    private String reportType;

    /**
     * Data source
     */
    @Column("data_source")
    private String dataSource;

    /**
     * Chart type
     */
    @Column("chart_type")
     private String chartType;

    /**
     * Configuration JSON
     */
    @Column("config_json")
    private String configJson;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Sort order
     */
    @Column("sort")
    private Integer sort;
}
