export const TENANT_FEATURE_LABEL_MAP: Record<string, string> = {
  approval: '审批流',
  report: '报表分析',
  message: '消息中心',
  tenantSettings: '租户设置',
};

export const TENANT_FEATURE_OPTIONS = Object.entries(TENANT_FEATURE_LABEL_MAP).map(([key, label]) => ({
  key,
  label,
}));

export const TENANT_FEATURE_DEFAULTS: Record<string, boolean> = Object.fromEntries(
  TENANT_FEATURE_OPTIONS.map((item) => [item.key, true]),
);

export const TENANT_FEATURE_DISABLED_DEFAULTS: Record<string, boolean> = Object.fromEntries(
  TENANT_FEATURE_OPTIONS.map((item) => [item.key, false]),
);
