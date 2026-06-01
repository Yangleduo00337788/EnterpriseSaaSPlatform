package com.flowx.report.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Tenant statistics view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total tenant count
     */
    private Long totalTenants;

    /**
     * Active tenant count
     */
    private Long activeTenants;

    /**
     * Package distribution (name, count)
     */
    private List<Map<String, Object>> packageDistribution;

    /**
     * Monthly growth data (month, count)
     */
    private List<Map<String, Object>> monthlyGrowth;
}
