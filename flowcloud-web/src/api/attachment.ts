import type { AxiosProgressEvent, AxiosResponse } from 'axios';
import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/types';

export interface AttachmentVO {
  id: number;
  bizType?: string;
  bizId?: number;
  bizName?: string;
  bizCode?: string;
  fieldName?: string;
  originalName: string;
  fileUrl: string;
  storageType?: string;
  fileSize?: number;
  mimeType?: string;
  uploaderName?: string;
  createTime?: string;
}

export interface AttachmentManageQuery {
  keyword?: string;
  bizType?: string;
  storageType?: string;
  pageNum: number;
  pageSize: number;
}

export interface AttachmentBatchOperationDTO {
  ids: number[];
}

export interface UploadAttachmentOptions {
  onUploadProgress?: (event: AxiosProgressEvent) => void;
}

export function uploadAttachment(
  file: File,
  bizType?: string,
  bizId?: number,
  fieldName?: string,
  options?: UploadAttachmentOptions,
) {
  const form = new FormData();
  form.append('file', file);
  if (bizType) form.append('bizType', bizType);
  if (bizId != null) form.append('bizId', String(bizId));
  if (fieldName) form.append('fieldName', fieldName);
  return request.post<unknown, { data: AttachmentVO }>('/attachments/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: options?.onUploadProgress,
  });
}

export function getAttachments(bizType: string, bizId: number, fieldName?: string) {
  return request.get<unknown, { data: AttachmentVO[] }>('/attachments', {
    params: { bizType, bizId, fieldName },
  });
}

export function getAttachmentPage(params: AttachmentManageQuery) {
  return request.get<unknown, ApiResult<PageResult<AttachmentVO>>>('/attachments/manage', {
    params,
  });
}

export function deleteAttachment(id: number) {
  return request.delete<unknown, ApiResult<void>>(`/attachments/${id}`);
}

export function batchDeleteAttachments(data: AttachmentBatchOperationDTO) {
  return request.post<unknown, ApiResult<void>>('/attachments/batch-delete', data);
}

export function downloadAttachmentsArchive(data: AttachmentBatchOperationDTO) {
  return request.post<AttachmentBatchOperationDTO, AxiosResponse<Blob>>('/attachments/download', data, {
    responseType: 'blob',
    disableApiEncryption: true,
  });
}

export function previewAttachment(id: number) {
  return request.get<unknown, AxiosResponse<Blob>>(`/attachments/${id}/content`, {
    responseType: 'blob',
    disableApiEncryption: true,
  });
}
