import request from '@/utils/request';
import type { ApiResult, DashboardVO, ReportAnalyticsVO } from '@/types';

export const getDashboard = () =>
  request.get<unknown, ApiResult<DashboardVO>>('/report/dashboard');

export const getReportAnalytics = () =>
  request.get<unknown, ApiResult<ReportAnalyticsVO>>('/report/analytics');
