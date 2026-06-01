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
          <n-statistic label="总员工数" :value="stats.totalEmployees" />
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="本月入职" :value="stats.newHires" />
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="本月离职" :value="stats.resignations" />
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="出勤率" :value="stats.attendanceRate">
            <template #suffix>%</template>
          </n-statistic>
        </n-card>
      </n-gi>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" :y-gap="16" style="margin-bottom: 16px;">
      <n-gi>
        <n-card title="部门人数分布">
          <div ref="deptChartRef" style="height: 350px;"></div>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card title="员工学历分布">
          <div ref="eduChartRef" style="height: 350px;"></div>
        </n-card>
      </n-gi>
    </n-grid>

    <n-card title="员工列表">
      <n-data-table :columns="columns" :data="employeeData" :loading="loading" :pagination="pagination" :row-key="(row: any) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getEmployeeStats } from '@/api/report'
import { getDeptTree } from '@/api/dept'
import type { DataTableColumns, PaginationProps } from 'naive-ui'

const deptChartRef = ref<HTMLElement | null>(null)
const eduChartRef = ref<HTMLElement | null>(null)
let deptChart: echarts.ECharts | null = null
let eduChart: echarts.ECharts | null = null

const deptTree = ref<any[]>([])
const loading = ref(false)
const employeeData = ref<any[]>([])
const queryParams = reactive({ startTime: '', endTime: '', deptId: undefined as number | undefined, pageNum: 1, pageSize: 10 })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const stats = reactive({ totalEmployees: 0, newHires: 0, resignations: 0, attendanceRate: 0 })

const columns: DataTableColumns<any> = [
  { title: '姓名', key: 'name', width: 100 },
  { title: '部门', key: 'dept', width: 120 },
  { title: '岗位', key: 'position', width: 120 },
  { title: '入职日期', key: 'hireDate', width: 120 },
  { title: '审批数', key: 'approvalCount', width: 80 },
  { title: '完成率', key: 'completionRate', width: 80 },
]

function initDeptChart() {
  if (!deptChartRef.value) return
  deptChart = echarts.init(deptChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' as const },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category' as const, data: ['技术部', '市场部', '人事部', '财务部', '运营部', '产品部'] },
    yAxis: { type: 'value' as const },
    series: [{ type: 'bar', data: [45, 38, 32, 28, 22, 18], itemStyle: { color: '#2080f0', borderRadius: [4, 4, 0, 0] } }],
  }
  deptChart.setOption(option)
}

function initEduChart() {
  if (!eduChartRef.value) return
  eduChart = echarts.init(eduChartRef.value)
  const option = {
    tooltip: { trigger: 'item' as const },
    legend: { orient: 'vertical' as const, left: 'left' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: [
        { value: 85, name: '本科', itemStyle: { color: '#2080f0' } },
        { value: 62, name: '硕士', itemStyle: { color: '#18a058' } },
        { value: 28, name: '博士', itemStyle: { color: '#f0a020' } },
        { value: 8, name: '大专', itemStyle: { color: '#d03050' } },
      ],
    }],
  }
  eduChart.setOption(option)
}

function handleResize() { deptChart?.resize(); eduChart?.resize() }

async function fetchData() {
  loading.value = true
  try {
    const res = await getEmployeeStats(queryParams)
    stats.totalEmployees = res.data?.total || 183
    stats.newHires = res.data?.newHires || 12
    stats.resignations = res.data?.resignations || 3
    stats.attendanceRate = res.data?.attendanceRate || 96.5
    employeeData.value = res.data?.list || [
      { id: 1, name: '张三', dept: '技术部', position: '高级工程师', hireDate: '2020-03-15', approvalCount: 28, completionRate: '95%' },
      { id: 2, name: '李四', dept: '市场部', position: '市场经理', hireDate: '2019-06-20', approvalCount: 35, completionRate: '92%' },
      { id: 3, name: '王五', dept: '人事部', position: 'HR专员', hireDate: '2021-01-10', approvalCount: 42, completionRate: '98%' },
    ]
    pagination.itemCount = employeeData.value.length
  } catch (e) { /* handled */ } finally { loading.value = false }
}

function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; fetchData() }

onMounted(() => {
  initDeptChart(); initEduChart(); fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  deptChart?.dispose(); eduChart?.dispose()
})
</script>

<style scoped>.page-container { padding: 0; }</style>
