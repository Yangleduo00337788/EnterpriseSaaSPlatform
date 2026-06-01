import request from '@/utils/request'

export interface DictQuery {
  pageNum?: number
  pageSize?: number
  dictName?: string
  dictType?: string
  status?: string
}

export interface DictForm {
  id?: number
  dictName: string
  dictType: string
  status?: string
  remark?: string
}

export interface DictDataForm {
  id?: number
  dictType: string
  dictLabel: string
  dictValue: string
  orderNum?: number
  status?: string
  remark?: string
}

export function getDictTypeList(params: DictQuery) {
  return request.get<any, any>('/system/dict/type/list', { params })
}

export function createDictType(data: DictForm) {
  return request.post<any, any>('/system/dict/type', data)
}

export function updateDictType(data: DictForm) {
  return request.put<any, any>('/system/dict/type', data)
}

export function deleteDictType(ids: string) {
  return request.delete<any, any>(`/system/dict/type/${ids}`)
}

export function getDictDataByType(dictType: string) {
  return request.get<any, any>(`/system/dict/data/type/${dictType}`)
}

export function getDictDataList(params: DictQuery) {
  return request.get<any, any>('/system/dict/data/list', { params })
}

export function createDictData(data: DictDataForm) {
  return request.post<any, any>('/system/dict/data', data)
}

export function updateDictData(data: DictDataForm) {
  return request.put<any, any>('/system/dict/data', data)
}

export function deleteDictData(ids: string) {
  return request.delete<any, any>(`/system/dict/data/${ids}`)
}
