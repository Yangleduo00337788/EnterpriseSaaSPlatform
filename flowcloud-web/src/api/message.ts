import request from '@/utils/request';
import type { ApiResult, PageResult, MessageVO } from '@/types';

export const getMessages = (params: { pageNum?: number; pageSize?: number }) =>
  request.get<unknown, ApiResult<PageResult<MessageVO>>>('/messages', { params });

export const getUnreadCount = () =>
  request.get<unknown, ApiResult<number>>('/messages/unread-count');

export const markMessageRead = (id: number) =>
  request.put<unknown, ApiResult<void>>(`/messages/${id}/read`);

export const markAllMessagesRead = () =>
  request.put<unknown, ApiResult<void>>('/messages/read-all');

export const markBatchMessagesRead = (ids: number[]) =>
  request.put<unknown, ApiResult<void>>('/messages/batch-read', { ids });
