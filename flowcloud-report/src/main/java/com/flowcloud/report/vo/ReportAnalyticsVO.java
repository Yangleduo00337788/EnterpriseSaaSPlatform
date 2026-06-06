package com.flowcloud.report.vo;

import lombok.Data;

import java.util.List;

@Data
public class ReportAnalyticsVO {

    private double rejectionRate;
    private List<TrendItem> trend;
    private List<DeptEfficiencyItem> deptEfficiency;
    private List<ApproverLoadItem> approverLoad;

    @Data
    public static class TrendItem {
        private String period;
        private long total;
        private long approved;
        private long rejected;
    }

    @Data
    public static class DeptEfficiencyItem {
        private Long deptId;
        private String deptName;
        private long total;
        private long approved;
        private double avgHours;
    }

    @Data
    public static class ApproverLoadItem {
        private Long approverId;
        private String approverName;
        private long pendingCount;
        private long handledCount;
    }
}
