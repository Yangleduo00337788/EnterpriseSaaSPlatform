package com.flowx.report.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Organization statistics view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total department count
     */
    private Long totalDepts;

    /**
     * Total position count
     */
    private Long totalPositions;

    /**
     * Department employee counts (name, value)
     */
    private List<Map<String, Object>> deptEmployeeCounts;
}
