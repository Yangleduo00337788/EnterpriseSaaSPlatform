import request from '@/utils/request';
import type { ApiResult, MenuVO } from '@/types';

export const getMenuTree = () =>
  request.get<unknown, ApiResult<MenuVO[]>>('/system/menus');

export const getCurrentUserMenuTree = () =>
  request.get<unknown, ApiResult<MenuVO[]>>('/system/menus/current');

export const createMenu = (data: Partial<MenuVO>) =>
  request.post<unknown, ApiResult<void>>('/system/menus', data);

export const updateMenu = (id: number, data: Partial<MenuVO>) =>
  request.put<unknown, ApiResult<void>>(`/system/menus/${id}`, data);

export const deleteMenu = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/system/menus/${id}`);
