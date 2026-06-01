package com.flowx.report.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Employee statistics view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total employee count
     */
    private Long totalEmployees;

    /**
     * New employees this month
     */
    private Long newEmployeesThisMonth;

    /**
     * Department distribution (name, count)
     */
    private List<Map<String, Object>> deptDistribution;

    /**
     * Position distribution (name, count)
     */
    private List<Map<String, Object>> positionDistribution;
}
