<template>
  <div class="page-container">
    <n-card>
      <div class="table-toolbar">
        <n-button type="primary" @click="handleAdd">新增套餐</n-button>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :row-key="(row: any) => row.id" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 650px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" label-placement="left" label-width="80">
        <n-form-item label="套餐名称">
          <n-input v-model:value="formData.packageName" placeholder="请输入套餐名称" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="formData.remark" type="textarea" placeholder="请输入备注" />
        </n-form-item>
        <n-form-item label="菜单权限">
          <n-tree v-model:checked-keys="formData.menuIds" :data="menuTree" checkable cascade :selectable="false" default-expand-all style="max-height: 300px; overflow: auto;" />
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
import type { DataTableColumns, FormInst } from 'naive-ui'
import request from '@/utils/request'
import { getMenuTree } from '@/api/menu'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([])
const modalVisible = ref(false); const modalTitle = ref('新增套餐'); const formRef = ref<FormInst | null>(null)
const menuTree = ref<any[]>([])

const formData = reactive({ id: undefined as number | undefined, packageName: '', remark: '', menuIds: [] as number[] })

const columns: DataTableColumns<any> = [
  { title: '套餐名称', key: 'packageName', width: 200 },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '创建时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'actions', width: 150, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

async function fetchData() {
  loading.value = true
  try { const res = await request.get<any, any>('/system/tenant-package/list'); tableData.value = res.data || [] } catch (e) { /* handled */ } finally { loading.value = false }
}

async function fetchMenuTree() {
  try { const res = await getMenuTree(); menuTree.value = res.data || [] } catch (e) { /* handled */ }
}

function handleAdd() { modalTitle.value = '新增套餐'; Object.assign(formData, { id: undefined, packageName: '', remark: '', menuIds: [] }); modalVisible.value = true }
function handleEdit(row: any) { modalTitle.value = '编辑套餐'; Object.assign(formData, { ...row, menuIds: row.menuIds || [] }); modalVisible.value = true }

async function handleSubmit() {
  try {
    submitLoading.value = true
    formData.id ? await request.put('/system/tenant-package', formData) : await request.post('/system/tenant-package', formData)
    message.success(formData.id ? '更新成功' : '创建成功'); modalVisible.value = false; fetchData()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: any) {
  dialog.warning({ title: '确认删除', content: `确定要删除套餐 "${row.packageName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await request.delete(`/system/tenant-package/${row.id}`); message.success('删除成功'); fetchData() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData(); fetchMenuTree() })
</script>

<style scoped>.page-container { padding: 0; }</style>
