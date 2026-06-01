import request from '@/utils/request'

export interface ApprovalSubmitForm {
  typeId: number
  title: string
  formData: Record<string, any>
  remark?: string
}

export interface ApprovalQuery {
  pageNum?: number
  pageSize?: number
  title?: string
  status?: string
  typeId?: number
}

export function submitApproval(data: ApprovalSubmitForm) {
  return request.post<any, any>('/approval/instance/submit', data)
}

export function getMyApprovals(params: ApprovalQuery) {
  return request.get<any, any>('/approval/instance/mine', { params })
}

export function getPendingApprovals(params: ApprovalQuery) {
  return request.get<any, any>('/approval/task/pending', { params })
}

export function getCompletedApprovals(params: ApprovalQuery) {
  return request.get<any, any>('/approval/task/completed', { params })
}

export function approveTask(taskId: number, data: { comment: string }) {
  return request.post<any, any>(`/approval/task/${taskId}/approve`, data)
}

export function rejectTask(taskId: number, data: { comment: string }) {
  return request.post<any, any>(`/approval/task/${taskId}/reject`, data)
}

export function withdrawApproval(instanceId: number) {
  return request.post<any, any>(`/approval/instance/${instanceId}/withdraw`)
}

export function getApprovalDetail(instanceId: number) {
  return request.get<any, any>(`/approval/instance/${instanceId}`)
}

export function getApprovalTypes() {
  return request.get<any, any>('/approval/type/list')
}

export function createApprovalType(data: any) {
  return request.post<any, any>('/approval/type', data)
}

export function updateApprovalType(data: any) {
  return request.put<any, any>('/approval/type', data)
}

export function deleteApprovalType(ids: string) {
  return request.delete<any, any>(`/approval/type/${ids}`)
}

export function getApprovalProcessTimeline(instanceId: number) {
  return request.get<any, any>(`/approval/instance/${instanceId}/timeline`)
}
