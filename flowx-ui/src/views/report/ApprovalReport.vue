<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="时间范围">
          <n-date-picker v-model:formatted-value="queryParams.startTime" type="daterange" style="width: 300px;" />
        </n-form-item>
        <n-form-item label="部门">
          <n-tree-select v-model:value="queryParams.deptId" placeholder="请选择部门" clearable style="width: 180px;" :options="deptTree" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="fetchData">查询</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-grid :cols="4" :x-gap="16" :y-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <n-card>
          <n-statistic label="审批总数" :value="stats.total" />
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="通过数" :value="stats.approved">
            <template #prefix><n-icon color="#18a058"><CheckCircleOutlined /></n-icon></template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="拒绝数" :value="stats.rejected">
            <template #prefix><n-icon color="#d03050"><CloseCircleOutlined /></n-icon></template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="平均耗时" :value="stats.avgDuration">
            <template #suffix>小时</template>
          </n-statistic>
        </n-card>
      </n-gi>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" :y-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <n-card title="审批趋势">
          <div ref="trendChartRef" style="height: 350px;"></div>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card title="审批类型分布">
          <div ref="typeChartRef" style="height: 350px;"></div>
        </n-card>
      </n-gi>
    </n-grid>

    <n-card title="部门审批排行">
      <n-data-table :columns="rankColumns" :data="rankData" :bordered="false" size="small" />
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, h } from 'vue'
import { NTag } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import * as echarts from 'echarts'
import { getApprovalStats, getApprovalTrend, getApprovalTypeDistribution } from '@/api/report'
import { getDeptTree } from '@/api/dept'

const trendChartRef = ref<HTMLElement | null>(null)
const typeChartRef = ref<HTMLElement | null>(null)
let trendChart: echarts.ECharts | null = null
let typeChart: echarts.ECharts | null = null

const deptTree = ref<any[]>([])
const queryParams = reactive({ startTime: '', endTime: '', deptId: undefined as number | undefined })
const stats = reactive({ total: 0, approved: 0, rejected: 0, avgDuration: 0 })

const rankData = ref<any[]>([
  { rank: 1, dept: '技术部', total: 45, approved: 40, rejected: 5, rate: '88.9%' },
  { rank: 2, dept: '市场部', total: 38, approved: 35, rejected: 3, rate: '92.1%' },
  { rank: 3, dept: '人事部', total: 32, approved: 30, rejected: 2, rate: '93.8%' },
  { rank: 4, dept: '财务部', total: 28, approved: 25, rejected: 3, rate: '89.3%' },
  { rank: 5, dept: '运营部', total: 22, approved: 20, rejected: 2, rate: '90.9%' },
])

const rankColumns: DataTableColumns<any> = [
  { title: '排名', key: 'rank', width: 80 },
  { title: '部门', key: 'dept', width: 120 },
  { title: '审批总数', key: 'total', width: 100 },
  { title: '通过数', key: 'approved', width: 100 },
  { title: '拒绝数', key: 'rejected', width: 100 },
  { title: '通过率', key: 'rate', width: 100 },
]

function initTrendChart() {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' as const },
    legend: { data: ['提交数', '通过数', '拒绝数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category' as const, data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'] },
    yAxis: { type: 'value' as const },
    series: [
      { name: '提交数', type: 'bar', data: [120, 132, 101, 134, 90, 230, 210], itemStyle: { color: '#2080f0' } },
      { name: '通过数', type: 'bar', data: [100, 112, 81, 114, 70, 200, 190], itemStyle: { color: '#18a058' } },
      { name: '拒绝数', type: 'bar', data: [20, 20, 20, 20, 20, 30, 20], itemStyle: { color: '#d03050' } },
    ],
  }
  trendChart.setOption(option)
}

function initTypeChart() {
  if (!typeChartRef.value) return
  typeChart = echarts.init(typeChartRef.value)
  const option = {
    tooltip: { trigger: 'item' as const },
    legend: { orient: 'vertical' as const, left: 'left' },
    series: [{
      name: '审批类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: [
        { value: 580, name: '请假审批', itemStyle: { color: '#2080f0' } },
        { value: 484, name: '报销审批', itemStyle: { color: '#18a058' } },
        { value: 300, name: '采购审批', itemStyle: { color: '#f0a020' } },
        { value: 200, name: '出差审批', itemStyle: { color: '#d03050' } },
        { value: 150, name: '合同审批', itemStyle: { color: '#8a2be2' } },
      ],
    }],
  }
  typeChart.setOption(option)
}

function handleResize() {
  trendChart?.resize()
  typeChart?.resize()
}

async function fetchData() {
  try {
    const res = await getApprovalStats(queryParams)
    Object.assign(stats, res.data || {})
  } catch (e) { /* handled */ }
}

onMounted(() => {
  initTrendChart()
  initTypeChart()
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  typeChart?.dispose()
})
</script>

<style scoped>.page-container { padding: 0; }</style>
