import axios, { type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/stores/user'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

export interface R<T = any> {
  code: number
  msg: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = userStore.token
    }
    const tenantId = localStorage.getItem('tenantId')
    if (tenantId) {
      config.headers['X-Tenant-Id'] = tenantId
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse<R>) => {
    const res = response.data
    if (res.code !== 200) {
      message.error(res.msg || '请求失败')
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.resetState()
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res as any
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      switch (status) {
        case 401:
          message.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.resetState()
          window.location.href = '/login'
          break
        case 403:
          message.error('没有权限访问')
          break
        case 404:
          message.error('请求资源不存在')
          break
        case 500:
          message.error('服务器内部错误')
          break
        default:
          message.error(`请求错误: ${status}`)
      }
    } else if (error.message.includes('timeout')) {
      message.error('请求超时')
    } else if (error.message.includes('Network Error')) {
      message.error('网络连接异常')
    }
    return Promise.reject(error)
  }
)

export default service
