import request from '@/utils/request'

export interface NotificationQuery {
  pageNum?: number
  pageSize?: number
  title?: string
  type?: string
  status?: string
}

export function getNotifications(params: NotificationQuery) {
  return request.get<any, any>('/message/notification/list', { params })
}

export function markAsRead(ids: string) {
  return request.put<any, any>(`/message/notification/read/${ids}`)
}

export function markAllAsRead() {
  return request.put<any, any>('/message/notification/read-all')
}

export function getUnreadCount() {
  return request.get<any, any>('/message/notification/unread-count')
}

export function deleteNotification(ids: string) {
  return request.delete<any, any>(`/message/notification/${ids}`)
}

export function getMessageTemplates(params: any) {
  return request.get<any, any>('/message/template/list', { params })
}

export function createMessageTemplate(data: any) {
  return request.post<any, any>('/message/template', data)
}

export function updateMessageTemplate(data: any) {
  return request.put<any, any>('/message/template', data)
}

export function deleteMessageTemplate(ids: string) {
  return request.delete<any, any>(`/message/template/${ids}`)
}
