import request from '@/utils/request';
import type { ApiResult, LoginForm, RegisterForm, UserInfo } from '@/types';

export const login = (data: LoginForm) =>
  request.post<unknown, ApiResult<UserInfo>>('/auth/login', data);

export const register = (data: RegisterForm) =>
  request.post<unknown, ApiResult<void>>('/auth/register', data);

export const getCurrentUser = () =>
  request.get<unknown, ApiResult<UserInfo>>('/auth/me');
