<template>
  <div class="page-container">
    <n-card>
      <template #header>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <span>通知中心</span>
          <n-space>
            <n-button size="small" @click="handleMarkAllRead">全部已读</n-button>
          </n-space>
        </div>
      </template>

      <n-tabs v-model:value="activeTab" @update:value="handleTabChange">
        <n-tab-pane name="all" tab="全部">
          <n-list hoverable clickable>
            <n-list-item v-for="item in notifications" :key="item.id" @click="handleRead(item)">
              <template #prefix>
                <n-badge :dot="item.status === '0'" processing>
                  <n-icon size="20" :color="getTypeColor(item.type)">
                    <BellOutlined />
                  </n-icon>
                </n-badge>
              </template>
              <n-thing :title="item.title" :description="item.content">
                <template #header-extra>
                  <span style="font-size: 12px; color: #999;">{{ item.createTime }}</span>
                </template>
              </n-thing>
            </n-list-item>
            <n-empty v-if="notifications.length === 0" description="暂无通知" />
          </n-list>
        </n-tab-pane>
        <n-tab-pane name="unread" tab="未读">
          <n-list hoverable clickable>
            <n-list-item v-for="item in unreadNotifications" :key="item.id" @click="handleRead(item)">
              <template #prefix>
                <n-badge dot processing>
                  <n-icon size="20" :color="getTypeColor(item.type)">
                    <BellOutlined />
                  </n-icon>
                </n-badge>
              </template>
              <n-thing :title="item.title" :description="item.content">
                <template #header-extra>
                  <span style="font-size: 12px; color: #999;">{{ item.createTime }}</span>
                </template>
              </n-thing>
            </n-list-item>
            <n-empty v-if="unreadNotifications.length === 0" description="暂无未读通知" />
          </n-list>
        </n-tab-pane>
      </n-tabs>

      <div style="display: flex; justify-content: center; margin-top: 16px;">
        <n-pagination v-model:page="queryParams.pageNum" :page-size="queryParams.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/message'

const message = useMessage()

const activeTab = ref('all')
const notifications = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 20, status: undefined as string | undefined })

const unreadNotifications = computed(() => notifications.value.filter(n => n.status === '0'))

function getTypeColor(type: string) {
  const map: Record<string, string> = { approval: '#f0a020', system: '#2080f0', message: '#18a058' }
  return map[type] || '#2080f0'
}

async function fetchData() {
  try {
    const res = await getNotifications(queryParams)
    notifications.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
}

function handleTabChange(name: string) {
  queryParams.status = name === 'unread' ? '0' : undefined
  queryParams.pageNum = 1
  fetchData()
}

async function handleRead(item: any) {
  if (item.status === '0') {
    try {
      await markAsRead(String(item.id))
      item.status = '1'
    } catch (e) { /* handled */ }
  }
}

async function handleMarkAllRead() {
  try {
    await markAllAsRead()
    message.success('全部已标记为已读')
    fetchData()
  } catch (e) { /* handled */ }
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
