import { Toast } from '@douyinfe/semi-ui';
import { previewAttachment, type AttachmentVO } from '@/api/attachment';

export async function openAttachmentPreview(attachment: AttachmentVO) {
  const previewWindow = window.open('about:blank', '_blank');
  try {
    const response = await previewAttachment(attachment.id);
    const blob = response.data instanceof Blob
      ? response.data
      : new Blob([response.data], { type: response.headers['content-type'] || attachment.mimeType || 'application/octet-stream' });
    if (blob.size <= 0) {
      throw new Error('empty preview content');
    }
    const blobUrl = window.URL.createObjectURL(blob);
    if (previewWindow) {
      previewWindow.location.replace(blobUrl);
    } else {
      window.open(blobUrl, '_blank');
    }
    window.setTimeout(() => window.URL.revokeObjectURL(blobUrl), 60_000);
  } catch {
    if (previewWindow) {
      previewWindow.close();
    }
    Toast.error('文件预览失败，请稍后重试');
  }
}
