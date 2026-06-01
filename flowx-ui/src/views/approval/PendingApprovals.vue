<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="标题">
          <n-input v-model:value="queryParams.title" placeholder="请输入标题" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="类型">
          <n-select v-model:value="queryParams.typeId" placeholder="请选择" clearable style="width: 140px;" :options="typeOptions" />
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

    <!-- Approve/Reject Modal -->
    <n-modal v-model:show="actionModalVisible" :title="actionTitle" preset="card" style="width: 500px;" :segmented="{ content: true, footer: true }">
      <n-form ref="actionFormRef" :model="actionForm" :rules="actionFormRules" label-placement="top">
        <n-form-item label="审批意见" path="comment">
          <n-input v-model:value="actionForm.comment" type="textarea" :rows="4" :placeholder="actionType === 'approve' ? '请输入审批通过意见（选填）' : '请输入拒绝原因'" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="actionModalVisible = false">取消</n-button>
          <n-button :type="actionType === 'approve' ? 'success' : 'error'" :loading="submitLoading" @click="handleActionSubmit">
            {{ actionType === 'approve' ? '通过' : '拒绝' }}
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- Detail Drawer -->
    <n-drawer v-model:show="drawerVisible" :width="600" placement="right">
      <n-drawer-content title="审批详情">
        <n-descriptions :column="2" label-placement="left" bordered size="small">
          <n-descriptions-item label="标题">{{ detail.title }}</n-descriptions-item>
          <n-descriptions-item label="审批类型">{{ detail.typeName }}</n-descriptions-item>
          <n-descriptions-item label="发起人">{{ detail.initiatorName }}</n-descriptions-item>
          <n-descriptions-item label="发起时间">{{ detail.createTime }}</n-descriptions-item>
          <n-descriptions-item label="状态">
            <n-tag type="warning" size="small">待审批</n-tag>
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

        <template #footer>
          <n-space justify="end">
            <n-button type="error" @click="openRejectModal(currentDetailId)">拒绝</n-button>
            <n-button type="success" @click="openApproveModal(currentDetailId)">通过</n-button>
          </n-space>
        </template>
      </n-drawer-content>
    </n-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, computed } from 'vue'
import { useMessage, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules, PaginationProps } from 'naive-ui'
import { getPendingApprovals, approveTask, rejectTask, getApprovalDetail, getApprovalProcessTimeline, getApprovalTypes } from '@/api/approval'

const message = useMessage()

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([])
const drawerVisible = ref(false); const detail = ref<any>({}); const timeline = ref<any[]>([])
const actionModalVisible = ref(false); const actionFormRef = ref<FormInst | null>(null)
const actionType = ref<'approve' | 'reject'>('approve')
const currentDetailId = ref(0)
const typeOptions = ref<any[]>([])

const actionTitle = computed(() => actionType.value === 'approve' ? '审批通过' : '审批拒绝')

const queryParams = reactive({ pageNum: 1, pageSize: 10, title: '', typeId: undefined as number | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

const actionForm = reactive({ comment: '' })
const actionFormRules: FormRules = {
  comment: actionType.value === 'reject' ? { required: true, message: '请输入拒绝原因', trigger: 'blur' } : {},
}

function timelineItemType(status: string) {
  const map: Record<string, 'success' | 'warning' | 'error' | 'info'> = { completed: 'success', pending: 'warning', rejected: 'error' }
  return map[status] || 'info'
}

const columns: DataTableColumns<any> = [
  { title: '标题', key: 'title', width: 200, ellipsis: { tooltip: true } },
  { title: '审批类型', key: 'typeName', width: 120 },
  { title: '发起人', key: 'initiatorName', width: 100 },
  { title: '发起时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 220, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleViewDetail(row) }, { default: () => '查看' }),
    h(NButton, { text: true, type: 'success', size: 'small', onClick: () => openApproveModal(row.id) }, { default: () => '通过' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => openRejectModal(row.id) }, { default: () => '拒绝' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getPendingApprovals(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

async function fetchTypeOptions() {
  try {
    const res = await getApprovalTypes()
    typeOptions.value = (res.data || []).map((t: any) => ({ label: t.typeName, value: t.id }))
  } catch (e) { /* handled */ }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.title = ''; queryParams.typeId = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }

async function handleViewDetail(row: any) {
  try {
    const res = await getApprovalDetail(row.id)
    detail.value = res.data || {}
    const tlRes = await getApprovalProcessTimeline(row.id)
    timeline.value = tlRes.data || []
    currentDetailId.value = row.id
  } catch (e) { /* handled */ }
  drawerVisible.value = true
}

function openApproveModal(id: number) {
  currentDetailId.value = id
  actionType.value = 'approve'
  actionForm.comment = ''
  actionModalVisible.value = true
}

function openRejectModal(id: number) {
  currentDetailId.value = id
  actionType.value = 'reject'
  actionForm.comment = ''
  actionModalVisible.value = true
}

async function handleActionSubmit() {
  try {
    if (actionType.value === 'reject' && !actionForm.comment) {
      message.warning('请输入拒绝原因')
      return
    }
    submitLoading.value = true
    if (actionType.value === 'approve') {
      await approveTask(currentDetailId.value, { comment: actionForm.comment })
      message.success('审批通过')
    } else {
      await rejectTask(currentDetailId.value, { comment: actionForm.comment })
      message.success('已拒绝')
    }
    actionModalVisible.value = false
    drawerVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitLoading.value = false }
}

onMounted(() => { fetchData(); fetchTypeOptions() })
</script>

<style scoped>.page-container { padding: 0; }</style>
