export const CATEGORY_MAP: Record<string, string> = {
  leave: '请假',
  expense: '报销',
  contract: '合同',
  purchase: '采购',
  other: '其他',
};

export const APPROVER_TYPE_MAP: Record<string, string> = {
  approval: '指定审批人',
  self: '发起人自审',
};

export const STATUS_MAP: Record<string, { text: string; color: string }> = {
  draft: { text: '草稿', color: 'grey' },
  pending: { text: '审批中', color: 'blue' },
  approved: { text: '已通过', color: 'green' },
  rejected: { text: '已驳回', color: 'red' },
  cancelled: { text: '已撤销', color: 'orange' },
};
