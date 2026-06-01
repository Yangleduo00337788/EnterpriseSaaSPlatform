import request from '@/utils/request'

export interface PositionQuery {
  pageNum?: number
  pageSize?: number
  positionName?: string
  positionCode?: string
  status?: string
}

export interface PositionForm {
  id?: number
  positionName: string
  positionCode: string
  orderNum?: number
  status?: string
  remark?: string
}

export function getPositionList(params: PositionQuery) {
  return request.get<any, any>('/system/position/list', { params })
}

export function getPositionById(id: number) {
  return request.get<any, any>(`/system/position/${id}`)
}

export function createPosition(data: PositionForm) {
  return request.post<any, any>('/system/position', data)
}

export function updatePosition(data: PositionForm) {
  return request.put<any, any>('/system/position', data)
}

export function deletePosition(ids: string) {
  return request.delete<any, any>(`/system/position/${ids}`)
}

export function getPositionOptions() {
  return request.get<any, any>('/system/position/options')
}
