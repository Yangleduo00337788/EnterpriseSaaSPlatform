package com.flowx.report.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Approval statistics view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total approval count
     */
    private Long totalCount;

    /**
     * Approved count
     */
    private Long approvedCount;

    /**
     * Rejected count
     */
    private Long rejectedCount;

    /**
     * Pending count
     */
    private Long pendingCount;

    /**
     * Average process time (hours)
     */
    private Double avgProcessTime;

    /**
     * Daily trend data (date, count)
     */
    private List<Map<String, Object>> dailyTrend;
}
