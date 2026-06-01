<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="标题">
          <n-input v-model:value="queryParams.title" placeholder="请输入标题" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-select v-model:value="queryParams.status" placeholder="请选择" clearable style="width: 120px;" :options="statusOptions" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">搜索</n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: any) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <!-- Detail Drawer -->
    <n-drawer v-model:show="drawerVisible" :width="600" placement="right">
      <n-drawer-content title="审批详情">
        <n-descriptions :column="2" label-placement="left" bordered size="small">
          <n-descriptions-item label="标题">{{ detail.title }}</n-descriptions-item>
          <n-descriptions-item label="审批类型">{{ detail.typeName }}</n-descriptions-item>
          <n-descriptions-item label="发起人">{{ detail.initiatorName }}</n-descriptions-item>
          <n-descriptions-item label="发起时间">{{ detail.createTime }}</n-descriptions-item>
          <n-descriptions-item label="状态">
            <n-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</n-tag>
          </n-descriptions-item>
        </n-descriptions>

        <n-divider>审批表单</n-divider>
        <n-descriptions :column="1" label-placement="left" bordered size="small">
          <n-descriptions-item v-for="(value, key) in detail.formData" :key="key" :label="String(key)">
            {{ value }}
          </n-descriptions-item>
        </n-descriptions>

        <n-divider>审批流程</n-divider>
        <n-timeline>
          <n-timeline-item v-for="item in timeline" :key="item.id" :type="timelineItemType(item.status)" :title="item.nodeName" :content="`${item.assigneeName || '待分配'} - ${item.status === 'completed' ? '已审批' : item.status === 'pending' ? '待审批' : '已拒绝'}`" :time="item.completeTime || ''" />
        </n-timeline>
      </n-drawer-content>
    </n-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, PaginationProps } from 'naive-ui'
import { getMyApprovals, getApprovalDetail, getApprovalProcessTimeline, withdrawApproval } from '@/api/approval'

const message = useMessage()

const loading = ref(false); const tableData = ref<any[]>([])
const drawerVisible = ref(false); const detail = ref<any>({})
const timeline = ref<any[]>([])

const queryParams = reactive({ pageNum: 1, pageSize: 10, title: '', status: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

const statusOptions = [
  { label: '审批中', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已拒绝', value: 'rejected' },
  { label: '已撤回', value: 'withdrawn' },
]

function statusLabel(status: string) {
  const map: Record<string, string> = { pending: '审批中', approved: '已通过', rejected: '已拒绝', withdrawn: '已撤回' }
  return map[status] || status
}

function statusTagType(status: string) {
  const map: Record<string, 'warning' | 'success' | 'error' | 'info'> = { pending: 'warning', approved: 'success', rejected: 'error', withdrawn: 'info' }
  return map[status] || 'default' as any
}

function timelineItemType(status: string) {
  const map: Record<string, 'success' | 'warning' | 'error' | 'info'> = { completed: 'success', pending: 'warning', rejected: 'error' }
  return map[status] || 'info'
}

const columns: DataTableColumns<any> = [
  { title: '标题', key: 'title', width: 200, ellipsis: { tooltip: true } },
  { title: '审批类型', key: 'typeName', width: 120 },
  { title: '发起人', key: 'initiatorName', width: 100 },
  { title: '状态', key: 'status', width: 100, render: (row) => h(NTag, { type: statusTagType(row.status), size: 'small' }, { default: () => statusLabel(row.status) }) },
  { title: '发起时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 180, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleViewDetail(row) }, { default: () => '查看' }),
    row.status === 'pending' ? h(NButton, { text: true, type: 'warning', size: 'small', onClick: () => handleWithdraw(row) }, { default: () => '撤回' }) : null,
  ].filter(Boolean) }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getMyApprovals(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.title = ''; queryParams.status = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }

async function handleViewDetail(row: any) {
  try {
    const res = await getApprovalDetail(row.id)
    detail.value = res.data || {}
    const tlRes = await getApprovalProcessTimeline(row.id)
    timeline.value = tlRes.data || []
  } catch (e) { /* handled */ }
  drawerVisible.value = true
}

function handleWithdraw(row: any) {
  try {
    withdrawApproval(row.id)
    message.success('撤回成功')
    fetchData()
  } catch (e) { /* handled */ }
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
