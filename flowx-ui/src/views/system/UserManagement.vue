<template>
  <div class="page-container">
    <!-- Search Bar -->
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams" label-placement="left">
        <n-form-item label="用户名">
          <n-input v-model:value="queryParams.username" placeholder="请输入用户名" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="手机号">
          <n-input v-model:value="queryParams.phone" placeholder="请输入手机号" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-select v-model:value="queryParams.status" placeholder="请选择状态" clearable style="width: 140px;" :options="statusOptions" />
        </n-form-item>
        <n-form-item label="部门">
          <n-tree-select v-model:value="queryParams.deptId" placeholder="请选择部门" clearable style="width: 180px;" :options="deptTree" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="handleSearch">
            <template #icon><n-icon><SearchOutlined /></n-icon></template>
            搜索
          </n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <!-- Toolbar & Table -->
    <n-card>
      <div class="table-toolbar">
        <n-space>
          <n-button type="primary" @click="handleAdd">
            <template #icon><n-icon><PlusOutlined /></n-icon></template>
            新增用户
          </n-button>
          <n-button type="info" @click="handleExport">
            <template #icon><n-icon><DownloadOutlined /></n-icon></template>
            导出
          </n-button>
        </n-space>
      </div>
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: UserVO) => row.id"
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
        @update:checked-row-keys="handleSelectionChange"
      />
    </n-card>

    <!-- Create/Edit Modal -->
    <n-modal
      v-model:show="modalVisible"
      :title="modalTitle"
      preset="card"
      style="width: 600px;"
      :segmented="{ content: true, footer: true }"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="80"
      >
        <n-grid :cols="2" :x-gap="12">
          <n-gi>
            <n-form-item label="用户名" path="username">
              <n-input v-model:value="formData.username" placeholder="请输入用户名" :disabled="!!formData.id" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="昵称" path="nickname">
              <n-input v-model:value="formData.nickname" placeholder="请输入昵称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="手机号" path="phone">
              <n-input v-model:value="formData.phone" placeholder="请输入手机号" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="邮箱" path="email">
              <n-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="性别" path="sex">
              <n-radio-group v-model:value="formData.sex">
                <n-radio :value="0">男</n-radio>
                <n-radio :value="1">女</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="状态" path="status">
              <n-radio-group v-model:value="formData.status">
                <n-radio value="0">正常</n-radio>
                <n-radio value="1">停用</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="部门" path="deptId">
              <n-tree-select v-model:value="formData.deptId" placeholder="请选择部门" :options="deptTree" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="岗位" path="positionId">
              <n-select v-model:value="formData.positionId" placeholder="请选择岗位" :options="positionOptions" />
            </n-form-item>
          </n-gi>
          <n-gi :span="2">
            <n-form-item label="密码" path="password" v-if="!formData.id">
              <n-input v-model:value="formData.password" type="password" placeholder="请输入密码" show-password-on="click" />
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

    <!-- Assign Roles Modal -->
    <n-modal v-model:show="roleModalVisible" title="分配角色" preset="card" style="width: 500px;">
      <n-transfer
        v-model:value="selectedRoleIds"
        :options="roleOptions"
        source-title="可选角色"
        target-title="已选角色"
      />
      <template #footer>
        <n-space justify="end">
          <n-button @click="roleModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleAssignRoles">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace, NIcon } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules, PaginationProps } from 'naive-ui'
import { getUserList, createUser, updateUser, deleteUser, resetPassword, assignRoles } from '@/api/user'
import { getDeptTree } from '@/api/dept'
import { getPositionOptions } from '@/api/position'
import { getRoleOptions } from '@/api/role'

const message = useMessage()
const dialog = useDialog()

interface UserVO {
  id: number
  username: string
  nickname: string
  deptName: string
  phone: string
  email: string
  sex: number
  status: string
  createTime: string
  roles?: string[]
}

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<UserVO[]>([])
const deptTree = ref<any[]>([])
const positionOptions = ref<any[]>([])
const roleOptions = ref<any[]>([])
const modalVisible = ref(false)
const roleModalVisible = ref(false)
const modalTitle = ref('新增用户')
const formRef = ref<FormInst | null>(null)
const selectedRoleIds = ref<string[]>([])
const currentUserId = ref(0)
const selectedIds = ref<number[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  status: undefined as string | undefined,
  deptId: undefined as number | undefined,
})

const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  prefix: ({ itemCount }: { itemCount: number | undefined }) => `共 ${itemCount ?? 0} 条`,
})

const statusOptions = [
  { label: '正常', value: '0' },
  { label: '停用', value: '1' },
]

