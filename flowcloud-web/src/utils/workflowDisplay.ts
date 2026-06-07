export const APPROVER_TYPE_MAP: Record<string, string> = {
  approval: '指定审批人',
  self: '发起人自审',
};

export const NODE_TYPE_MAP: Record<string, string> = {
  approval: '审批节点',
  self: '发起人自审',
};

export const NODE_TYPE_OPTIONS = Object.entries(NODE_TYPE_MAP).map(([value, label]) => ({
  value,
  label,
}));

export const NODE_MODE_MAP: Record<string, string> = {
  sequential: '顺序审批',
  countersign: '会签',
  'or-sign': '或签',
};

export const NODE_MODE_OPTIONS = Object.entries(NODE_MODE_MAP).map(([value, label]) => ({
  value,
  label,
}));

export const APPROVER_SOURCE_MAP: Record<string, string> = {
  users: '指定用户',
  dept_leader: '部门负责人',
  manager: '直属上级',
  role: '角色',
};

export const APPROVER_SOURCE_OPTIONS = Object.entries(APPROVER_SOURCE_MAP).map(([value, label]) => ({
  value,
  label,
}));
