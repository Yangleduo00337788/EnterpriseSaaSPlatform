import axios from 'axios';
import { Toast } from '@douyinfe/semi-ui';
import type { ApiResult } from '@/types';

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResult;
    if (res.code !== 200) {
      Toast.error(res.message || '请求失败');
      if (res.code === 401 || res.code === 1005) {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
      return Promise.reject(new Error(res.message));
    }
    return response.data;
  },
  (error) => {
    Toast.error(error.message || '网络错误');
    return Promise.reject(error);
  }
);

export default request;
