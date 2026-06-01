import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
  captchaCode: string
  uuid: string
}

export interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
}

export interface CaptchaResult {
  uuid: string
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

export function getCaptchaApi() {
  return request.get<any, any>('/auth/captcha')
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
