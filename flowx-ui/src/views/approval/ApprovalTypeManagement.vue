<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="类型名称">
          <n-input v-model:value="queryParams.typeName" placeholder="请输入类型名称" clearable style="width: 180px;" />
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
      <div class="table-toolbar">
        <n-button type="primary" @click="handleAdd">新增审批类型</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: any) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 600px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="100">
        <n-form-item label="类型名称" path="typeName">
          <n-input v-model:value="formData.typeName" placeholder="请输入类型名称" />
        </n-form-item>
        <n-form-item label="类型编码" path="typeCode">
          <n-input v-model:value="formData.typeCode" placeholder="请输入类型编码" />
        </n-form-item>
        <n-form-item label="所属分类">
          <n-select v-model:value="formData.category" placeholder="请选择分类" :options="categoryOptions" />
        </n-form-item>
        <n-form-item label="关联流程">
          <n-select v-model:value="formData.workflowKey" placeholder="请选择关联流程" :options="workflowOptions" clearable />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="formData.orderNum" :min="0" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-radio-group v-model:value="formData.status">
            <n-radio value="0">启用</n-radio>
            <n-radio value="1">停用</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="表单配置">
          <n-input v-model:value="formData.formConfig" type="textarea" placeholder="请输入表单JSON配置" :rows="6" />
        </n-form-item>
        <n-form-item label="说明">
          <n-input v-model:value="formData.description" type="textarea" placeholder="请输入说明" />
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
import { getApprovalTypes, createApprovalType, updateApprovalType, deleteApprovalType } from '@/api/approval'
import request from '@/utils/request'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([])
const modalVisible = ref(false); const modalTitle = ref('新增审批类型'); const formRef = ref<FormInst | null>(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, typeName: '', status: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const statusOptions = [{ label: '启用', value: '0' }, { label: '停用', value: '1' }]
const categoryOptions = [{ label: '人事类', value: 'HR' }, { label: '财务类', value: 'FINANCE' }, { label: '行政类', value: 'ADMIN' }, { label: '业务类', value: 'BUSINESS' }]
const workflowOptions = ref<any[]>([])

const formData = reactive({
  id: undefined as number | undefined, typeName: '', typeCode: '', category: '', workflowKey: '', orderNum: 0, status: '0', formConfig: '', description: '',
})
const formRules: FormRules = { typeName: { required: true, message: '请输入类型名称', trigger: 'blur' }, typeCode: { required: true, message: '请输入类型编码', trigger: 'blur' } }

const columns: DataTableColumns<any> = [
  { title: '类型名称', key: 'typeName', width: 160 },
  { title: '类型编码', key: 'typeCode', width: 150 },
  { title: '分类', key: 'category', width: 100 },
  { title: '关联流程', key: 'workflowKey', width: 150 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '启用' : '停用' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 150, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getApprovalTypes(); tableData.value = res.data || []; pagination.itemCount = tableData.value.length } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.typeName = ''; queryParams.status = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }
function handleAdd() { modalTitle.value = '新增审批类型'; resetForm(); modalVisible.value = true }
function handleEdit(row: any) { modalTitle.value = '编辑审批类型'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, typeName: '', typeCode: '', category: '', workflowKey: '', orderNum: 0, status: '0', formConfig: '', description: '' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    formData.id ? await updateApprovalType(formData) : await createApprovalType(formData)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: any) {
  dialog.warning({ title: '确认删除', content: `确定要删除审批类型 "${row.typeName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteApprovalType(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
