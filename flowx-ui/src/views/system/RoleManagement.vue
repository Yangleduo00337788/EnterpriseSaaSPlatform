<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="角色名称">
          <n-input v-model:value="queryParams.roleName" placeholder="请输入角色名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="权限标识">
          <n-input v-model:value="queryParams.roleKey" placeholder="请输入权限标识" clearable style="width: 180px;" />
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
        <n-button type="primary" @click="handleAdd">新增角色</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination" :row-key="(row: RoleVO) => row.id" @update:page="handlePageChange" @update:page-size="handlePageSizeChange" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 550px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80">
        <n-form-item label="角色名称" path="roleName">
          <n-input v-model:value="formData.roleName" placeholder="请输入角色名称" />
        </n-form-item>
        <n-form-item label="权限标识" path="roleKey">
          <n-input v-model:value="formData.roleKey" placeholder="请输入权限标识" />
        </n-form-item>
        <n-form-item label="显示顺序" path="orderNum">
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

    <n-modal v-model:show="menuModalVisible" title="分配菜单权限" preset="card" style="width: 500px;">
      <n-tree
        v-model:checked-keys="selectedMenuIds"
        :data="menuTree"
        checkable
        cascade
        :selectable="false"
        default-expand-all
      />
      <template #footer>
        <n-space justify="end">
          <n-button @click="menuModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleAssignMenus">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules, PaginationProps } from 'naive-ui'
import { getRoleList, createRole, updateRole, deleteRole, assignMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'

const message = useMessage()
const dialog = useDialog()

interface RoleVO {
  id: number
  roleName: string
  roleKey: string
  orderNum: number
  status: string
  remark: string
  createTime: string
}

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<RoleVO[]>([])
const modalVisible = ref(false)
const menuModalVisible = ref(false)
const modalTitle = ref('新增角色')
const formRef = ref<FormInst | null>(null)
const currentRoleId = ref(0)
const selectedMenuIds = ref<number[]>([])
const menuTree = ref<any[]>([])

const queryParams = reactive({ pageNum: 1, pageSize: 10, roleName: '', roleKey: '', status: undefined as string | undefined })
const pagination = reactive<PaginationProps>({ page: 1, pageSize: 10, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })
const statusOptions = [{ label: '正常', value: '0' }, { label: '停用', value: '1' }]

const formData = reactive({ id: undefined as number | undefined, roleName: '', roleKey: '', orderNum: 0, status: '0', remark: '' })
const formRules: FormRules = {
  roleName: { required: true, message: '请输入角色名称', trigger: 'blur' },
  roleKey: { required: true, message: '请输入权限标识', trigger: 'blur' },
}

const columns: DataTableColumns<RoleVO> = [
  { title: '角色名称', key: 'roleName', width: 150 },
  { title: '权限标识', key: 'roleKey', width: 150 },
  { title: '排序', key: 'orderNum', width: 80 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 240, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'info', size: 'small', onClick: () => handleAssignMenu(row) }, { default: () => '分配菜单' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    tableData.value = res.data.records || []
    pagination.itemCount = res.data.total || 0
  } catch (e) { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.pageNum = 1; pagination.page = 1; fetchData() }
function handleReset() { queryParams.roleName = ''; queryParams.roleKey = ''; queryParams.status = undefined; handleSearch() }
function handlePageChange(page: number) { queryParams.pageNum = page; pagination.page = page; fetchData() }
function handlePageSizeChange(size: number) { queryParams.pageSize = size; pagination.pageSize = size; handleSearch() }

function handleAdd() { modalTitle.value = '新增角色'; resetForm(); modalVisible.value = true }
function handleEdit(row: RoleVO) { modalTitle.value = '编辑角色'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { formData.id = undefined; formData.roleName = ''; formData.roleKey = ''; formData.orderNum = 0; formData.status = '0'; formData.remark = '' }

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    formData.id ? await updateRole(formData as any) : await createRole(formData as any)
    message.success(formData.id ? '更新成功' : '创建成功')
    modalVisible.value = false
    fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: RoleVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除角色 "${row.roleName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteRole(String(row.id)); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

async function handleAssignMenu(row: RoleVO) {
  currentRoleId.value = row.id
  selectedMenuIds.value = []
  try {
    const res = await getMenuTree()
    menuTree.value = res.data || []
  } catch (e) { /* handled */ }
  menuModalVisible.value = true
}

async function handleAssignMenus() {
  try {
    submitLoading.value = true
    await assignMenus(currentRoleId.value, selectedMenuIds.value)
    message.success('菜单分配成功')
    menuModalVisible.value = false
  } catch (e) { /* handled */ } finally { submitLoading.value = false }
}

onMounted(() => { fetchData() })
</script>

<style scoped>.page-container { padding: 0; }</style>
