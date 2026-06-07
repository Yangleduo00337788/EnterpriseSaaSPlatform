import type { ApiResult } from '@/types';

const API_PUBLIC_KEY_URL = '/api/security/public-key';
const AES_GCM_IV_LENGTH = 12;
const ENCRYPTION_FLAG_HEADER = 'X-Api-Encrypted';
const ENCRYPTED_KEY_HEADER = 'X-Api-Encrypted-Key';
const KEY_FINGERPRINT_HEADER = 'X-Api-Key-Fingerprint';
const API_PUBLIC_KEY_CACHE_KEY = 'api-crypto-public-key';
const encoder = new TextEncoder();
const decoder = new TextDecoder();

export interface ApiCryptoEnvelope {
  encrypted: boolean;
  iv: string;
  payload: string;
}

interface ApiPublicKeyResponse {
  algorithm: string;
  publicKey: string;
  fingerprint: string;
}

let currentPublicKeyFingerprint = '';
let cachedPublicKey: ApiPublicKeyResponse | null = readCachedPublicKey();
let cachedImportedPublicKey: CryptoKey | null = null;
let cachedImportFingerprint = '';
let pendingPublicKeyRequest: Promise<ApiPublicKeyResponse> | null = null;

export function isApiCryptoSupported() {
  return typeof window !== 'undefined' && !!window.crypto?.subtle;
}

export function isApiCryptoEnvelope(data: unknown): data is ApiCryptoEnvelope {
  if (!data || typeof data !== 'object') {
    return false;
  }
  const envelope = data as Partial<ApiCryptoEnvelope>;
  return envelope.encrypted === true && typeof envelope.iv === 'string' && typeof envelope.payload === 'string';
}

export function shouldSkipApiCrypto(data: unknown) {
  return (
    data == null ||
    data instanceof FormData ||
    data instanceof URLSearchParams ||
    data instanceof Blob ||
    data instanceof ArrayBuffer
  );
}

export async function attachApiCryptoHeaders(headers: Record<string, string>) {
  const publicKey = await getServerPublicKey();
  const aesKey = await window.crypto.subtle.generateKey({ name: 'AES-GCM', length: 256 }, true, [
    'encrypt',
    'decrypt',
  ]);
  const rawAesKey = new Uint8Array(await window.crypto.subtle.exportKey('raw', aesKey));
  const encryptedKey = await window.crypto.subtle.encrypt({ name: 'RSA-OAEP' }, publicKey, rawAesKey);

  headers[ENCRYPTION_FLAG_HEADER] = '1';
  headers[ENCRYPTED_KEY_HEADER] = bytesToBase64(new Uint8Array(encryptedKey));
  headers[KEY_FINGERPRINT_HEADER] = currentPublicKeyFingerprint;

  return aesKey;
}

export async function encryptApiPayload(data: unknown, aesKey: CryptoKey): Promise<ApiCryptoEnvelope> {
  const iv = window.crypto.getRandomValues(new Uint8Array(AES_GCM_IV_LENGTH));
  const payload = await window.crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    aesKey,
    encoder.encode(JSON.stringify(data)),
  );
  return {
    encrypted: true,
    iv: bytesToBase64(iv),
    payload: bytesToBase64(new Uint8Array(payload)),
  };
}

export async function decryptApiPayload<T>(envelope: ApiCryptoEnvelope, aesKey: CryptoKey): Promise<T> {
  const plainBuffer = await window.crypto.subtle.decrypt(
    { name: 'AES-GCM', iv: base64ToBytes(envelope.iv) },
    aesKey,
    base64ToBytes(envelope.payload),
  );
  return JSON.parse(decoder.decode(plainBuffer)) as T;
}

export function clearApiCryptoPublicKeyCache() {
  cachedPublicKey = null;
  cachedImportedPublicKey = null;
  cachedImportFingerprint = '';
  currentPublicKeyFingerprint = '';
  pendingPublicKeyRequest = null;
  if (typeof window !== 'undefined') {
    window.sessionStorage.removeItem(API_PUBLIC_KEY_CACHE_KEY);
  }
}

export async function fetchApiPublicKey(forceRefresh = false): Promise<ApiPublicKeyResponse> {
  if (forceRefresh) {
    clearApiCryptoPublicKeyCache();
  }
  if (cachedPublicKey) {
    currentPublicKeyFingerprint = cachedPublicKey.fingerprint;
    return cachedPublicKey;
  }
  if (!pendingPublicKeyRequest) {
    pendingPublicKeyRequest = fetch(API_PUBLIC_KEY_URL, {
      cache: 'no-store',
      credentials: 'include',
    })
      .then(async (response) => {
        const result = (await response.json()) as ApiResult<ApiPublicKeyResponse>;
        if (result.code !== 200 || !result.data?.publicKey || !result.data?.fingerprint) {
          throw new Error(result.message || '接口加密公钥获取失败');
        }
        cachedPublicKey = result.data;
        currentPublicKeyFingerprint = result.data.fingerprint;
        persistPublicKey(result.data);
        return result.data;
      })
      .finally(() => {
        pendingPublicKeyRequest = null;
      });
  }
  return pendingPublicKeyRequest;
}

async function getServerPublicKey(forceRefresh = false) {
  const data = await fetchApiPublicKey(forceRefresh);
  if (cachedImportedPublicKey && cachedImportFingerprint === data.fingerprint) {
    return cachedImportedPublicKey;
  }
  cachedImportedPublicKey = await window.crypto.subtle.importKey(
    'spki',
    base64ToBytes(data.publicKey),
    { name: 'RSA-OAEP', hash: 'SHA-256' },
    false,
    ['encrypt'],
  );
  cachedImportFingerprint = data.fingerprint;
  currentPublicKeyFingerprint = data.fingerprint;
  return cachedImportedPublicKey;
}

function bytesToBase64(bytes: Uint8Array) {
  let binary = '';
  for (let index = 0; index < bytes.length; index += 1) {
    binary += String.fromCharCode(bytes[index]);
  }
  return window.btoa(binary);
}

function base64ToBytes(base64: string) {
  const binary = window.atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let index = 0; index < binary.length; index += 1) {
    bytes[index] = binary.charCodeAt(index);
  }
  return bytes;
}

function persistPublicKey(data: ApiPublicKeyResponse) {
  if (typeof window === 'undefined') {
    return;
  }
  window.sessionStorage.setItem(API_PUBLIC_KEY_CACHE_KEY, JSON.stringify(data));
}

function readCachedPublicKey(): ApiPublicKeyResponse | null {
  if (typeof window === 'undefined') {
    return null;
  }
  const raw = window.sessionStorage.getItem(API_PUBLIC_KEY_CACHE_KEY);
  if (!raw) {
    return null;
  }
  try {
    const parsed = JSON.parse(raw) as Partial<ApiPublicKeyResponse>;
    if (
      typeof parsed.algorithm === 'string' &&
      typeof parsed.publicKey === 'string' &&
      typeof parsed.fingerprint === 'string'
    ) {
      currentPublicKeyFingerprint = parsed.fingerprint;
      return parsed as ApiPublicKeyResponse;
    }
  } catch {
    window.sessionStorage.removeItem(API_PUBLIC_KEY_CACHE_KEY);
  }
  return null;
}
