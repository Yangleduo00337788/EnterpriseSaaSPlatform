<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="参数名称">
          <n-input v-model:value="queryParams.configName" placeholder="请输入参数名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="参数键名">
          <n-input v-model:value="queryParams.configKey" placeholder="请输入参数键名" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">搜索</n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-card>
      <div class="table-toolbar">
        <n-button type="primary" @click="handleAdd">新增配置</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: ConfigVO) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 550px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80">
        <n-form-item label="参数名称" path="configName">
          <n-input v-model:value="formData.configName" placeholder="请输入参数名称" />
        </n-form-item>
        <n-form-item label="参数键名" path="configKey">
          <n-input v-model:value="formData.configKey" placeholder="请输入参数键名" />
        </n-form-item>
        <n-form-item label="参数键值" path="configValue">
          <n-input v-model:value="formData.configValue" placeholder="请输入参数键值" />
        </n-form-item>
        <n-form-item label="系统内置">
          <n-radio-group v-model:value="formData.configType">
            <n-radio value="Y">是</n-radio>
            <n-radio value="N">否</n-radio>
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
import request from '@/utils/request'

const message = useMessage()
const dialog = useDialog()

interface ConfigVO { id: number; configName: string; configKey: string; configValue: string; configType: string; remark: string; createTime: string }

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<ConfigVO[]>([])
const modalVisible = ref(false); const modalTitle = ref('新增配置'); const formRef = ref<FormInst | null>(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, configName: '', configKey: '' })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

const formData = reactive({ id: undefined as number | undefined, configName: '', configKey: '', configValue: '', configType: 'Y', remark: '' })
const formRules: FormRules = { configName: { required: true, message: '请输入参数名称', trigger: 'blur' }, configKey: { required: true, message: '请输入参数键名', trigger: 'blur' }, configValue: { required: true, message: '请输入参数键值', trigger: 'blur' } }

const columns: DataTableColumns<ConfigVO> = [
  { title: '参数名称', key: 'configName', width: 180 },
  { title: '参数键名', key: 'configKey', width: 200 },
  { title: '参数键值', key: 'configValue', width: 150 },
  { title: '系统内置', key: 'configType', width: 100, render: (row) => h(NTag, { type: row.configType === 'Y' ? 'success' : 'info', size: 'small' }, { default: () => row.configType === 'Y' ? '是' : '否' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 150, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await request.get<any, any>('/system/config/list', { params: queryParams }); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.configName = ''; queryParams.configKey = ''; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }
function handleAdd() { modalTitle.value = '新增配置'; resetForm(); modalVisible.value = true }
function handleEdit(row: ConfigVO) { modalTitle.value = '编辑配置'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, configName: '', configKey: '', configValue: '', configType: 'Y', remark: '' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    const url = formData.id ? '/system/config' : '/system/config'
    formData.id ? await request.put(url, formData) : await request.post(url, formData)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: ConfigVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除配置 "${row.configName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await request.delete(`/system/config/${row.id}`); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
