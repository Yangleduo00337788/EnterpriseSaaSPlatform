import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, logoutApi, getUserInfoApi } from '@/api/auth'
import { useRouter } from 'vue-router'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  sex: number
  deptId: number
  deptName: string
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  const isAuthenticated = computed(() => !!token.value)

  const hasPermission = (permission: string) => {
    if (permissions.value.includes('*:*:*')) return true
    return permissions.value.includes(permission)
  }

  const hasRole = (role: string) => {
    if (roles.value.includes('admin')) return true
    return roles.value.includes(role)
  }

  async function login(loginForm: { username: string; password: string; captchaCode: string; uuid: string }) {
    const res = await loginApi(loginForm)
    const { accessToken, tokenType } = res.data
    token.value = `${tokenType} ${accessToken}`
    localStorage.setItem('token', token.value)
    return res
  }

  async function getUserInfo() {
    const res = await getUserInfoApi()
    const data = res.data
    userInfo.value = data.user
    roles.value = data.roles || []
    permissions.value = data.permissions || []
    return data
  }

  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      // ignore logout error
    }
    resetState()
  }

  function resetState() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    localStorage.removeItem('token')
  }

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  return {
    token,
    userInfo,
    roles,
    permissions,
    isAuthenticated,
    hasPermission,
    hasRole,
    login,
    getUserInfo,
    logout,
    resetState,
    setToken,
  }
})
