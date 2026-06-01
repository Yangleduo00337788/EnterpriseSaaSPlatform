package com.flowx.report.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Report configuration view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ReportConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Report config ID
     */
    private Long id;

    /**
     * Report name
     */
    private String reportName;

    /**
     * Report code
     */
    private String reportCode;

    /**
     * Report type
     */
    private String reportType;

    /**
     * Data source
     */
    private String dataSource;

    /**
     * Chart type
     */
    private String chartType;

    /**
     * Configuration JSON
     */
    private String configJson;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
