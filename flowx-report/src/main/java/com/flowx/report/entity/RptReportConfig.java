package com.flowx.report.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("rpt_report_config")
public class RptReportConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Report name
     */
    @TableField("report_name")
    private String reportName;

    /**
     * Report code (unique identifier)
     */
    @TableField("report_code")
    private String reportCode;

    /**
     * Report type
     */
    @TableField("report_type")
    private String reportType;

    /**
     * Data source
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * Chart type
     */
    @TableField("chart_type")
     private String chartType;

    /**
     * Configuration JSON
     */
    @TableField("config_json")
    private String configJson;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Sort order
     */
    @TableField("sort")
    private Integer sort;
}
