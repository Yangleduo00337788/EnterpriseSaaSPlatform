import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/types';

export interface AuditLogVO {
  id: number;
  tenantId: number;
  userId: number;
  userName: string;
  action: string;
  targetType: string;
  targetName: string;
  result: string;
  detail: string;
  ip: string;
  createTime: string;
}

export interface AuditLogQuery {
  targetType?: string;
  action?: string;
  userId?: number;
  startTime?: string;
  endTime?: string;
  pageNum?: number;
  pageSize?: number;
}

export const getAuditLogs = (params: AuditLogQuery) =>
  request.get<unknown, ApiResult<PageResult<AuditLogVO>>>('/system/audit-logs', { params });