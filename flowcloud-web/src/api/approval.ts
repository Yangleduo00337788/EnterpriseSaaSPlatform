import request from '@/utils/request';
import type { ApiResult, PageResult, TemplateVO, InstanceVO, TaskVO } from '@/types';

export const getTemplates = (category?: string) =>
  request.get<unknown, ApiResult<TemplateVO[]>>('/approval/templates', { params: { category } });

/** 管理态：含草稿/停用 */
export const getAllTemplates = (category?: string) =>
  request.get<unknown, ApiResult<TemplateVO[]>>('/approval/templates/all', { params: { category } });

export const getTemplate = (id: number) =>
  request.get<unknown, ApiResult<TemplateVO>>(`/approval/templates/${id}`);

export const createTemplate = (data: Partial<TemplateVO>) =>
  request.post<unknown, ApiResult<void>>('/approval/templates', data);

export const updateTemplate = (id: number, data: Partial<TemplateVO>) =>
  request.put<unknown, ApiResult<void>>(`/approval/templates/${id}`, data);

export const deleteTemplate = (id: number) =>
  request.delete<unknown, ApiResult<void>>(`/approval/templates/${id}`);

export const publishTemplate = (id: number, remark?: string) =>
  request.post<unknown, ApiResult<void>>(`/approval/templates/${id}/publish`, { remark });

export const disableTemplate = (id: number) =>
  request.post<unknown, ApiResult<void>>(`/approval/templates/${id}/disable`);

export const getTemplateVersions = (id: number) =>
  request.get<unknown, ApiResult<unknown[]>>(`/approval/templates/${id}/versions`);

export const submitApproval = (data: { templateId: number; title: string; formData?: string }) =>
  request.post<unknown, ApiResult<number>>('/approval/instances', data);

export const getMySubmissions = (params: { status?: string; pageNum?: number; pageSize?: number }) =>
  request.get<unknown, ApiResult<PageResult<InstanceVO>>>('/approval/instances/my', { params });

export const getAllInstances = (params: { status?: string; category?: string; pageNum?: number; pageSize?: number }) =>
  request.get<unknown, ApiResult<PageResult<InstanceVO>>>('/approval/instances', { params });

export const getInstanceDetail = (id: number) =>
  request.get<unknown, ApiResult<InstanceVO>>(`/approval/instances/${id}`);

export const cancelInstance = (id: number) =>
  request.put<unknown, ApiResult<void>>(`/approval/instances/${id}/cancel`);

export const getPendingTasks = (params: { pageNum?: number; pageSize?: number }) =>
  request.get<unknown, ApiResult<PageResult<TaskVO>>>('/approval/tasks/pending', { params });

export const getHandledTasks = (params: { pageNum?: number; pageSize?: number }) =>
  request.get<unknown, ApiResult<PageResult<TaskVO>>>('/approval/tasks/handled', { params });

export const completeTask = (data: { taskId: number; action: string; comment?: string }) =>
  request.post<unknown, ApiResult<void>>('/approval/tasks/complete', data);

export const remindTask = (taskId: number) =>
  request.post<unknown, ApiResult<void>>(`/approval/tasks/${taskId}/remind`);
