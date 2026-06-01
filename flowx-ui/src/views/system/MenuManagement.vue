<template>
  <div class="page-container">
    <n-card style="margin-bottom: 16px;">
      <n-form inline :model="queryParams">
        <n-form-item label="菜单名称">
          <n-input v-model:value="queryParams.menuName" placeholder="请输入菜单名称" clearable style="width: 180px;" />
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
          <n-button type="primary" @click="handleAdd">新增菜单</n-button>
          <n-button @click="toggleExpand">{{ allExpanded ? '全部折叠' : '全部展开' }}</n-button>
        </n-space>
      </div>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :row-key="(row: MenuVO) => row.id" :default-expand-all="allExpanded" />
    </n-card>

    <n-modal v-model:show="modalVisible" :title="modalTitle" preset="card" style="width: 650px;" :segmented="{ content: true, footer: true }">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80">
        <n-grid :cols="2" :x-gap="12">
          <n-gi :span="2">
            <n-form-item label="上级菜单">
              <n-tree-select v-model:value="formData.parentId" placeholder="选择上级菜单" :options="menuTreeOptions" clearable default-expand-all />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="菜单类型" path="menuType">
              <n-radio-group v-model:value="formData.menuType">
                <n-radio-button value="M">目录</n-radio-button>
                <n-radio-button value="C">菜单</n-radio-button>
                <n-radio-button value="F">按钮</n-radio-button>
              </n-radio-group>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="菜单名称" path="menuName">
              <n-input v-model:value="formData.menuName" placeholder="请输入菜单名称" />
            </n-form-item>
          </n-gi>
          <n-gi v-if="formData.menuType !== 'F'">
            <n-form-item label="路由地址" path="path">
              <n-input v-model:value="formData.path" placeholder="请输入路由地址" />
            </n-form-item>
          </n-gi>
          <n-gi v-if="formData.menuType === 'C'">
            <n-form-item label="组件路径" path="component">
              <n-input v-model:value="formData.component" placeholder="请输入组件路径" />
            </n-form-item>
          </n-gi>
          <n-gi v-if="formData.menuType !== 'M'">
            <n-form-item label="权限标识" path="perms">
              <n-input v-model:value="formData.perms" placeholder="请输入权限标识" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="显示排序" path="orderNum">
              <n-input-number v-model:value="formData.orderNum" :min="0" style="width: 100%;" />
            </n-form-item>
          </n-gi>
          <n-gi v-if="formData.menuType !== 'F'">
            <n-form-item label="图标">
              <n-input v-model:value="formData.icon" placeholder="请输入图标名称" />
            </n-form-item>
          </n-gi>
          <n-gi v-if="formData.menuType !== 'F'">
            <n-form-item label="显示状态">
              <n-radio-group v-model:value="formData.visible">
                <n-radio value="0">显示</n-radio>
                <n-radio value="1">隐藏</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="菜单状态">
              <n-radio-group v-model:value="formData.status">
                <n-radio value="0">正常</n-radio>
                <n-radio value="1">停用</n-radio>
              </n-radio-group>
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
import { ref, reactive, onMounted, h, computed } from 'vue'
import { useMessage, useDialog, NButton, NTag, NSpace, NIcon } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { getMenuList, getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'

const message = useMessage()
const dialog = useDialog()

interface MenuVO {
  id: number
  menuName: string
  parentId: number
  path: string
  component: string
  menuType: string
  visible: string
  status: string
  perms: string
  icon: string
  orderNum: number
  children?: MenuVO[]
}

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<MenuVO[]>([])
const modalVisible = ref(false)
const modalTitle = ref('新增菜单')
const formRef = ref<FormInst | null>(null)
const allExpanded = ref(true)
const menuTreeOptions = ref<any[]>([])

const queryParams = reactive({ menuName: '', status: undefined as string | undefined })
const statusOptions = [{ label: '正常', value: '0' }, { label: '停用', value: '1' }]

const formData = reactive({
  id: undefined as number | undefined,
  menuName: '',
  parentId: undefined as number | undefined,
  orderNum: 0,
  path: '',
  component: '',
  menuType: 'M',
  visible: '0',
  status: '0',
  perms: '',
  icon: '',
  remark: '',
})

const formRules: FormRules = {
  menuName: { required: true, message: '请输入菜单名称', trigger: 'blur' },
  menuType: { required: true, message: '请选择菜单类型', trigger: 'change' },
}

const columns: DataTableColumns<MenuVO> = [
  { title: '菜单名称', key: 'menuName', width: 200 },
  { title: '图标', key: 'icon', width: 80 },
  { title: '排序', key: 'orderNum', width: 70 },
  { title: '权限标识', key: 'perms', width: 180, ellipsis: { tooltip: true } },
  { title: '组件路径', key: 'component', width: 180, ellipsis: { tooltip: true } },
  { title: '状态', key: 'status', width: 80, render: (row) => h(NTag, { type: row.status === '0' ? 'success' : 'error', size: 'small' }, { default: () => row.status === '0' ? '正常' : '停用' }) },
  { title: '显示', key: 'visible', width: 80, render: (row) => h(NTag, { type: row.visible === '0' ? 'success' : 'warning', size: 'small' }, { default: () => row.visible === '0' ? '显示' : '隐藏' }) },
  { title: '操作', key: 'actions', width: 180, render: (row) => h(NSpace, { size: 4 }, { default: () => [
    h(NButton, { text: true, type: 'primary', size: 'small', onClick: () => handleAdd(row) }, { default: () => '新增' }),
    h(NButton, { text: true, type: 'info', size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
    h(NButton, { text: true, type: 'error', size: 'small', onClick: () => handleDelete(row) }, { default: () => '删除' }),
  ] }) },
]

function buildTreeOptions(list: MenuVO[]): any[] {
  return list.map(item => ({
    label: item.menuName,
    value: item.id,
    children: item.children ? buildTreeOptions(item.children) : undefined,
  }))
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res.data || []
  } catch (e) { /* handled */ } finally { loading.value = false }
}

async function fetchMenuTree() {
  try {
    const res = await getMenuTree()
    menuTreeOptions.value = buildTreeOptions(res.data || [])
  } catch (e) { /* handled */ }
}

function handleReset() { queryParams.menuName = ''; queryParams.status = undefined; fetchData() }
function toggleExpand() { allExpanded.value = !allExpanded.value; fetchData() }

function handleAdd(parent?: MenuVO) {
  modalTitle.value = '新增菜单'
  resetForm()
  if (parent) formData.parentId = parent.id
  modalVisible.value = true
}

function handleEdit(row: MenuVO) {
  modalTitle.value = '编辑菜单'
  Object.assign(formData, row)
  modalVisible.value = true
}

function resetForm() {
  Object.assign(formData, { id: undefined, menuName: '', parentId: undefined, orderNum: 0, path: '', component: '', menuType: 'M', visible: '0', status: '0', perms: '', icon: '', remark: '' })
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    formData.id ? await updateMenu(formData as any) : await createMenu(formData as any)
    message.success(formData.id ? '更新成功' : '创建成功')
    modalVisible.value = false
    fetchData()
    fetchMenuTree()
  } catch (e: any) { if (e?.message) message.error(e.message) } finally { submitLoading.value = false }
}

function handleDelete(row: MenuVO) {
  dialog.warning({ title: '确认删除', content: `确定要删除菜单 "${row.menuName}" 吗？`, positiveText: '确定', negativeText: '取消', onPositiveClick: async () => {
    try { await deleteMenu(row.id); message.success('删除成功'); fetchData(); fetchMenuTree() } catch (e) { /* handled */ }
  }})
}

onMounted(() => { fetchData(); fetchMenuTree() })
</script>

<style scoped>.page-container { padding: 0; }</style>
