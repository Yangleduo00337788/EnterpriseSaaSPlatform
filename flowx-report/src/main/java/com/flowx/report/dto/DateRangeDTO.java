package com.flowx.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Date range DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class DateRangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Start date
     */
    private LocalDate startDate;

    /**
     * End date
     */
    private LocalDate endDate;
}
