import request from '@/utils/request';
import type { ApiResult, TenantProfileVO } from '@/types';

export const getCurrentTenant = () =>
  request.get<unknown, ApiResult<TenantProfileVO>>('/system/tenant/current');

export const updateCurrentTenant = (data: Partial<TenantProfileVO>) =>
  request.put<unknown, ApiResult<void>>('/system/tenant/current', data);