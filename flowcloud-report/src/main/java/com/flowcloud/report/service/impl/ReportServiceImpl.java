package com.flowcloud.report.service.impl;

import com.flowcloud.approval.entity.ApprovalInstance;
import com.flowcloud.approval.entity.ApprovalTask;
import com.flowcloud.approval.enums.ApprovalStatus;
import com.flowcloud.approval.enums.TaskStatus;
import com.flowcloud.approval.mapper.ApprovalInstanceMapper;
import com.flowcloud.approval.mapper.ApprovalTaskMapper;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.report.service.ReportService;
import com.flowcloud.report.vo.DashboardVO;
import com.flowcloud.report.vo.ReportAnalyticsVO;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ApprovalInstanceMapper instanceMapper;
    private final ApprovalTaskMapper taskMapper;
    private final SysDeptMapper deptMapper;

    @Override
    public DashboardVO getDashboard() {
        Long tenantId = TenantContext.getTenantId();
        Long userId = TenantContext.getUserId();

        DashboardVO vo = new DashboardVO();
        vo.setTotalInstances(instanceMapper.selectCountByQuery(
                QueryWrapper.create().where(ApprovalInstance::getTenantId).eq(tenantId)));
        vo.setPendingCount(instanceMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(ApprovalInstance::getTenantId).eq(tenantId)
                        .and(ApprovalInstance::getStatus).eq(ApprovalStatus.PENDING.getCode())));
        vo.setApprovedCount(instanceMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(ApprovalInstance::getTenantId).eq(tenantId)
                        .and(ApprovalInstance::getStatus).eq(ApprovalStatus.APPROVED.getCode())));
        vo.setRejectedCount(instanceMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(ApprovalInstance::getTenantId).eq(tenantId)
                        .and(ApprovalInstance::getStatus).eq(ApprovalStatus.REJECTED.getCode())));
        vo.setMyPendingTasks(taskMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(ApprovalTask::getTenantId).eq(tenantId)
                        .and(ApprovalTask::getApproverId).eq(userId)
                        .and(ApprovalTask::getStatus).eq(TaskStatus.PENDING.getCode())));

        List<ApprovalInstance> allInstances = instanceMapper.selectListByQuery(
                QueryWrapper.create().where(ApprovalInstance::getTenantId).eq(tenantId));
        Map<String, Long> categoryMap = new HashMap<>();
        for (ApprovalInstance inst : allInstances) {
            categoryMap.merge(inst.getCategory(), 1L, Long::sum);
        }
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        categoryMap.forEach((k, v) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("category", k);
            item.put("count", v);
            categoryStats.add(item);
        });
        vo.setCategoryStats(categoryStats);
        vo.setMonthlyTrend(buildMonthlyTrend(allInstances));
        return vo;
    }

    @Override
    public ReportAnalyticsVO getAnalytics() {
        Long tenantId = TenantContext.getTenantId();
        List<ApprovalInstance> instances = instanceMapper.selectListByQuery(
                QueryWrapper.create().where(ApprovalInstance::getTenantId).eq(tenantId));
        List<ApprovalTask> tasks = taskMapper.selectListByQuery(
                QueryWrapper.create().where(ApprovalTask::getTenantId).eq(tenantId));

        ReportAnalyticsVO vo = new ReportAnalyticsVO();
        long finished = instances.stream()
                .filter(i -> ApprovalStatus.APPROVED.getCode().equals(i.getStatus())
                        || ApprovalStatus.REJECTED.getCode().equals(i.getStatus()))
                .count();
        long rejected = instances.stream()
                .filter(i -> ApprovalStatus.REJECTED.getCode().equals(i.getStatus()))
                .count();
        vo.setRejectionRate(finished == 0 ? 0 : Math.round(rejected * 10000.0 / finished) / 100.0);
        vo.setTrend(buildTrendItems(instances));
        vo.setDeptEfficiency(buildDeptEfficiency(instances, tenantId));
        vo.setApproverLoad(buildApproverLoad(tasks));
        return vo;
    }

    private List<Map<String, Object>> buildMonthlyTrend(List<ApprovalInstance> instances) {
        Map<String, Long> monthMap = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            monthMap.put(now.minusMonths(i).format(fmt), 0L);
        }
        for (ApprovalInstance inst : instances) {
            if (inst.getSubmitTime() == null) {
                continue;
            }
            String month = inst.getSubmitTime().format(fmt);
            if (monthMap.containsKey(month)) {
                monthMap.merge(month, 1L, Long::sum);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        monthMap.forEach((k, v) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("month", k);
            item.put("count", v);
            result.add(item);
        });
        return result;
    }

    private List<ReportAnalyticsVO.TrendItem> buildTrendItems(List<ApprovalInstance> instances) {
        Map<String, ReportAnalyticsVO.TrendItem> map = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            String period = now.minusMonths(i).format(fmt);
            ReportAnalyticsVO.TrendItem item = new ReportAnalyticsVO.TrendItem();
            item.setPeriod(period);
            map.put(period, item);
        }
        for (ApprovalInstance inst : instances) {
            if (inst.getSubmitTime() == null) {
                continue;
            }
            String period = inst.getSubmitTime().format(fmt);
            ReportAnalyticsVO.TrendItem item = map.get(period);
            if (item == null) {
                continue;
            }
            item.setTotal(item.getTotal() + 1);
            if (ApprovalStatus.APPROVED.getCode().equals(inst.getStatus())) {
                item.setApproved(item.getApproved() + 1);
            } else if (ApprovalStatus.REJECTED.getCode().equals(inst.getStatus())) {
                item.setRejected(item.getRejected() + 1);
            }
        }
        return new ArrayList<>(map.values());
    }

    private List<ReportAnalyticsVO.DeptEfficiencyItem> buildDeptEfficiency(List<ApprovalInstance> instances, Long tenantId) {
        Map<Long, ReportAnalyticsVO.DeptEfficiencyItem> map = new HashMap<>();
        Map<Long, String> deptNames = new HashMap<>();
        deptMapper.selectListByQuery(
                QueryWrapper.create().where(SysDept::getTenantId).eq(tenantId))
                .forEach(d -> deptNames.put(d.getId(), d.getDeptName()));

        Map<Long, List<Double>> deptHours = new HashMap<>();
        for (ApprovalInstance inst : instances) {
            Long deptId = inst.getDeptId() != null ? inst.getDeptId() : 0L;
            ReportAnalyticsVO.DeptEfficiencyItem item = map.computeIfAbsent(deptId, id -> {
                ReportAnalyticsVO.DeptEfficiencyItem d = new ReportAnalyticsVO.DeptEfficiencyItem();
                d.setDeptId(id == 0 ? null : id);
                d.setDeptName(deptNames.getOrDefault(id, "未分配部门"));
                return d;
            });
            item.setTotal(item.getTotal() + 1);
            if (ApprovalStatus.APPROVED.getCode().equals(inst.getStatus())) {
                item.setApproved(item.getApproved() + 1);
            }
            if (inst.getSubmitTime() != null && inst.getFinishTime() != null) {
                double hours = Duration.between(inst.getSubmitTime(), inst.getFinishTime()).toMinutes() / 60.0;
                deptHours.computeIfAbsent(deptId, k -> new ArrayList<>()).add(hours);
            }
        }
        for (Map.Entry<Long, List<Double>> entry : deptHours.entrySet()) {
            ReportAnalyticsVO.DeptEfficiencyItem item = map.get(entry.getKey());
            if (item != null && !entry.getValue().isEmpty()) {
                double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                item.setAvgHours(Math.round(avg * 10.0) / 10.0);
            }
        }
        return map.values().stream()
                .sorted(Comparator.comparingLong(ReportAnalyticsVO.DeptEfficiencyItem::getTotal).reversed())
                .toList();
    }

    private List<ReportAnalyticsVO.ApproverLoadItem> buildApproverLoad(List<ApprovalTask> tasks) {
        Map<Long, ReportAnalyticsVO.ApproverLoadItem> map = new HashMap<>();
        for (ApprovalTask task : tasks) {
            ReportAnalyticsVO.ApproverLoadItem item = map.computeIfAbsent(task.getApproverId(), id -> {
                ReportAnalyticsVO.ApproverLoadItem a = new ReportAnalyticsVO.ApproverLoadItem();
                a.setApproverId(id);
                a.setApproverName(task.getApproverName());
                return a;
            });
            if (TaskStatus.PENDING.getCode().equals(task.getStatus())) {
                item.setPendingCount(item.getPendingCount() + 1);
            } else {
                item.setHandledCount(item.getHandledCount() + 1);
            }
        }
        return map.values().stream()
                .sorted(Comparator.comparingLong(ReportAnalyticsVO.ApproverLoadItem::getPendingCount).reversed())
                .toList();
    }
}
