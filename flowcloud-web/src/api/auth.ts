import request from '@/utils/request';
import type {
  ApiResult,
  AvatarUploadResult,
  ChangePasswordPayload,
  LoginForm,
  ProfileUpdatePayload,
  RegisterForm,
  UserInfo,
} from '@/types';

export const login = (data: LoginForm) =>
  request.post<unknown, ApiResult<UserInfo>>('/auth/login', data);

export const register = (data: RegisterForm) =>
  request.post<unknown, ApiResult<void>>('/auth/register', data);

export const getCurrentUser = () =>
  request.get<unknown, ApiResult<UserInfo>>('/auth/me');

export const updateCurrentProfile = (data: ProfileUpdatePayload) =>
  request.put<unknown, ApiResult<UserInfo>>('/auth/profile', data);

export const uploadCurrentUserAvatar = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post<unknown, ApiResult<AvatarUploadResult>>(
    '/auth/profile/avatar',
    formData,
    { headers: { 'Content-Type': 'multipart/form-data' } },
  );
};

export const changeCurrentPassword = (data: ChangePasswordPayload) =>
  request.put<unknown, ApiResult<void>>('/auth/profile/password', data);
