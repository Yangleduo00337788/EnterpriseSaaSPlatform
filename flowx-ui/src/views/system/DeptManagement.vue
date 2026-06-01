<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="部门名称">
          <n-input v-model:value="queryParams.deptName" placeholder="请输入部门名称" clearable style="width: 180px;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-select v-model:value="queryParams.status" placeholder="请选择" clearable style="width: 120px;" :options="statusOptions" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" @click="fetchData">搜索</n-button>
          <n-button style="margin-left: 8px;" @click="handleReset">重置</n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <n-card>
      <div class="table-toolbar">
        <n-space>
          <n-button type="primary" @click="handleAdd()">新增部门</n-button>
          <n-button @click="toggleExpand">{{ allExpanded ? '全部折叠' : '全部展开' }}</n-button>
        </n-space>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :row-key="(row: DeptVO) => row.id" :default-expand-all="allExpanded" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 550px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80">
        <n-form-item label="上级部门">
          <n-tree-select v-model:value="formData.parentId" placeholder="选择上级部门" :options="deptTreeOptions" clearable default-expand-all />
        </n-form-item>
        <n-form-item label="部门名称" path="deptName">
          <n-input v-model:value="formData.deptName" placeholder="请输入部门名称" />
        </n-form-item>
        <n-form-item label="负责人">
          <n-input v-model:value="formData.leader" placeholder="请输入负责人" />
        </n-form-item>
        <n-form-item label="联系电话">
          <n-input v-model:value="formData.phone" placeholder="请输入联系电话" />
        </n-form-item>
        <n-form-item label="邮箱">
          <n-input v-model:value="formData.email" placeholder="请输入邮箱" />
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
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { getDeptList, getDeptTree, createDept, updateDept, deleteDept } from '@/api/dept'

const message = useMessage()
const dialog = useDialog()

interface DeptVO {
  id: number; deptName: string; parentId: number; leader: string; phone: string; email: string; orderNum: number; status: string; children?: DeptVO[]
}

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<DeptVO[]>([])
const modalVisible = ref(false)
const modalTitle = ref('新增部门')
const formRef = ref<FormInst | null>(null)
const allExpanded = ref(true)
const deptTreeOptions = ref<any[]>([])

const queryParams = reactive({ deptName: '', status: undefined as string | undefined })
const statusOptions = [{ label: '正常', value: '0' }, { label: '停用', value: '1' }]

const formData = reactive({ id: undefined as number | undefined, deptName: '', parentId: undefined as number | undefined, leader: '', phone: '', email: '', orderNum: 0, status: '0' })
const formRules: FormRules = { deptName: { required: true, message: '请输入部门名称', trigger: 'blur' } }

const columns: DataTableColumns<DeptVO> = [
  { title: '部门名称', key: 'deptName', width: 200 },
  { title: '负责人', key: 'leader', width: 100 },
  { title: '联系电话', key: 'phone', width: 130 },
  { title: '邮箱', key: 'email', width: 180, ellipsis: { tooltip: true } },
  { title: '排序', key: 'orderNum', width: 70 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '操作', key: 'actions', width: 180, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleAdd(row) }, { default: () => '新增' }),
    h(NButton, { text: true, type: 'info', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

function buildTreeOptions(list: DeptVO[]): any[] {
  return list.map(item => ({ label: item.deptName, value: item.id, children: item.children ? buildTreeOptions(item.children) : undefined }))
}

async function fetchData() {
  loading.value = true
  try { const res = await getDeptList(); tableData.value = res.data || [] } catch (e) { /* handled */ } finally { loading.value = false }
}

async function fetchDeptTree() {
  try { const res = await getDeptTree(); deptTreeOptions.value = buildTreeOptions(res.data || []) } catch (e) { /* handled */ }
}

function handleReset() { queryParams.deptName = ''; queryParams.status = undefined; fetchData() }
function toggleExpand() { allExpanded.value = !allExpanded.value; fetchData() }

function handleAdd(parent?: DeptVO) {
  modalTitle.value = '新增部门'; resetForm()
  if (parent) formData.parentId = parent.id
  modalVisible.value = true
}

function handleEdit(row: DeptVO) { modalTitle.value = '编辑部门'; Object.assign(formData, row); modalVisible.value = true }
function resetForm() { Object.assign(formData, { id: undefined, deptName: '', parentId: undefined, leader: '', phone: '', email: '', orderNum: 0, status: '0' }) }

async function handleSubmit() {
  try {
    await formRef.value?.validate(); submitLoading.value = true
    formData.id ? await updateDept(formData as any) : await createDept(formData as any)
    message.success(formData.id ? '更新成功' : '创建成功')
    modalVisible.value = false; fetchData(); fetchDeptTree()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: DeptVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除部门 "${row.deptName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteDept(row.id); message.success('删除成功'); fetchData(); fetchDeptTree() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData(); fetchDeptTree() })
</script>

<style scoped>.page-container { padding: 0; }</style>
