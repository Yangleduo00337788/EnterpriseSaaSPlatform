<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="模板名称">
          <n-input v-model:value="queryParams.templateName" placeholder="请输入模板名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">搜索</n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-card>
      <div class="table-toolbar">
        <n-button type="primary" @click="handleAdd">新增模板</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: any) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 650px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="100">
        <n-form-item label="模板名称" path="templateName">
          <n-input v-model:value="formData.templateName" placeholder="请输入模板名称" />
        </n-form-item>
        <n-form-item label="模板编码" path="templateCode">
          <n-input v-model:value="formData.templateCode" placeholder="请输入模板编码" />
        </n-form-item>
        <n-form-item label="消息类型">
          <n-select v-model:value="formData.messageType" placeholder="请选择" :options="[{label:'站内信',value:'1'},{label:'邮件',value:'2'},{label:'短信',value:'3'}]" />
        </n-form-item>
        <n-form-item label="标题模板">
          <n-input v-model:value="formData.titleTemplate" placeholder="请输入标题模板，如：{userName}的审批通知" />
        </n-form-item>
        <n-form-item label="内容模板" path="contentTemplate">
          <n-input v-model:value="formData.contentTemplate" type="textarea" :rows="6" placeholder="请输入内容模板，支持 {变量名} 占位符" />
        </n-form-item>
        <n-form-item label="状态">
          <n-radio-group v-model:value="formData.status">
            <n-radio value="0">启用</n-radio>
            <n-radio value="1">停用</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="modalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules, PaginationProps } from 'naive-ui'
import { getMessageTemplates, createMessageTemplate, updateMessageTemplate, deleteMessageTemplate } from '@/api/message'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([])
const modalVisible = ref(false); const modalTitle = ref('新增模板'); const formRef = ref<FormInst | null>(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, templateName: '' })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

const formData = reactive({ id: undefined as number | undefined, templateName: '', templateCode: '', messageType: '1', titleTemplate: '', contentTemplate: '', status: '0' })
const formRules: FormRules = { templateName: { required: true, message: '请输入模板名称', trigger: 'blur' }, templateCode: { required: true, message: '请输入模板编码', trigger: 'blur' }, contentTemplate: { required: true, message: '请输入内容模板', trigger: 'blur' } }

const columns: DataTableColumns<any> = [
  { title: '模板名称', key: 'templateName', width: 180 },
  { title: '模板编码', key: 'templateCode', width: 150 },
  { title: '消息类型', key: 'messageType', width: 100, render: (row) => h(NTag, { size: 'small' }, { default: () => ({ '1': '站内信', '2': '邮件', '3': '短信' } as any)[row.messageType] || '未知' }) },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '启用' : '停用' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 150, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getMessageTemplates(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.templateName = ''; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }
function handleAdd() { modalTitle.value = '新增模板'; resetForm(); modalVisible.value = true }
function handleEdit(row: any) { modalTitle.value = '编辑模板'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, templateName: '', templateCode: '', messageType: '1', titleTemplate: '', contentTemplate: '', status: '0' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    formData.id ? await updateMessageTemplate(formData) : await createMessageTemplate(formData)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: any) {
  dialog.warning({ title: '确认删除', content: `确定要删除模板 "${row.templateName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteMessageTemplate(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
