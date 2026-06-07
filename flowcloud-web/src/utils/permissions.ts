import type { UserInfo } from '@/types';

export const PERM = {
  DASHBOARD: 'dashboard',
  SUBMIT: 'approval:submit',
  MY: 'approval:my',
  PENDING: 'approval:pending',
  ALL: 'approval:all',
  TEMPLATE: 'template',
  TEMPLATE_MANAGE: 'approval:template:manage',
  TASK_HANDLE: 'approval:task:handle',
  INSTANCE_VIEW_ALL: 'approval:instance:viewAll',
  MESSAGES: 'messages',
  USER: 'system:user',
  USER_VIEW: 'system:user:view',
  USER_EDIT: 'system:user:edit',
  DEPT: 'system:dept',
  DEPT_VIEW: 'system:dept:view',
  DEPT_EDIT: 'system:dept:edit',
  TENANT: 'system:tenant',
  TENANT_VIEW: 'system:tenant:view',
  TENANT_EDIT: 'system:tenant:edit',
  POSITION: 'system:position',
  POSITION_EDIT: 'system:position:edit',
  AUDIT: 'system:audit',
  AUDIT_VIEW: 'system:audit:view',
  ROLE: 'system:role',
  ROLE_VIEW: 'system:role:view',
  ROLE_EDIT: 'system:role:edit',
  DICT: 'system:dict',
  DICT_EDIT: 'system:dict:edit',
  REPORT: 'report',
  MESSAGE_TEMPLATE: 'system:message-template',
  MESSAGE_TEMPLATE_EDIT: 'system:message-template:edit',
} as const;

const PATH_FEATURE: Record<string, string> = {
  '/approval/pending': 'approval',
  '/approval/handled': 'approval',
  '/approval/my': 'approval',
  '/approval/submit': 'approval',
  '/approval/all': 'approval',
  '/templates': 'approval',
  '/messages': 'message',
  '/report': 'report',
  '/system/tenant': 'tenantSettings',
};

const MENU_PERM: Record<string, string> = {
  '/dashboard': PERM.DASHBOARD,
  '/approval/pending': PERM.PENDING,
  '/approval/handled': PERM.PENDING,
  '/approval/my': PERM.MY,
  '/approval/submit': PERM.SUBMIT,
  '/approval/all': PERM.ALL,
  '/templates': PERM.TEMPLATE,
  '/messages': PERM.MESSAGES,
  '/system/users': PERM.USER,
  '/system/depts': PERM.DEPT,
  '/system/positions': PERM.POSITION,
  '/system/tenant': PERM.TENANT,
  '/system/audit-logs': PERM.AUDIT,
  '/system/roles': PERM.ROLE,
  '/system/dicts': PERM.DICT,
  '/report': PERM.REPORT,
  '/system/message-templates': PERM.MESSAGE_TEMPLATE,
};

export function getUserPermissions(user: UserInfo | null): Set<string> {
  if (!user?.permissions?.length) return new Set();
  return new Set(user.permissions);
}

export function hasPermission(user: UserInfo | null, perm: string): boolean {
  if (!user) return false;
  const permissions = getUserPermissions(user);
  return user.roles?.includes('admin') || permissions.has('*') || permissions.has(perm);
}

export function isFeatureEnabled(user: UserInfo | null, feature: string): boolean {
  if (!user?.enabledFeatures?.length) return true;
  return user.enabledFeatures.includes(feature);
}

export function canAccessPath(user: UserInfo | null, path: string): boolean {
  const feature = PATH_FEATURE[path];
  if (feature && !isFeatureEnabled(user, feature)) {
    return false;
  }
  const perm = MENU_PERM[path];
  if (!perm) return true;
  return hasPermission(user, perm);
}

