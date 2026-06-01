package com.flowx.report.service.impl;

import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.EmployeeReportService;
import com.flowx.report.vo.EmployeeStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Employee report service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeReportServiceImpl implements EmployeeReportService {

    private final RptReportMapper reportMapper;

    @Override
    public EmployeeStatsVO getEmployeeStats() {
        Long totalEmployees = reportMapper.getTotalEmployees();

        // First day of current month
        LocalDateTime monthStart = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                1, 0, 0, 0);
        Long newEmployeesThisMonth = reportMapper.getNewEmployeesThisMonth(monthStart);

        List<Map<String, Object>> deptDistribution = reportMapper.getDeptDistribution();
        List<Map<String, Object>> positionDistribution = reportMapper.getPositionDistribution();

        return EmployeeStatsVO.builder()
                .totalEmployees(totalEmployees != null ? totalEmployees : 0L)
                .newEmployeesThisMonth(newEmployeesThisMonth != null ? newEmployeesThisMonth : 0L)
                .deptDistribution(deptDistribution)
                .positionDistribution(positionDistribution)
                .build();
    }
}
