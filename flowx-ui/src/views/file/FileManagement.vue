<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="文件名">
          <n-input v-model:value="queryParams.fileName" placeholder="请输入文件名" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="文件类型">
          <n-select v-model:value="queryParams.fileType" placeholder="请选择" clearable style="width: 140px;" :options="fileTypeOptions" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">搜索</n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-card>
      <div class="table-toolbar">
        <n-upload :show-file-list="false" :custom-request="handleUpload" :on-finish="onUploadFinish">
          <n-button type="primary">上传文件</n-button>
        </n-upload>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: any) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, PaginationProps, UploadCustomRequestOptions } from 'naive-ui'
import { uploadFile, getFileList, deleteFile, downloadFile } from '@/api/file'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false); const tableData = ref<any[]>([])
const queryParams = reactive({ pageNum: 1, pageSize: 10, fileName: '', fileType: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const fileTypeOptions = [
  { label: '图片', value: 'image' },
  { label: '文档', value: 'document' },
  { label: '视频', value: 'video' },
  { label: '其他', value: 'other' },
]

function formatFileSize(bytes: number) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const columns: DataTableColumns<any> = [
  { title: '文件名', key: 'fileName', width: 250, ellipsis: { tooltip: true } },
  { title: '文件类型', key: 'fileType', width: 100 },
  { title: '文件大小', key: 'fileSize', width: 100, render: (row) => h('span', {}, formatFileSize(row.fileSize)) },
  { title: '上传人', key: 'uploadBy', width: 100 },
  { title: '上传时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 180, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleDownload(row) }, { default: () => '下载' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getFileList(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.fileName = ''; queryParams.fileType = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }

async function handleUpload({ file }: UploadCustomRequestOptions) {
  if (!file.file) return
  try {
    await uploadFile(file.file)
    message.success('上传成功')
    fetchData()
  } catch (e) { /* handled */ }
}

function onUploadFinish() { fetchData() }

async function handleDownload(row: any) {
  try {
    const res = await downloadFile(row.id)
    const blob = new Blob([res.data])
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = row.fileName
    a.click()
    URL.revokeObjectURL(url)
  } catch (e) { /* handled */ }
}

function handleDelete(row: any) {
  dialog.warning({ title: '确认删除', content: `确定要删除文件 "${row.fileName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteFile(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
