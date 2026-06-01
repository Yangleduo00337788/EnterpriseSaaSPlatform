import request from '@/utils/request'

export interface MenuForm {
  id?: number
  menuName: string
  parentId?: number
  orderNum?: number
  path?: string
  component?: string
  menuType: string
  visible?: string
  status?: string
  perms?: string
  icon?: string
  remark?: string
}

export interface MenuTree {
  id: number
  menuName: string
  parentId: number
  children: MenuTree[]
}

export function getMenuTree() {
  return request.get<any, any>('/system/menu/tree')
}

export function getMenuList() {
  return request.get<any, any>('/system/menu/list')
}

export function getMenuById(id: number) {
  return request.get<any, any>(`/system/menu/${id}`)
}

export function createMenu(data: MenuForm) {
  return request.post<any, any>('/system/menu', data)
}

export function updateMenu(data: MenuForm) {
  return request.put<any, any>('/system/menu', data)
}

export function deleteMenu(id: number) {
  return request.delete<any, any>(`/system/menu/${id}`)
}
