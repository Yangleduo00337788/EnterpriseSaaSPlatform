import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
  captchaCode: string
  captchaKey: string
}

export interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
}

export interface CaptchaResult {
  captchaKey: string
  img: string
}

export function loginApi(data: LoginParams) {
  return request.post<any, any>('/auth/login', data)
}

export function registerApi(data: any) {
  return request.post<any, any>('/auth/register', data)
}

export function logoutApi() {
  return request.post<any, any>('/auth/logout')
}
export async function getCaptchaApi() {
  const res = await request.get<any, any>("/auth/captcha")
  const raw = res.data.img as string
  const base64 = raw.includes(",") ? raw.split(",")[1] : raw
  const byteCharacters = atob(base64)
  const byteArray = new Uint8Array([...byteCharacters].map(c => c.charCodeAt(0)))
  const blob = new Blob([byteArray], { type: "image/png" })
  return {
    data: {
      uuid: res.data.uuid as string,
      img: URL.createObjectURL(blob)
    }
  }
}

export function refreshTokenApi() {
  return request.post<any, any>('/auth/refresh-token')
}

export function getUserInfoApi() {
  return request.get<any, any>('/auth/user-info')
}

export function getRoutersApi() {
  return request.get<any, any>('/auth/routers')
}
