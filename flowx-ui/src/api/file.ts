import request from '@/utils/request'

export interface FileQuery {
  pageNum?: number
  pageSize?: number
  fileName?: string
  fileType?: string
}

export function uploadFile(file: File, onProgress?: (percent: number) => void) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, any>('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (progressEvent) => {
      if (onProgress && progressEvent.total) {
        onProgress(Math.round((progressEvent.loaded * 100) / progressEvent.total))
      }
    },
  })
}

export function getFileList(params: FileQuery) {
  return request.get<any, any>('/file/list', { params })
}

export function deleteFile(ids: string) {
  return request.delete<any, any>(`/file/${ids}`)
}

export function downloadFile(fileId: number) {
  return request.get<any, any>(`/file/download/${fileId}`, { responseType: 'blob' })
}

export function previewFile(fileId: number) {
  return request.get<any, any>(`/file/preview/${fileId}`)
}
