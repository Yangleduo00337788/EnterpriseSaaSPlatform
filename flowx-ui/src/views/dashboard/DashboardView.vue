<template>
  <div class="page-container">
    <!-- Welcome Card -->
    <n-card style="margin-bottom: 16px;">
      <div style="display: flex; align-items: center; justify-content: space-between;">
        <div>
          <h2 style="margin: 0 0 8px; font-size: 20px; font-weight: 600;">
            欢迎回来，{{ userStore.userInfo?.nickname || '用户' }}
          </h2>
          <p style="margin: 0; color: #666;">{{ greeting }}，祝你工作愉快！</p>
        </div>
        <n-space>
          <n-button type="primary" @click="$router.push('/approval/pending')">
            待我审批 ({{ pendingCount }})
          </n-button>
          <n-button @click="$router.push('/approval/instances')">我的申请</n-button>
        </n-space>
      </div>
    </n-card>

    <!-- Stat Cards -->
    <n-grid :cols="4" :x-gap="16" :y-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <n-card>
          <n-statistic label="审批总数" :value="stats.totalApprovals">
            <template #prefix>
              <n-icon size="20" color="#2080f0"><FileTextOutlined /></n-icon>
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="待审批" :value="stats.pendingApprovals">
            <template #prefix>
              <n-icon size="20" color="#f0a020"><ClockCircleOutlined /></n-icon>
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="今日提交" :value="stats.todaySubmissions">
            <template #prefix>
              <n-icon size="20" color="#18a058"><SendOutlined /></n-icon>
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="完成率" :value="stats.completionRate">
            <template #suffix>%</template>
            <template #prefix>
              <n-icon size="20" color="#d03050"><CheckCircleOutlined /></n-icon>
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
    </n-grid>

    <!-- Charts -->
    <n-grid :cols="2" :x-gap="16" :y-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <n-card title="审批趋势（近7天）">
          <div ref="trendChartRef" style="height: 300px;"></div>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card title="审批状态分布">
          <div ref="statusChartRef" style="height: 300px;"></div>
        </n-card>
      </n-gi>
    </n-grid>

    <!-- Recent Approvals -->
    <n-card title="最近审批" style="margin-bottom: 16px;">
      <template #header-extra>
        <n-button text type="primary" @click="$router.push('/approval/instances')">查看全部</n-button>
      </template>
      <n-data-table
        :columns="recentColumns"
        :data="recentApprovals"
        :bordered="false"
        size="small"
      />
    </n-card>

    <!-- Quick Actions -->
    <n-card title="快捷操作">
      <n-space>
        <n-button @click="$router.push('/approval/pending')">待我审批</n-button>
        <n-button @click="$router.push('/workflow/definitions')">流程管理</n-button>
        <n-button @click="$router.push('/system/users')">用户管理</n-button>
        <n-button @click="$router.push('/message/notifications')">消息通知</n-button>
        <n-button @click="$router.push('/ai/chat')">AI 助手</n-button>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { NTag, NButton } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import * as echarts from 'echarts'

const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

const pendingCount = ref(0)
const stats = reactive({
  totalApprovals: 0,
  pendingApprovals: 0,
  todaySubmissions: 0,
  completionRate: 0,
})

const trendChartRef = ref<HTMLElement | null>(null)
const statusChartRef = ref<HTMLElement | null>(null)
let trendChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null

interface RecentApproval {
  id: number
  title: string
  type: string
  status: string
  submitTime: string
  approver: string
}

const recentApprovals = ref<RecentApproval[]>([
  { id: 1, title: '张三的请假申请', type: '请假审批', status: 'approved', submitTime: '2024-01-15 10:30', approver: '李经理' },
  { id: 2, title: '设备采购申请', type: '采购审批', status: 'pending', submitTime: '2024-01-15 09:00', approver: '王总监' },
  { id: 3, title: '出差报销申请', type: '报销审批', status: 'rejected', submitTime: '2024-01-14 16:20', approver: '赵经理' },
  { id: 4, title: '项目立项申请', type: '项目审批', status: 'approved', submitTime: '2024-01-14 14:10', approver: '刘总' },
  { id: 5, title: '合同审批申请', type: '合同审批', status: 'pending', submitTime: '2024-01-14 11:30', approver: '陈经理' },
])

const statusMap: Record<string, { label: string; type: 'success' | 'warning' | 'error' | 'info' }> = {
  approved: { label: '已通过', type: 'success' },
  pending: { label: '审批中', type: 'warning' },
  rejected: { label: '已拒绝', type: 'error' },
  withdrawn: { label: '已撤回', type: 'info' },
}

const recentColumns: DataTableColumns<RecentApproval> = [
  { title: '标题', key: 'title', ellipsis: { tooltip: true } },
  { title: '类型', key: 'type', width: 100 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row) {
      const s = statusMap[row.status]
      return h(NTag, { type: s?.type || 'default', size: 'small' }, { default: () => s?.label || row.status })
    },
  },
  { title: '审批人', key: 'approver', width: 100 },
  { title: '提交时间', key: 'submitTime', width: 160 },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    render() {
      return h(NButton, { text: true, type: 'primary', size: 'small' }, { default: () => '查看' })
    },
  },
]

function initTrendChart() {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' as const },
    legend: { data: ['提交数', '审批通过数', '审批拒绝数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category' as const,
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    },
    yAxis: { type: 'value' as const },
    series: [
      { name: '提交数', type: 'line', smooth: true, data: [12, 18, 15, 22, 19, 8, 5], areaStyle: { opacity: 0.1 }, itemStyle: { color: '#2080f0' } },
      { name: '审批通过数', type: 'line', smooth: true, data: [10, 15, 13, 18, 16, 7, 4], areaStyle: { opacity: 0.1 }, itemStyle: { color: '#18a058' } },
      { name: '审批拒绝数', type: 'line', smooth: true, data: [2, 3, 2, 4, 3, 1, 1], areaStyle: { opacity: 0.1 }, itemStyle: { color: '#d03050' } },
    ],
  }
  trendChart.setOption(option)
}

function initStatusChart() {
  if (!statusChartRef.value) return
  statusChart = echarts.init(statusChartRef.value)
  const option = {
    tooltip: { trigger: 'item' as const },
    legend: { orient: 'vertical' as const, left: 'left' },
    series: [
      {
        name: '审批状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: [
          { value: 156, name: '已通过', itemStyle: { color: '#18a058' } },
          { value: 23, name: '审批中', itemStyle: { color: '#f0a020' } },
          { value: 12, name: '已拒绝', itemStyle: { color: '#d03050' } },
          { value: 8, name: '已撤回', itemStyle: { color: '#2080f0' } },
        ],
      },
    ],
  }
  statusChart.setOption(option)
}

function handleResize() {
  trendChart?.resize()
  statusChart?.resize()
}

onMounted(() => {
  initTrendChart()
  initStatusChart()
  window.addEventListener('resize', handleResize)
  stats.totalApprovals = 199
  stats.pendingApprovals = 23
  stats.todaySubmissions = 8
  stats.completionRate = 93.5
  pendingCount.value = 23
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  statusChart?.dispose()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
</style>
