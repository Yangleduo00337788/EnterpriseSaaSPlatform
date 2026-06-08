export const APPROVAL_CATEGORY_OPTIONS = [
  { value: 'leave', label: '请假' },
  { value: 'expense', label: '报销' },
  { value: 'contract', label: '合同' },
  { value: 'purchase', label: '采购' },
  { value: 'other', label: '其他' },
] as const;

export const APPROVAL_STATUS_META: Record<string, { text: string; color: string; tone: string }> = {
  draft: { text: '草稿', color: 'grey', tone: 'neutral' },
  pending: { text: '审批中', color: 'blue', tone: 'processing' },
  approved: { text: '已通过', color: 'green', tone: 'success' },
  rejected: { text: '已驳回', color: 'red', tone: 'danger' },
  cancelled: { text: '已撤销', color: 'orange', tone: 'warning' },
};

export const APPROVAL_STATUS_OPTIONS = Object.entries(APPROVAL_STATUS_META).map(([value, item]) => ({
  value,
  label: item.text,
}));

export const TEMPLATE_STATUS_META: Record<number, { text: string; color: 'green' | 'grey' | 'red' | 'orange' }> = {
  0: { text: '草稿', color: 'orange' },
  1: { text: '已发布', color: 'green' },
  2: { text: '已停用', color: 'grey' },
};

export const TASK_RESULT_META: Record<string, { text: string; color: 'green' | 'grey' | 'red' | 'blue' | 'orange'; tone: string }> = {
  pending: { text: '待处理', color: 'blue', tone: 'processing' },
  approved: { text: '已通过', color: 'green', tone: 'success' },
  rejected: { text: '已驳回', color: 'red', tone: 'danger' },
  cancelled: { text: '已撤销', color: 'orange', tone: 'warning' },
};
