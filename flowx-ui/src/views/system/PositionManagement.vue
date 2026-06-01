<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="岗位名称">
          <n-input v-model:value="queryParams.positionName" placeholder="请输入岗位名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="岗位编码">
          <n-input v-model:value="queryParams.positionCode" placeholder="请输入岗位编码" clearable style="width: 180px;" />
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
        <n-button type="primary" @click="handleAdd">新增岗位</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: PositionVO) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 500px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80">
        <n-form-item label="岗位名称" path="positionName">
          <n-input v-model:value="formData.positionName" placeholder="请输入岗位名称" />
        </n-form-item>
        <n-form-item label="岗位编码" path="positionCode">
          <n-input v-model:value="formData.positionCode" placeholder="请输入岗位编码" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="formData.orderNum" :min="0" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-radio-group v-model:value="formData.status">
            <n-radio value="0">正常</n-radio>
            <n-radio value="1">停用</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="formData.remark" type="textarea" placeholder="请输入备注" />
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
import { getPositionList, createPosition, updatePosition, deletePosition } from '@/api/position'

const message = useMessage()
const dialog = useDialog()

interface PositionVO { id: number; positionName: string; positionCode: string; orderNum: number; status: string; remark: string; createTime: string }

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<PositionVO[]>([])
const modalVisible = ref(false); const modalTitle = ref('新增岗位'); const formRef = ref<FormInst | null>(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, positionName: '', positionCode: '', status: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const statusOptions = [{ label: '正常', value: '0' }, { label: '停用', value: '1' }]

const formData = reactive({ id: undefined as number | undefined, positionName: '', positionCode: '', orderNum: 0, status: '0', remark: '' })
const formRules: FormRules = { positionName: { required: true, message: '请输入岗位名称', trigger: 'blur' }, positionCode: { required: true, message: '请输入岗位编码', trigger: 'blur' } }

const columns: DataTableColumns<PositionVO> = [
  { title: '岗位名称', key: 'positionName', width: 150 },
  { title: '岗位编码', key: 'positionCode', width: 150 },
  { title: '排序', key: 'orderNum', width: 80 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 150, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getPositionList(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.positionName = ''; queryParams.positionCode = ''; queryParams.status = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }
function handleAdd() { modalTitle.value = '新增岗位'; resetForm(); modalVisible.value = true }
function handleEdit(row: PositionVO) { modalTitle.value = '编辑岗位'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, positionName: '', positionCode: '', orderNum: 0, status: '0', remark: '' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    formData.id ? await updatePosition(formData as any) : await createPosition(formData as any)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: PositionVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除岗位 "${row.positionName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deletePosition(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
