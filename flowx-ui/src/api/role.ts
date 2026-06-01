import request from '@/utils/request'

export interface RoleQuery {
  pageNum?: number
  pageSize?: number
  roleName?: string
  roleKey?: string
  status?: string
}

export interface RoleForm {
  id?: number
  roleName: string
  roleKey: string
  orderNum?: number
  status?: string
  remark?: string
  menuIds?: number[]
}

export function getRoleList(params: RoleQuery) {
  return request.get<any, any>('/system/role/list', { params })
}

export function getRoleById(id: number) {
  return request.get<any, any>(`/system/role/${id}`)
}

export function createRole(data: RoleForm) {
  return request.post<any, any>('/system/role', data)
}

export function updateRole(data: RoleForm) {
  return request.put<any, any>('/system/role', data)
}

export function deleteRole(ids: string) {
  return request.delete<any, any>(`/system/role/${ids}`)
}

export function assignMenus(roleId: number, menuIds: number[]) {
  return request.put<any, any>(`/system/role/${roleId}/menus`, { menuIds })
}

export function getRoleOptions() {
  return request.get<any, any>('/system/role/options')
}
