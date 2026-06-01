package com.flowx.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Report query DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ReportQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Report type
     */
    private String reportType;

    /**
     * Start date (yyyy-MM-dd)
     */
    private String startDate;

    /**
     * End date (yyyy-MM-dd)
     */
    private String endDate;
}
