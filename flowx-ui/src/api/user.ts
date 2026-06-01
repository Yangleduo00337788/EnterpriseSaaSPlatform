import request from '@/utils/request'

export interface UserQuery {
  pageNum?: number
  pageSize?: number
  username?: string
  phone?: string
  status?: string
  deptId?: number
}

export interface UserForm {
  id?: number
  username: string
  nickname: string
  password?: string
  email?: string
  phone?: string
  sex?: number
  status?: string
  deptId?: number
  positionId?: number
  remark?: string
}

export function getUserList(params: UserQuery) {
  return request.get<any, any>('/system/user/list', { params })
}

export function getUserById(id: number) {
  return request.get<any, any>(`/system/user/${id}`)
}

export function createUser(data: UserForm) {
  return request.post<any, any>('/system/user', data)
}

export function updateUser(data: UserForm) {
  return request.put<any, any>('/system/user', data)
}

export function deleteUser(ids: string) {
  return request.delete<any, any>(`/system/user/${ids}`)
}

export function assignRoles(userId: number, roleIds: number[]) {
  return request.put<any, any>(`/system/user/${userId}/roles`, { roleIds })
}

export function resetPassword(userId: number, newPassword: string) {
  return request.put<any, any>(`/system/user/${userId}/reset-password`, { newPassword })
}

export function updateUserStatus(userId: number, status: string) {
  return request.put<any, any>(`/system/user/${userId}/status`, { status })
}

export function exportUser(params: UserQuery) {
  return request.get<any, any>('/system/user/export', { params, responseType: 'blob' })
}
