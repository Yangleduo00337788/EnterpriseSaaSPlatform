import request from '@/utils/request';
import type { ApiResult, MessageTemplateVO } from '@/types';

export const getMessageTemplates = () =>
  request.get<unknown, ApiResult<MessageTemplateVO[]>>('/system/message-templates');

export const createMessageTemplate = (data: Partial<MessageTemplateVO>) =>
  request.post<unknown, ApiResult<void>>('/system/message-templates', data);

export const updateMessageTemplate = (id: number, data: Partial<MessageTemplateVO>) =>
  request.put<unknown, ApiResult<void>>(`/system/message-templates/${id}`, data);

export const deleteMessageTemplate = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/system/message-templates/${id}`);
