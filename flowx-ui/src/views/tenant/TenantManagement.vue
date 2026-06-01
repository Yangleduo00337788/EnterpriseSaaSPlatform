<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="租户名称">
          <n-input v-model:value="queryParams.tenantName" placeholder="请输入租户名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="联系人">
          <n-input v-model:value="queryParams.contactName" placeholder="请输入联系人" clearable style="width: 180px;" />
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
        <n-button type="primary" @click="handleAdd">新增租户</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: TenantVO) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 600px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="100">
        <n-grid :cols="2" :x-gap="12">
          <n-gi :span="2">
            <n-form-item label="租户名称" path="tenantName">
              <n-input v-model:value="formData.tenantName" placeholder="请输入租户名称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="联系人" path="contactName">
              <n-input v-model:value="formData.contactName" placeholder="请输入联系人" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="联系电话" path="contactPhone">
              <n-input v-model:value="formData.contactPhone" placeholder="请输入联系电话" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="联系邮箱">
              <n-input v-model:value="formData.contactEmail" placeholder="请输入联系邮箱" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="绑定域名">
              <n-input v-model:value="formData.domain" placeholder="请输入绑定域名" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="租户套餐">
              <n-select v-model:value="formData.packageId" placeholder="请选择套餐" :options="packageOptions" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="账号上限">
              <n-input-number v-model:value="formData.accountLimit" :min="1" style="width: 100%;" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="过期时间">
              <n-date-picker v-model:formatted-value="formData.expireTime" type="date" style="width: 100%;" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="状态">
              <n-radio-group v-model:value="formData.status">
                <n-radio value="0">正常</n-radio>
                <n-radio value="1">停用</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
          <n-gi :span="2">
            <n-form-item label="备注">
              <n-input v-model:value="formData.remark" type="textarea" placeholder="请输入备注" />
            </n-form-item>
          </n-gi>
        </n-grid>
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
import { getTenantList, createTenant, updateTenant, deleteTenant } from '@/api/tenant'

const message = useMessage()
const dialog = useDialog()

interface TenantVO { id: number; tenantName: string; contactName: string; contactPhone: string; contactEmail: string; domain: string; packageId: number; packageName: string; expireTime: string; accountLimit: number; status: string; createTime: string }

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<TenantVO[]>([])
const modalVisible = ref(false); const modalTitle = ref('新增租户'); const formRef = ref<FormInst | null>(null)
const packageOptions = ref<any[]>([])

const queryParams = reactive({ pageNum: 1, pageSize: 10, tenantName: '', contactName: '', status: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const statusOptions = [{ label: '正常', value: '0' }, { label: '停用', value: '1' }]

const formData = reactive({ id: undefined as number | undefined, tenantName: '', contactName: '', contactPhone: '', contactEmail: '', domain: '', packageId: undefined as number | undefined, expireTime: '', accountLimit: 100, status: '0', remark: '' })
const formRules: FormRules = { tenantName: { required: true, message: '请输入租户名称', trigger: 'blur' }, contactName: { required: true, message: '请输入联系人', trigger: 'blur' }, contactPhone: { required: true, message: '请输入联系电话', trigger: 'blur' } }

const columns: DataTableColumns<TenantVO> = [
  { title: '租户名称', key: 'tenantName', width: 180 },
  { title: '联系人', key: 'contactName', width: 100 },
  { title: '联系电话', key: 'contactPhone', width: 130 },
  { title: '套餐', key: 'packageName', width: 120 },
  { title: '账号上限', key: 'accountLimit', width: 90 },
  { title: '过期时间', key: 'expireTime', width: 120 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '操作', key: 'actions', width: 150, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await getTenantList(queryParams); tableData.value = res.data.records || []; pagination.itemCount = res.data.total || 0 } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.tenantName = ''; queryParams.contactName = ''; queryParams.status = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }
function handleAdd() { modalTitle.value = '新增租户'; resetForm(); modalVisible.value = true }
function handleEdit(row: TenantVO) { modalTitle.value = '编辑租户'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, tenantName: '', contactName: '', contactPhone: '', contactEmail: '', domain: '', packageId: undefined, expireTime: '', accountLimit: 100, status: '0', remark: '' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    formData.id ? await updateTenant(formData as any) : await createTenant(formData as any)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: TenantVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除租户 "${row.tenantName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteTenant(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
