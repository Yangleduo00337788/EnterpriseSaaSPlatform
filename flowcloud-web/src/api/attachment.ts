import request from '@/utils/request';
import type { ApiResult } from '@/types';

export interface AttachmentVO {
  id: number;
  bizType?: string;
  bizId?: number;
  fieldName?: string;
  originalName: string;
  fileUrl: string;
  fileSize?: number;
  mimeType?: string;
  uploaderName?: string;
  createTime?: string;
}

export function uploadAttachment(
  file: File,
  bizType?: string,
  bizId?: number,
  fieldName?: string,
) {
  const form = new FormData();
  form.append('file', file);
  if (bizType) form.append('bizType', bizType);
  if (bizId != null) form.append('bizId', String(bizId));
  if (fieldName) form.append('fieldName', fieldName);
  return request.post<unknown, { data: AttachmentVO }>('/attachments/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function getAttachments(bizType: string, bizId: number, fieldName?: string) {
  return request.get<unknown, { data: AttachmentVO[] }>('/attachments', {
    params: { bizType, bizId, fieldName },
  });
}

export function deleteAttachment(id: number) {
  return request.delete<unknown, ApiResult<void>>(`/attachments/${id}`);
}
