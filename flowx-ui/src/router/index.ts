import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '工作台', icon: 'DashboardOutlined' },
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'SettingOutlined' },
        children: [
          {
            path: 'users',
            name: 'UserManagement',
            component: () => import('@/views/system/UserManagement.vue'),
            meta: { title: '用户管理', icon: 'UserOutlined' },
          },
          {
            path: 'roles',
            name: 'RoleManagement',
            component: () => import('@/views/system/RoleManagement.vue'),
            meta: { title: '角色管理', icon: 'TeamOutlined' },
          },
          {
            path: 'menus',
            name: 'MenuManagement',
            component: () => import('@/views/system/MenuManagement.vue'),
            meta: { title: '菜单管理', icon: 'MenuOutlined' },
          },
          {
            path: 'depts',
            name: 'DeptManagement',
            component: () => import('@/views/system/DeptManagement.vue'),
            meta: { title: '部门管理', icon: 'ApartmentOutlined' },
          },
          {
            path: 'positions',
            name: 'PositionManagement',
            component: () => import('@/views/system/PositionManagement.vue'),
            meta: { title: '岗位管理', icon: 'IdcardOutlined' },
          },
          {
            path: 'dicts',
            name: 'DictManagement',
            component: () => import('@/views/system/DictManagement.vue'),
            meta: { title: '字典管理', icon: 'BookOutlined' },
          },
          {
            path: 'configs',
            name: 'ConfigManagement',
            component: () => import('@/views/system/ConfigManagement.vue'),
            meta: { title: '参数配置', icon: 'ControlOutlined' },
          },
        ],
      },
      {
        path: 'tenant',
        name: 'Tenant',
        meta: { title: '租户管理', icon: 'ShopOutlined' },
        children: [
          {
            path: 'list',
            name: 'TenantManagement',
            component: () => import('@/views/tenant/TenantManagement.vue'),
            meta: { title: '租户列表', icon: 'HomeOutlined' },
          },
          {
            path: 'packages',
            name: 'TenantPackageManagement',
            component: () => import('@/views/tenant/TenantPackageManagement.vue'),
            meta: { title: '租户套餐', icon: 'GiftOutlined' },
          },
        ],
      },
      {
        path: 'approval',
        name: 'Approval',
        meta: { title: '审批管理', icon: 'AuditOutlined' },
        children: [
          {
            path: 'types',
            name: 'ApprovalTypeManagement',
            component: () => import('@/views/approval/ApprovalTypeManagement.vue'),
            meta: { title: '审批类型', icon: 'FileTextOutlined' },
          },
          {
            path: 'instances',
            name: 'ApprovalInstanceManagement',
            component: () => import('@/views/approval/ApprovalInstanceManagement.vue'),
            meta: { title: '审批实例', icon: 'FileSearchOutlined' },
          },
          {
            path: 'pending',
            name: 'PendingApprovals',
            component: () => import('@/views/approval/PendingApprovals.vue'),
            meta: { title: '待我审批', icon: 'ClockCircleOutlined' },
          },
        ],
      },
      {
        path: 'workflow',
        name: 'Workflow',
        meta: { title: '工作流', icon: 'BranchesOutlined' },
        children: [
          {
            path: 'definitions',
            name: 'WorkflowDefinitions',
            component: () => import('@/views/workflow/WorkflowDefinitions.vue'),
            meta: { title: '流程定义', icon: 'DeploymentUnitOutlined' },
          },
          {
            path: 'instances',
            name: 'WorkflowInstances',
            component: () => import('@/views/workflow/WorkflowInstances.vue'),
            meta: { title: '流程实例', icon: 'NodeIndexOutlined' },
          },
        ],
      },
      {
        path: 'message',
        name: 'Message',
        meta: { title: '消息中心', icon: 'MessageOutlined' },
        children: [
          {
            path: 'notifications',
            name: 'NotificationCenter',
            component: () => import('@/views/message/NotificationCenter.vue'),
            meta: { title: '通知中心', icon: 'BellOutlined' },
          },
          {
            path: 'templates',
            name: 'MessageTemplates',
            component: () => import('@/views/message/MessageTemplates.vue'),
            meta: { title: '消息模板', icon: 'FormOutlined' },
          },
        ],
      },
      {
        path: 'file',
        name: 'File',
        meta: { title: '文件管理', icon: 'FolderOutlined' },
        children: [
          {
            path: 'list',
            name: 'FileManagement',
            component: () => import('@/views/file/FileManagement.vue'),
            meta: { title: '文件列表', icon: 'FileOutlined' },
          },
        ],
      },
      {
        path: 'ai',
        name: 'AI',
        meta: { title: 'AI 助手', icon: 'RobotOutlined' },
        children: [
          {
            path: 'chat',
            name: 'AiChat',
            component: () => import('@/views/ai/AiChat.vue'),
            meta: { title: 'AI 对话', icon: 'CommentOutlined' },
          },
        ],
      },
      {
        path: 'report',
        name: 'Report',
        meta: { title: '报表统计', icon: 'BarChartOutlined' },
        children: [
          {
            path: 'approval',
            name: 'ApprovalReport',
            component: () => import('@/views/report/ApprovalReport.vue'),
            meta: { title: '审批报表', icon: 'PieChartOutlined' },
          },
          {
            path: 'employee',
            name: 'EmployeeReport',
            component: () => import('@/views/report/EmployeeReport.vue'),
            meta: { title: '员工报表', icon: 'FundOutlined' },
          },
        ],
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (requiresAuth && !userStore.isAuthenticated) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && userStore.isAuthenticated) {
    next({ path: '/' })
  } else {
    next()
  }
})

router.afterEach((to) => {
  document.title = `${to.meta.title || 'FlowX'} - FlowX 企业级工作流平台`
})

export default router
