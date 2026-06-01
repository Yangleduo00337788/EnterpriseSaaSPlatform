import request from '@/utils/request'

export interface TenantQuery {
  pageNum?: number
  pageSize?: number
  tenantName?: string
  contactName?: string
  status?: string
}

export interface TenantForm {
  id?: number
  tenantName: string
  contactName: string
  contactPhone: string
  contactEmail?: string
  domain?: string
  packageId?: number
  expireTime?: string
  accountLimit?: number
  status?: string
  remark?: string
}

export function getTenantList(params: TenantQuery) {
  return request.get<any, any>('/system/tenant/list', { params })
}

export function getTenantById(id: number) {
  return request.get<any, any>(`/system/tenant/${id}`)
}

export function createTenant(data: TenantForm) {
  return request.post<any, any>('/system/tenant', data)
}

export function updateTenant(data: TenantForm) {
  return request.put<any, any>('/system/tenant', data)
}

export function deleteTenant(ids: string) {
  return request.delete<any, any>(`/system/tenant/${ids}`)
}

export function getTenantStats() {
  return request.get<any, any>('/system/tenant/stats')
}