const formData = reactive({
  id: undefined as number | undefined,
  username: '',
  nickname: '',
  password: '',
  email: '',
  phone: '',
  sex: 0,
  status: '0',
  deptId: undefined as number | undefined,
  positionId: undefined as number | undefined,
  remark: '',
})

const formRules: FormRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  nickname: { required: true, message: '请输入昵称', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
}

const columns: DataTableColumns<UserVO> = [
  { type: 'selection' },
  { title: '用户名', key: 'username', width: 120 },
  { title: '昵称', key: 'nickname', width: 120 },
  { title: '部门', key: 'deptName', width: 120 },
  { title: '手机号', key: 'phone', width: 130 },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render(row) {
      return h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' })
    },
  },
  { title: '创建时间', key: 'createTime', width: 160 },
  {
    title: '操作',
    key: 'actions',
    width: 280,
    render(row) {
      return h(NSpace, { size: 4 }, {
        default: () => [
          h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
          h(NButton, { text: true, type: 'info', size: 'small', onClick: () => handleAssignRole(row) }, { default: () => '分配角色' }),
          h(NButton, { text: true, type: 'warning', size: 'small', onClick: () => handleResetPwd(row) }, { default: () => '重置密码' }),
          h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
        ],
      })
    },
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    tableData.value = res.data.records || []
    pagination.itemCount = res.data.total || 0
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

async function fetchDeptTree() {
  try {
    const res = await getDeptTree()
    deptTree.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function fetchPositionOptions() {
  try {
    const res = await getPositionOptions()
    positionOptions.value = (res.data || []).map((p: any) => ({ label: p.positionName, value: p.id }))
  } catch (e) { /* ignore */ }
}

async function fetchRoleOptions() {
  try {
    const res = await getRoleOptions()
    roleOptions.value = (res.data || []).map((r: any) => ({ label: r.roleName, value: r.id }))
  } catch (e) { /* ignore */ }
}

function handleSearch() {
  queryParams.pageNum = 1
  pagination.page = 1
  fetchData()
}

function handleReset() {
  queryParams.username = ''
  queryParams.phone = ''
  queryParams.status = undefined
  queryParams.deptId = undefined
  handleSearch()
}

function handlePageChange(page: number) {
  queryParams.pageNum = page
  pagination.page = page
  fetchData()
}

function handlePageSizeChange(pageSize: number) {
  queryParams.pageSize = pageSize
  pagination.pageSize = pageSize
  queryParams.pageNum = 1
  pagination.page = 1
  fetchData()
}

function handleSelectionChange(keys: number[]) {
  selectedIds.value = keys
}

function handleAdd() {
  modalTitle.value = '新增用户'
  resetForm()
  modalVisible.value = true
}

function handleEdit(row: UserVO) {
  modalTitle.value = '编辑用户'
  resetForm()
  Object.assign(formData, { ...row, password: '' })
  modalVisible.value = true
}

function resetForm() {
  formData.id = undefined
  formData.username = ''
  formData.nickname = ''
  formData.password = ''
  formData.email = ''
  formData.phone = ''
  formData.sex = 0
  formData.status = '0'
  formData.deptId = undefined
  formData.positionId = undefined
  formData.remark = ''
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    if (formData.id) {
      await updateUser(formData as any)
      message.success('更新成功')
    } else {
      await createUser(formData as any)
      message.success('创建成功')
    }
    modalVisible.value = false
    fetchData()
  } catch (e: any) {
    if (e?.message) message.error(e.message)
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(row: UserVO) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除用户 "${row.username}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteUser(String(row.id))
        message.success('删除成功')
        fetchData()
      } catch (e) { /* handled */ }
    },
  })
}

function handleResetPwd(row: UserVO) {
  dialog.warning({
    title: '重置密码',
    content: `确定要重置用户 "${row.username}" 的密码吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await resetPassword(row.id, '123456')
        message.success('密码已重置为 123456')
      } catch (e) { /* handled */ }
    },
  })
}

function handleAssignRole(row: UserVO) {
  currentUserId.value = row.id
  selectedRoleIds.value = (row.roles || []).map(String)
  roleModalVisible.value = true
}

async function handleAssignRoles() {
  try {
    submitLoading.value = true
    await assignRoles(currentUserId.value, selectedRoleIds.value.map(Number))
    message.success('角色分配成功')
    roleModalVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally {
    submitLoading.value = false
  }
}

function handleExport() {
  message.info('导出功能开发中')
}

onMounted(() => {
  fetchData()
  fetchDeptTree()
  fetchPositionOptions()
  fetchRoleOptions()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
</style>
