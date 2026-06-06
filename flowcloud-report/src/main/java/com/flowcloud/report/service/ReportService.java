package com.flowcloud.report.service;

import com.flowcloud.report.vo.DashboardVO;
import com.flowcloud.report.vo.ReportAnalyticsVO;

public interface ReportService {

    DashboardVO getDashboard();

    ReportAnalyticsVO getAnalytics();
}
