package com.flowx.report.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Operation statistics view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total operation count
     */
    private Long totalOperations;

    /**
     * Today's operation count
     */
    private Long todayOperations;

    /**
     * Operation type distribution (name, count)
     */
    private List<Map<String, Object>> operationTypeDistribution;

    /**
     * Hourly trend data (hour, count)
     */
    private List<Map<String, Object>> hourlyTrend;
}
