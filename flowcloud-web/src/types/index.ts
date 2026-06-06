export interface ApiResult<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

export interface LoginForm {
  username: string;
  password: string;
  tenantCode: string;
}

export interface RegisterForm {
  tenantName: string;
  tenantCode: string;
  contactName: string;
  contactPhone: string;
  contactEmail?: string;
  username: string;
  password: string;
  realName: string;
}

export interface UserInfo {
  token: string;
  userId: number;
  tenantId: number;
  deptId?: number;
  username: string;
  realName: string;
  avatar?: string;
  tenantName: string;
  logo?: string;
  themeColor?: string;
  dataScope?: string;
  roles: string[];
  permissions: string[];
  enabledFeatures?: string[];
}

export interface UserVO {
  id: number;
  username: string;
  realName: string;
  email?: string;
  phone?: string;
  deptId?: number;
  deptName?: string;
  managerId?: number;
  managerName?: string;
  jobTitle?: string;
  workStatus?: string;
  status: number;
  roleIds?: number[];
  roleNames?: string[];
  createTime: string;
}

export interface DeptVO {
  id: number;
  parentId?: number;
  deptName: string;
  leader?: string;
  leaderUserId?: number;
  ancestors?: string;
  sort?: number;
  status: number;
  children?: DeptVO[];
}

export interface UserOptionVO {
  id: number;
  realName: string;
  username: string;
  jobTitle?: string;
  deptName?: string;
}

export interface RoleOptionVO {
  id: number;
  roleCode: string;
  roleName: string;
  dataScope?: string;
}

export interface RoleVO {
  id: number;
  roleCode: string;
  roleName: string;
  description?: string;
  dataScope?: string;
  sort?: number;
  status: number;
  permissionIds?: number[];
  createTime?: string;
}

export interface PermissionVO {
  id: number;
  parentId?: number;
  permCode: string;
  permName: string;
  permType?: string;
  path?: string;
  sort?: number;
  children?: PermissionVO[];
}

export interface DictTypeVO {
  id: number;
  dictCode: string;
  dictName: string;
  status: number;
  remark?: string;
  items?: DictDataVO[];
}

export interface DictDataVO {
  id: number;
  dictTypeId: number;
  dictLabel: string;
  dictValue: string;
  sort?: number;
  status: number;
  remark?: string;
}

export interface MessageTemplateVO {
  id: number;
  templateCode: string;
  templateName: string;
  eventType: string;
  titleTemplate: string;
  contentTemplate: string;
  status: number;
}

export interface ReportTrendItem {
  period: string;
  total: number;
  approved: number;
  rejected: number;
}

export interface ReportDeptItem {
  deptId: number;
  deptName: string;
  total: number;
  approved: number;
  avgHours: number;
}

export interface ReportApproverItem {
  approverId: number;
  approverName: string;
  pendingCount: number;
  handledCount: number;
}

export interface ReportAnalyticsVO {
  rejectionRate: number;
  trend: ReportTrendItem[];
  deptEfficiency: ReportDeptItem[];
  approverLoad: ReportApproverItem[];
}

export interface TenantProfileVO {
  id: number;
  tenantCode: string;
  tenantName: string;
  contactName?: string;
  contactPhone?: string;
  contactEmail?: string;
  logo?: string;
  themeColor?: string;
  status: number;
  planType?: string;
  maxUsers?: number;
  currentUsers?: number;
  remainingUserSlots?: number;
  expireTime?: string;
  expired?: boolean;
  packageConfig?: string;
  featureConfig?: string;
  enabledFeatures?: string[];
}

export interface PositionVO {
  id: number;
  positionCode: string;
  positionName: string;
  deptId?: number;
  deptName?: string;
  sort?: number;
  status: number;
  remark?: string;
}

export interface TemplateVO {
  id: number;
  templateCode: string;
  templateName: string;
  category: string;
  description?: string;
  formSchema?: string;
  flowNodes?: FlowNode[];
  status: number;
  statusLabel?: string;
  sort?: number;
  pubVersion?: number;
}

export interface FlowNode {
  index: number;
  name: string;
  type: string;
  approverIds: number[];
}

export interface InstanceVO {
  id: number;
  instanceNo: string;
  templateId: number;
  templateName: string;
  category: string;
  title: string;
  applicantId: number;
  applicantName: string;
  formData?: string;
  status: string;
  statusLabel: string;
  currentNode: number;
  submitTime?: string;
  finishTime?: string;
  createTime: string;
  records?: RecordVO[];
  tasks?: TaskVO[];
}

export interface TaskVO {
  id: number;
  instanceId: number;
  instanceNo: string;
  title: string;
  nodeIndex: number;
  nodeName: string;
  approverId: number;
  approverName: string;
  status: string;
  statusLabel: string;
  comment?: string;
  handleTime?: string;
  createTime: string;
}

export interface RecordVO {
  nodeIndex: number;
  nodeName: string;
  operatorId: number;
  operatorName: string;
  action: string;
  comment?: string;
  createTime: string;
}

export interface MessageVO {
  id: number;
  title: string;
  content: string;
  type: string;
  bizType?: string;
  bizId?: number;
  isRead: number;
  createTime: string;
}

export interface DashboardVO {
  totalInstances: number;
  pendingCount: number;
  approvedCount: number;
  rejectedCount: number;
  myPendingTasks: number;
  categoryStats: { category: string; count: number }[];
  monthlyTrend: unknown[];
}
