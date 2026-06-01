<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="流程名称">
          <n-input v-model:value="queryParams.name" placeholder="请输入流程名称" clearable style="width: 180px;" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, PaginationProps } from 'naive-ui'
import { getInstances, suspendInstance, activateInstance, deleteInstance } from '@/api/workflow'

const message = useMessage()

const loading = ref(false); const tableData = ref<any[]>([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, name: '', key: '' })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

const columns: DataTableColumns<any> = [
  { title: '流程实例ID', key: 'id', width: 180, ellipsis: { tooltip: true } },
  { title: '流程名称', key: 'processDefinitionName', width: 180 },
  { title: '发起人', key: 'startUserId', width: 100 },
  { title: '状态', key: 'suspended', width: 80, render: (row) => h(NTag, { type: row.suspended ? 'warning' : 'success', size: 'small' }, { default: () => row.suspended ? '已挂起' : '运行中' }) },
  { title: '开始时间', key: 'startTime', width: 160 },
  { title: '结束时间', key: 'endTime', width: 160 },
  { title: '操作', key: 'actions', width: 200, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    row.suspended
      ? h(NButton, { text: true, type: 'success', size: 'small', onClick: () => handleActivate(row) }, { default: () => '激活' })
      : h(NButton, { text: true, type: 'warning', size: 'small', onClick: () => handleSuspend(row) }, { default: () => '挂起' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getInstances(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.name = ''; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }

async function handleSuspend(row: any) {
  try { await suspendInstance(row.id); message.success('挂起成功'); fetchData() } catch (e) { /* handled */ }
}

async function handleActivate(row: any) {
  try { await activateInstance(row.id); message.success('激活成功'); fetchData() } catch (e) { /* handled */ }
}

async function handleDelete(row: any) {
  try { await deleteInstance(row.id); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
