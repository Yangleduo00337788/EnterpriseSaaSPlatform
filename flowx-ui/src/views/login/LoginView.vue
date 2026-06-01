<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="login-card">
        <div class="login-header">
          <div class="login-logo">
            <img src="/logo.png" alt="FlowX" class="logo-img" />
          </div>
          <h1 class="login-title">FlowX</h1>
          <p class="login-subtitle">企业级工作流管理平台</p>
        </div>
        <n-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          size="large"
        >
          <n-form-item path="username">
            <n-input
              v-model:value="loginForm.username"
              placeholder="请输入用户名"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <n-icon size="18"><UserOutlined /></n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="password">
            <n-input
              v-model:value="loginForm.password"
              type="password"
              show-password-on="click"
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <n-icon size="18"><LockOutlined /></n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="captchaCode">
            <div style="display: flex; gap: 12px; width: 100%;">
              <n-input
                v-model:value="loginForm.captchaCode"
                placeholder="请输入验证码"
                style="flex: 1;"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <n-icon size="18"><SafetyOutlined /></n-icon>
                </template>
              </n-input>
              <div class="captcha-img" @click="refreshCaptcha">
                <img v-if="captchaImg" :src="captchaImg" alt="验证码" />
                <span v-else>获取验证码</span>
              </div>
            </div>
          </n-form-item>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <n-checkbox v-model:checked="rememberMe">记住密码</n-checkbox>
            <n-button text type="primary" size="small">忘记密码?</n-button>
          </div>
          <n-button
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </n-button>
        </n-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCaptchaApi } from '@/api/auth'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()
const formRef = ref<FormInst | null>(null)
const loading = ref(false)
const captchaImg = ref('')
const rememberMe = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: 'admin123',
  captchaCode: '',
  uuid: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

async function refreshCaptcha() {
  try {
    const res = await getCaptchaApi()
    captchaImg.value = `data:image/gif;base64,${res.data.img}`
    loginForm.uuid = res.data.uuid
  } catch (e) {
    // ignore
  }
}

async function handleLogin() {
  try {
    await formRef.value?.validate()
    loading.value = true
    await userStore.login(loginForm)
    await userStore.getUserInfo()
    message.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    if (e?.message) {
      message.error(e.message)
    }
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.login-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}

.logo-img {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(24, 160, 88, 0.3);
  object-fit: cover;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 8px 0 4px;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
}

.captcha-img {
  width: 120px;
  height: 40px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border: 1px solid #e0e0e0;
  flex-shrink: 0;
  transition: border-color 0.2s;
}

.captcha-img:hover {
  border-color: #18a058;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-img span {
  font-size: 12px;
  color: #999;
}
</style>
