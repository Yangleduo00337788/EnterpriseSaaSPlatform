export interface RecentTenantSession {
  tenantCode: string;
  tenantName: string;
  username?: string;
  lastUsedAt: number;
}

const RECENT_TENANTS_STORAGE_KEY = 'flowcloud-recent-tenants';
const LAST_TENANT_CODE_STORAGE_KEY = 'flowcloud-last-tenant-code';
const MAX_RECENT_TENANTS = 6;

function canUseStorage() {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
}

function readRecentTenants(): RecentTenantSession[] {
  if (!canUseStorage()) {
    return [];
  }
  try {
    const raw = window.localStorage.getItem(RECENT_TENANTS_STORAGE_KEY);
    if (!raw) {
      return [];
    }
    const parsed = JSON.parse(raw) as RecentTenantSession[];
    if (!Array.isArray(parsed)) {
      return [];
    }
    return parsed.filter((item) => item?.tenantCode && item?.tenantName);
  } catch {
    return [];
  }
}

export function getRecentTenants() {
  return readRecentTenants().sort((a, b) => b.lastUsedAt - a.lastUsedAt);
}

export function getRecentTenantByCode(tenantCode?: string | null) {
  if (!tenantCode) {
    return undefined;
  }
  return getRecentTenants().find((item) => item.tenantCode === tenantCode);
}

export function getPreferredTenantCode() {
  if (!canUseStorage()) {
    return '';
  }
  return (
    window.localStorage.getItem(LAST_TENANT_CODE_STORAGE_KEY) ??
    getRecentTenants()[0]?.tenantCode ??
    ''
  );
}

export function rememberTenantSession(payload: {
  tenantCode: string;
  tenantName: string;
  username?: string;
}) {
  if (!canUseStorage()) {
    return;
  }
  const tenantCode = payload.tenantCode.trim();
  if (!tenantCode) {
    return;
  }

  const nextItem: RecentTenantSession = {
    tenantCode,
    tenantName: payload.tenantName.trim() || tenantCode,
    username: payload.username?.trim() || '',
    lastUsedAt: Date.now(),
  };

  const merged = [nextItem, ...getRecentTenants().filter((item) => item.tenantCode !== tenantCode)]
    .slice(0, MAX_RECENT_TENANTS);

  window.localStorage.setItem(RECENT_TENANTS_STORAGE_KEY, JSON.stringify(merged));
  window.localStorage.setItem(LAST_TENANT_CODE_STORAGE_KEY, tenantCode);
}
