<template>
  <n-layout has-sider style="height: 100vh">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
      :native-scrollbar="false"
      style="height: 100vh; border-radius: 0 16px 16px 0; overflow: hidden;"
    >
      <div class="logo-container" :class="{ collapsed }">
        <img src="/logo.png" alt="FlowX" class="logo-img" />
        <span v-if="!collapsed" class="logo-text">FlowX</span>
      </div>
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeMenu"
        @update:value="handleMenuClick"
      />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered style="height: 56px; display: flex; align-items: center; padding: 0 16px; justify-content: space-between; border-radius: 0 0 16px 16px; margin: 0 16px;">
        <div style="display: flex; align-items: center; gap: 16px;">
          <n-breadcrumb>
            <n-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </n-breadcrumb-item>
          </n-breadcrumb>
        </div>
        <div style="display: flex; align-items: center; gap: 16px;">
          <n-badge :value="unreadCount" :max="99">
            <n-button quaternary circle @click="router.push('/message/notifications')">
              <template #icon>
                <n-icon size="18"><BellOutlined /></n-icon>
              </template>
            </n-button>
          </n-badge>
          <n-dropdown :options="userDropdownOptions" @select="handleUserAction">
            <div style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
              <n-avatar :size="32" round style="background-color: #18a058;">
                {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
              </n-avatar>
              <span style="font-size: 14px;">{{ userStore.userInfo?.nickname || '用户' }}</span>
            </div>
          </n-dropdown>
        </div>
      </n-layout-header>
      <n-layout-content
        content-style="padding: 16px;"
        :native-scrollbar="false"
        style="height: calc(100vh - 56px); border-radius: 16px; margin: 0 16px 16px 0;"
      >
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, h, onMounted } from 'vue'
import { useRouter, useRoute, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadCount } from '@/api/message'
import { NIcon } from 'naive-ui'
import type { MenuOption, DropdownOption } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const unreadCount = ref(0)
const cachedViews = ref<string[]>([])

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta.title as string,
  }))
})

function renderIcon(icon: string) {
  return () => h('span', { style: 'font-size: 16px;' }, icon.charAt(0))
}

function buildMenuOptions(routes: RouteRecordRaw[]): MenuOption[] {
  return routes
    .filter(route => !route.meta?.hidden)
    .map(route => {
      const option: MenuOption = {
        key: route.path,
        label: route.meta?.title as string || route.name as string,
        icon: route.meta?.icon ? renderIcon(route.meta.icon as string) : undefined,
      }
      if (route.children && route.children.length > 0) {
        const visibleChildren = route.children.filter(c => !c.meta?.hidden)
        if (visibleChildren.length === 1 && !visibleChildren[0].meta?.title) {
          return {
            key: visibleChildren[0].path,
            label: route.meta?.title as string,
            icon: route.meta?.icon ? renderIcon(route.meta.icon as string) : undefined,
          }
        }
        option.children = buildMenuOptions(route.children)
      }
      return option
    })
}

const mainRoute = router.options.routes.find(r => r.path === '/')
const menuOptions = computed(() => {
  if (mainRoute?.children) {
    return buildMenuOptions(mainRoute.children)
  }
  return []
})

const userDropdownOptions: DropdownOption[] = [
  { label: '个人中心', key: 'profile' },
  { type: 'divider', key: 'd1' },
  { label: '退出登录', key: 'logout' },
]

function handleMenuClick(key: string) {
  router.push(key.startsWith('/') ? key : `/${key}`)
}

async function handleUserAction(key: string) {
  if (key === 'logout') {
    await userStore.logout()
    router.push('/login')
  } else if (key === 'profile') {
    // navigate to profile
  }
}

async function fetchUnreadCount() {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    // ignore
  }
}

onMounted(() => {
  fetchUnreadCount()
})
</script>

<style scoped>
.logo-container {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  border-bottom: 1px solid rgb(239, 239, 245);
  overflow: hidden;
  margin: 8px;
  border-radius: 12px;
  background: rgba(24, 160, 88, 0.04);
}

.logo-container.collapsed {
  justify-content: center;
  padding: 0;
}

.logo-img {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  flex-shrink: 0;
  object-fit: cover;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #18a058;
  white-space: nowrap;
}

:deep(.n-menu-item-content-header) {
  font-size: 14px;
}
</style>
