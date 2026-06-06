package com.flowcloud.report.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {

    private long totalInstances;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
    private long myPendingTasks;
    private List<Map<String, Object>> categoryStats;
    private List<Map<String, Object>> monthlyTrend;
}
