import request from '@/utils/request';
import type { ApiResult } from '@/types';

export interface StorageSettingsVO {
  storageType: 'LOCAL' | 'MINIO';
  localPath?: string;
  localBaseUrl?: string;
  minioEndpoint?: string;
  minioAccessKey?: string;
  minioSecretKey?: string;
  minioBucket?: string;
  minioBaseUrl?: string;
  minioConsoleUrl?: string;
}

export type StorageSettingsDTO = StorageSettingsVO;

export const getStorageSettings = () =>
  request.get<unknown, ApiResult<StorageSettingsVO>>('/system/settings/storage');

export const updateStorageSettings = (data: StorageSettingsDTO) =>
  request.put<unknown, ApiResult<void>>('/system/settings/storage', data);

export const testStorageSettings = (data: StorageSettingsDTO) =>
  request.post<unknown, ApiResult<string>>('/system/settings/storage/test', data);
