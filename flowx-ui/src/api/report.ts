import request from '@/utils/request'

export interface ReportQuery {
  startTime?: string
  endTime?: string
  deptId?: number
  type?: string
}

export function getApprovalStats(params: ReportQuery) {
  return request.get<any, any>('/report/approval/stats', { params })
}

export function getApprovalTrend(params: ReportQuery) {
  return request.get<any, any>('/report/approval/trend', { params })
}

export function getEmployeeStats(params: ReportQuery) {
  return request.get<any, any>('/report/employee/stats', { params })
}

export function getTenantStatsReport(params: ReportQuery) {
  return request.get<any, any>('/report/tenant/stats', { params })
}

export function getOperationStats(params: ReportQuery) {
  return request.get<any, any>('/report/operation/stats', { params })
}

export function getApprovalTypeDistribution(params: ReportQuery) {
  return request.get<any, any>('/report/approval/type-distribution', { params })
}
