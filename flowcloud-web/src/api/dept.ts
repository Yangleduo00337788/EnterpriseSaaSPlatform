import request from '@/utils/request';
import type { ApiResult, DeptVO } from '@/types';

export const getDeptTree = () =>
  request.get<unknown, ApiResult<DeptVO[]>>('/system/depts');

export const createDept = (data: Partial<DeptVO>) =>
  request.post<unknown, ApiResult<void>>('/system/depts', data);

export const updateDept = (id: number, data: Partial<DeptVO>) =>
  request.put<unknown, ApiResult<void>>(`/system/depts/${id}`, data);

export const deleteDept = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/system/depts/${id}`);