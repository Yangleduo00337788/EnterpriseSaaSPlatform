import request from '@/utils/request';
import type { ApiResult, PermissionVO, RoleVO } from '@/types';

export const getRoleList = () =>
  request.get<unknown, ApiResult<RoleVO[]>>('/system/roles');

export const getRoleById = (id: number) =>
  request.get<unknown, ApiResult<RoleVO>>(`/system/roles/${id}`);

export const createRole = (data: Partial<RoleVO> & { permissionIds?: number[] }) =>
  request.post<unknown, ApiResult<void>>('/system/roles', data);

export const updateRole = (id: number, data: Partial<RoleVO> & { permissionIds?: number[] }) =>
  request.put<unknown, ApiResult<void>>(`/system/roles/${id}`, data);

export const deleteRole = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/system/roles/${id}`);

export const getPermissionTree = () =>
  request.get<unknown, ApiResult<PermissionVO[]>>('/system/permissions/tree');
