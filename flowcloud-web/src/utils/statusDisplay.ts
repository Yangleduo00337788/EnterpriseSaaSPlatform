export const ENABLED_STATUS_META: Record<number, { text: string; color: 'green' | 'red' }> = {
  0: { text: '禁用', color: 'red' },
  1: { text: '启用', color: 'green' },
};

export const ENABLED_STATUS_OPTIONS = Object.entries(ENABLED_STATUS_META).map(([value, item]) => ({
  value: Number(value),
  label: item.text,
}));

export const TENANT_STATUS_META: Record<number, { text: string; color: 'green' | 'red' }> = {
  0: { text: '已停用', color: 'red' },
  1: { text: '启用中', color: 'green' },
};

export const MESSAGE_READ_STATUS_META: Record<number, { text: string; color: 'green' | 'orange' }> = {
  0: { text: '未读', color: 'orange' },
  1: { text: '已读', color: 'green' },
};

export const WORK_STATUS_META: Record<string, { text: string; color: 'green' | 'blue' | 'grey' }> = {
  active: { text: '在岗', color: 'green' },
  probation: { text: '试用', color: 'blue' },
  inactive: { text: '离职', color: 'grey' },
};

export const WORK_STATUS_OPTIONS = Object.entries(WORK_STATUS_META).map(([value, item]) => ({
  value,
  label: item.text,
}));

export const MESSAGE_TYPE_META: Record<string, { text: string; color: 'blue' | 'grey' }> = {
  approval: { text: '审批', color: 'blue' },
  system: { text: '系统', color: 'grey' },
};

export const AUDIT_ACTION_META: Record<string, { color: 'green' | 'grey' | 'blue' | 'orange' | 'teal' | 'red' | 'light-blue' }> = {
  LOGIN: { color: 'green' },
  LOGOUT: { color: 'grey' },
  PUBLISH_TEMPLATE: { color: 'blue' },
  DISABLE_TEMPLATE: { color: 'orange' },
  SUBMIT_APPROVAL: { color: 'teal' },
  APPROVE_TASK: { color: 'green' },
  REJECT_TASK: { color: 'red' },
};

export const AUDIT_RESULT_META: Record<string, { text: string; color: 'green' | 'red' }> = {
  success: { text: '成功', color: 'green' },
  fail: { text: '失败', color: 'red' },
};

export const EXPIRY_STATUS_META = {
  expired: { text: '已到期', color: 'red' as const },
};
