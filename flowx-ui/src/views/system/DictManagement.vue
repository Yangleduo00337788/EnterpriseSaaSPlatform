<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="字典名称">
          <n-input v-model:value="queryParams.dictName" placeholder="请输入字典名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="字典类型">
          <n-input v-model:value="queryParams.dictType" placeholder="请输入字典类型" clearable style="width: 180px;" />
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
        <n-button type="primary" @click="handleAdd">新增字典</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: DictVO) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 500px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80">
        <n-form-item label="字典名称" path="dictName">
          <n-input v-model:value="formData.dictName" placeholder="请输入字典名称" />
        </n-form-item>
        <n-form-item label="字典类型" path="dictType">
          <n-input v-model:value="formData.dictType" placeholder="请输入字典类型" :disabled="!!formData.id" />
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

    <!-- Dict Data Drawer -->
    <n-drawer v-model:show="drawerVisible" :width="700" placement="right">
      <n-drawer-content :title="`字典数据 - ${currentDictType}`">
        <div style="margin-bottom: 12px;">
          <n-button type="primary" size="small" @click="handleAddData">新增数据</n-button>
        </div>
        <n-data-table :columns="dataColumns" :data="dictDataList" :loading="dataLoading" size="small" />
      </n-drawer-content>
    </n-drawer>

    <n-modal v-model:show="dataModalVisible" title="字典数据" preset="card" style="width: 500px;" :segmented="{ content: true, footer: true }">
      <n-form ref="dataFormRef" :model="dataFormData" :rules="dataFormRules" label-placement="left" label-width="80">
        <n-form-item label="字典标签" path="dictLabel">
          <n-input v-model:value="dataFormData.dictLabel" placeholder="请输入字典标签" />
        </n-form-item>
        <n-form-item label="字典值" path="dictValue">
          <n-input v-model:value="dataFormData.dictValue" placeholder="请输入字典值" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="dataFormData.orderNum" :min="0" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-radio-group v-model:value="dataFormData.status">
            <n-radio value="0">正常</n-radio>
            <n-radio value="1">停用</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="dataModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleDataSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules, PaginationProps } from 'naive-ui'
import { getDictTypeList, createDictType, updateDictType, deleteDictType, getDictDataByType, createDictData, updateDictData, deleteDictData } from '@/api/dict'

const message = useMessage()
const dialog = useDialog()

interface DictVO { id: number; dictName: string; dictType: string; status: string; remark: string; createTime: string }
interface DictDataVO { id: number; dictType: string; dictLabel: string; dictValue: string; orderNum: number; status: string }

const loading = ref(false); const dataLoading = ref(false); const submitLoading = ref(false)
const tableData = ref<DictVO[]>([]); const dictDataList = ref<DictDataVO[]>([])
const modalVisible = ref(false); const drawerVisible = ref(false); const dataModalVisible = ref(false)
const modalTitle = ref('新增字典'); const formRef = ref<FormInst | null>(null); const dataFormRef = ref<FormInst | null>(null)
const currentDictType = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 10, dictName: '', dictType: '', status: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const statusOptions = [{ label: '正常', value: '0' }, { label: '停用', value: '1' }]

const formData = reactive({ id: undefined as number | undefined, dictName: '', dictType: '', status: '0', remark: '' })
const formRules: FormRules = { dictName: { required: true, message: '请输入字典名称', trigger: 'blur' }, dictType: { required: true, message: '请输入字典类型', trigger: 'blur' } }

const dataFormData = reactive({ id: undefined as number | undefined, dictType: '', dictLabel: '', dictValue: '', orderNum: 0, status: '0' })
const dataFormRules: FormRules = { dictLabel: { required: true, message: '请输入字典标签', trigger: 'blur' }, dictValue: { required: true, message: '请输入字典值', trigger: 'blur' } }

const columns: DataTableColumns<DictVO> = [
  { title: '字典名称', key: 'dictName', width: 150 },
  { title: '字典类型', key: 'dictType', width: 180 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 200, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'info', size: 'small', onClick: () => handleViewData(row) }, { default: () => '数据' }),
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

const dataColumns: DataTableColumns<DictDataVO> = [
  { title: '字典标签', key: 'dictLabel', width: 120 },
  { title: '字典值', key: 'dictValue', width: 100 },
  { title: '排序', key: 'orderNum', width: 70 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '操作', key: 'actions', width: 120, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEditData(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDeleteData(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getDictTypeList(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.dictName = ''; queryParams.dictType = ''; queryParams.status = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }
function handleAdd() { modalTitle.value = '新增字典'; resetForm(); modalVisible.value = true }
function handleEdit(row: DictVO) { modalTitle.value = '编辑字典'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, dictName: '', dictType: '', status: '0', remark: '' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    formData.id ? await updateDictType(formData as any) : await createDictType(formData as any)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: DictVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除字典 "${row.dictName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteDictType(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

async function handleViewData(row: DictVO) {
  currentDictType.value = row.dictType
  dataFormData.dictType = row.dictType
  drawerVisible.value = true
  dataLoading.value = true
  try { const res = await getDictDataByType(row.dictType); dictDataList.value = res.data || [] } catch (e) { /* handled */ } finally { dataLoading.value = false }
}

function handleAddData() { Object.assign(dataFormData, { id: undefined, dictType: currentDictType.value, dictLabel: '', dictValue: '', orderNum: 0, status: '0' }); dataModalVisible.value = true }
function handleEditData(row: DictDataVO) { Object.assign(dataFormData, row); dataModalVisible.value = true }

async function handleDataSubmit() {
  try {
    await dataFormRef.value?.validate(); submitLoading.value = true
    dataFormData.id ? await updateDictData(dataFormData as any) : await createDictData(dataFormData as any)
    message.success(dataFormData.id ? '更新成功' : '创建成功'); dataModalVisible.value = false
    const res = await getDictDataByType(currentDictType.value); dictDataList.value = res.data || []
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDeleteData(row: DictDataVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除字典数据 "${row.dictLabel}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteDictData(String(row.id)); message.success('删除成功'); const res = await getDictDataByType(currentDictType.value); dictDataList.value = res.data || [] } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
