import request from '@/utils/request';
import type { ApiResult, PageResult, RoleOptionVO, UserOptionVO, UserVO } from '@/types';

export const getUserList = (params: { keyword?: string; pageNum?: number; pageSize?: number; deptId?: number }) =>
  request.get<unknown, ApiResult<PageResult<UserVO>>>('/system/users', { params });

export const getUserOptions = () =>
  request.get<unknown, ApiResult<UserOptionVO[]>>('/system/users/options');

export const createUser = (data: Partial<UserVO> & { password?: string; roleIds?: number[] }) =>
  request.post<unknown, ApiResult<void>>('/system/users', data);

export const updateUser = (id: number, data: Partial<UserVO> & { roleIds?: number[] }) =>
  request.put<unknown, ApiResult<void>>(`/system/users/${id}`, data);

export const deleteUser = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/system/users/${id}`);

export const toggleUserStatus = (id: number) =>
  request.put<unknown, ApiResult<void>>(`/system/users/${id}/status`);

export const resetUserPassword = (id: number, password: string) =>
  request.put<unknown, ApiResult<void>>(`/system/users/${id}/reset-password`, { password });

export const getRoleOptions = () =>
  request.get<unknown, ApiResult<RoleOptionVO[]>>('/system/roles/options');

export const exportUsers = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/system/users/export', {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = '员工列表.xlsx';
  link.click();
  window.URL.revokeObjectURL(url);
};

export const importUsers = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post<unknown, ApiResult<{ successCount: number; failCount: number; errors: string[] }>>(
    '/system/users/import',
    formData,
    { headers: { 'Content-Type': 'multipart/form-data' } },
  );
};
