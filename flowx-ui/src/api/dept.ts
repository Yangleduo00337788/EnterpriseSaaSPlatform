import request from '@/utils/request'

export interface DeptForm {
  id?: number
  deptName: string
  parentId?: number
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status?: string
}

export interface DeptTree {
  id: number
  deptName: string
  parentId: number
  children: DeptTree[]
}

export function getDeptTree() {
  return request.get<any, any>('/system/dept/tree')
}

export function getDeptList() {
  return request.get<any, any>('/system/dept/list')
}

export function getDeptById(id: number) {
  return request.get<any, any>(`/system/dept/${id}`)
}

export function createDept(data: DeptForm) {
  return request.post<any, any>('/system/dept', data)
}

export function updateDept(data: DeptForm) {
  return request.put<any, any>('/system/dept', data)
}

export function deleteDept(id: number) {
  return request.delete<any, any>(`/system/dept/${id}`)
}
