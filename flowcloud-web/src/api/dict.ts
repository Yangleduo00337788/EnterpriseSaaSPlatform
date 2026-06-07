import request from '@/utils/request';
import type { ApiResult, DictDataVO, DictTypeVO } from '@/types';

export const getDictList = () =>
  request.get<unknown, ApiResult<DictTypeVO[]>>('/system/dicts');

export const getDictById = (id: number) =>
  request.get<unknown, ApiResult<DictTypeVO>>(`/system/dicts/${id}`);

export const getDictDataByCode = (dictCode: string) =>
  request.get<unknown, ApiResult<DictDataVO[]>>(`/system/dicts/code/${dictCode}`);

export const createDict = (data: Partial<DictTypeVO> & { items?: DictDataVO[] }) =>
  request.post<unknown, ApiResult<void>>('/system/dicts', data);

export const updateDict = (id: number, data: Partial<DictTypeVO> & { items?: DictDataVO[] }) =>
  request.put<unknown, ApiResult<void>>(`/system/dicts/${id}`, data);

export const deleteDict = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/system/dicts/${id}`);
