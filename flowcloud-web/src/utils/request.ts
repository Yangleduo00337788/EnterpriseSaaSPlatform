import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { Toast } from '@douyinfe/semi-ui';
import type { ApiResult } from '@/types';
import {
  attachApiCryptoHeaders,
  clearApiCryptoPublicKeyCache,
  decryptApiPayload,
  encryptApiPayload,
  isApiCryptoEnvelope,
  isApiCryptoSupported,
} from '@/utils/apiCrypto';

interface EncryptableRequestConfig extends InternalAxiosRequestConfig {
  apiCryptoKey?: CryptoKey;
  disableApiEncryption?: boolean;
  apiCryptoRetryCount?: number;
  originalData?: unknown;
}

const API_CRYPTO_KEY_EXPIRED_CODE = 4601;

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

request.interceptors.request.use(async (config) => {
  const nextConfig = config as EncryptableRequestConfig;
  if (typeof nextConfig.originalData === 'undefined') {
    nextConfig.originalData = nextConfig.data;
  }
  const token = localStorage.getItem('token');
  if (token) {
    setHeader(nextConfig, 'Authorization', `Bearer ${token}`);
  }
  if (!isApiCryptoSupported() || nextConfig.disableApiEncryption || shouldSkipEncryptedResponse(nextConfig)) {
    return nextConfig;
  }

  const cryptoHeaders: Record<string, string> = {};
  nextConfig.apiCryptoKey = await attachApiCryptoHeaders(cryptoHeaders);
  Object.entries(cryptoHeaders).forEach(([key, value]) => setHeader(nextConfig, key, value));

  if (canEncryptRequestBody(nextConfig)) {
    nextConfig.data = await encryptApiPayload(nextConfig.data, nextConfig.apiCryptoKey);
    setHeader(nextConfig, 'Content-Type', 'application/json');
  }
  return nextConfig;
});

request.interceptors.response.use(
  async (response) => {
    const res = (await decodeResponseData(response.data, response.config as EncryptableRequestConfig)) as ApiResult;
    if (shouldRetryApiCrypto(res, response.config as EncryptableRequestConfig)) {
      return retryWithFreshApiCrypto(response.config as EncryptableRequestConfig);
    }
    if (res.code !== 200) {
      Toast.error(res.message || '请求失败');
      if (res.code === 401 || res.code === 1005) {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
      return Promise.reject(new Error(res.message));
    }
    return res;
  },
  async (error: AxiosError) => {
    const response = error.response;
    if (response) {
      const config = response.config as EncryptableRequestConfig;
      const decrypted = await decodeResponseData(response.data, config);
      if (isApiResult(decrypted)) {
        if (shouldRetryApiCrypto(decrypted, config)) {
          return retryWithFreshApiCrypto(config);
        }
        response.data = decrypted;
        Toast.error(decrypted.message || '请求失败');
        if (decrypted.code === 401 || decrypted.code === 1005) {
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    }
    Toast.error(error.message || '网络错误');
    return Promise.reject(error);
  }
);

function canEncryptRequestBody(config: EncryptableRequestConfig) {
  return (
    config.data != null &&
    !(config.data instanceof FormData) &&
    !(config.data instanceof URLSearchParams) &&
    !(config.data instanceof Blob) &&
    !(config.data instanceof ArrayBuffer)
  );
}

function isApiResult(data: unknown): data is ApiResult {
  return !!data && typeof data === 'object' && 'code' in data && 'message' in data;
}

function setHeader(config: EncryptableRequestConfig, key: string, value: string) {
  if (config.headers && typeof config.headers.set === 'function') {
    config.headers.set(key, value);
    return;
  }
  config.headers = {
    ...(config.headers ?? {}),
    [key]: value,
  };
}

function shouldSkipEncryptedResponse(config: EncryptableRequestConfig) {
  return config.responseType === 'blob' || config.responseType === 'arraybuffer';
}

async function decodeResponseData(data: unknown, config: EncryptableRequestConfig) {
  if (!isApiCryptoEnvelope(data) || !config.apiCryptoKey) {
    return data;
  }
  return decryptApiPayload(data, config.apiCryptoKey);
}

function shouldRetryApiCrypto(result: ApiResult | undefined, config: EncryptableRequestConfig) {
  return !!result && result.code === API_CRYPTO_KEY_EXPIRED_CODE && (config.apiCryptoRetryCount ?? 0) < 1;
}

async function retryWithFreshApiCrypto(config: EncryptableRequestConfig) {
  clearApiCryptoPublicKeyCache();
  const retryConfig = config as EncryptableRequestConfig;
  retryConfig.apiCryptoRetryCount = (retryConfig.apiCryptoRetryCount ?? 0) + 1;
  retryConfig.apiCryptoKey = undefined;
  retryConfig.data = retryConfig.originalData;
  removeHeader(retryConfig, 'X-Api-Encrypted');
  removeHeader(retryConfig, 'X-Api-Encrypted-Key');
  removeHeader(retryConfig, 'X-Api-Key-Fingerprint');
  return request(retryConfig);
}

function removeHeader(config: EncryptableRequestConfig, key: string) {
  if (config.headers && typeof config.headers.delete === 'function') {
    config.headers.delete(key);
    return;
  }
  if (config.headers && key in config.headers) {
    delete (config.headers as Record<string, unknown>)[key];
  }
}

export default request;
