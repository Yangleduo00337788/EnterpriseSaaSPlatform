<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="流程名称">
          <n-input v-model:value="queryParams.name" placeholder="请输入流程名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="流程标识">
          <n-input v-model:value="queryParams.key" placeholder="请输入流程标识" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">搜索</n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-card>
      <div class="table-toolbar">
        <n-space>
          <n-upload :show-file-list="false" :custom-request="handleDeploy" accept=".bpmn,.bpmn20.xml,.zip">
            <n-button type="primary">部署流程</n-button>
          </n-upload>
        </n-space>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: any) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="xmlModalVisible" title="流程定义 XML" preset="card" style="width: 80%;">
      <n-input v-model:value="xmlContent" type="textarea" :rows="20" readonly />
    </n-modal>

    <n-modal v-model:show="imageModalVisible" title="流程图" preset="card" style="width: 80%;">
      <div style="text-align: center;">
        <img v-if="imageUrl" :src="imageUrl" style="max-width: 100%;" alt="流程图" />
      </div>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, PaginationProps, UploadCustomRequestOptions } from 'naive-ui'
import { getDefinitions, deployWorkflow, deleteDeployment, getDefinitionXml, getProcessImage } from '@/api/workflow'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false); const tableData = ref<any[]>([])
const xmlModalVisible = ref(false); const xmlContent = ref('')
const imageModalVisible = ref(false); const imageUrl = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, name: '', key: '', category: '' })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

const columns: DataTableColumns<any> = [
  { title: '流程名称', key: 'name', width: 200 },
  { title: '流程标识', key: 'key', width: 150 },
  { title: '版本', key: 'version', width: 80 },
  { title: '部署时间', key: 'deploymentTime', width: 160 },
  { title: '状态', key: 'suspended', width: 80, render: (row) => h(NTag, { type: row.suspended ? 'warning' : 'success', size: 'small' }, { default: () => row.suspended ? '已挂起' : '激活' }) },
  { title: '操作', key: 'actions', width: 250, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'info', size: 'small', onClick: () => handleViewXml(row) }, { default: () => '查看XML' }),
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleViewImage(row) }, { default: () => '流程图' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getDefinitions(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.name = ''; queryParams.key = ''; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }

async function handleDeploy({ file }: UploadCustomRequestOptions) {
  if (!file.file) return
  const formData = new FormData()
  formData.append('file', file.file)
  try {
    await deployWorkflow(formData)
    message.success('部署成功')
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleViewXml(row: any) {
  try {
    const res = await getDefinitionXml(row.id)
    xmlContent.value = res.data || ''
    xmlModalVisible.value = true
  } catch (e) { /* handled */ }
}

async function handleViewImage(row: any) {
  try {
    const res = await getProcessImage(row.id)
    const blob = new Blob([res.data], { type: 'image/png' })
    imageUrl.value = URL.createObjectURL(blob)
    imageModalVisible.value = true
  } catch (e) { /* handled */ }
}

function handleDelete(row: any) {
  dialog.warning({ title: '确认删除', content: `确定要删除流程 "${row.name}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteDeployment(row.deploymentId); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
