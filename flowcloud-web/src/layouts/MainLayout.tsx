import { useState, useEffect, useCallback, useMemo } from 'react';
import type { ReactNode } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Nav, Avatar, Dropdown, Badge, Button, Toast, Space, Typography, Breadcrumb } from '@douyinfe/semi-ui';
import {
  IconHome, IconList, IconUser, IconSetting, IconExit,
  IconFile, IconSend, IconPlus, IconBell, IconMoon, IconSun, IconChevronDown, IconHistogram,
} from '@douyinfe/semi-icons';
import { useAppDispatch, useAppSelector } from '@/hooks/useAppDispatch';
import { logout } from '@/store/authSlice';
import { getUnreadCount } from '@/api/message';
import { canAccessPath, hasPermission, PERM } from '@/utils/permissions';
import { filterAccessibleMenus, findMenuTrailByPath } from '@/utils/menuTree';
import { getPreferredTenantCode, getRecentTenants } from '@/utils/tenantSession';
import type { MenuVO } from '@/types';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;
const DEFAULT_THEME_COLOR = '#3370FF';
const THEME_STORAGE_KEY = 'theme-mode';
const MESSAGE_AUTO_REFRESH_STORAGE_KEY = 'message-auto-refresh';

interface AppMenuItem {
  itemKey: string;
  text: string;
  icon?: ReactNode;
  items?: AppMenuItem[];
}

const ICON_MAP: Record<string, ReactNode> = {
  home: <IconHome />,
  approval: <IconList />,
  template: <IconSetting />,
  setting: <IconSetting />,
  bell: <IconBell />,
  user: <IconUser />,
  file: <IconFile />,
  send: <IconSend />,
  plus: <IconPlus />,
  report: <IconHistogram />,
};

function getMenuNodeKey(menu: MenuVO): string {
  return menu.path?.trim() || `menu-${menu.id}`;
}

function getMenuIcon(menu: MenuVO): ReactNode | undefined {
  if (menu.icon && ICON_MAP[menu.icon]) {
    return ICON_MAP[menu.icon];
  }
  if (menu.path === '/dashboard') return <IconHome />;
  if (menu.path?.startsWith('/approval/')) return <IconList />;
  if (menu.path === '/messages') return <IconBell />;
  if (menu.path === '/report') return <IconHistogram />;
  if (menu.path?.startsWith('/system/files')) return <IconFile />;
  if (menu.path?.startsWith('/system/users')) return <IconUser />;
  if (menu.path?.startsWith('/system/')) return <IconSetting />;
  return undefined;
}

function buildNavItems(menus: MenuVO[]): AppMenuItem[] {
  return menus.map((menu) => ({
    itemKey: getMenuNodeKey(menu),
    text: menu.permName,
    icon: getMenuIcon(menu),
    items: menu.children?.length ? buildNavItems(menu.children) : undefined,
  }));
}

function getOpenKeysFromTrail(trail: MenuVO[]): string[] {
  return trail
    .slice(0, -1)
    .filter((menu) => (menu.children?.length ?? 0) > 0)
    .map((menu) => getMenuNodeKey(menu));
}

interface BreadcrumbItemConfig {
  label: string;
  path?: string;
  icon?: ReactNode;
}

export interface MainLayoutOutletContext {
  themeMode: 'light' | 'dark';
  applyTheme: (newTheme: 'light' | 'dark') => void;
  messageAutoRefresh: boolean;
  handleMessageAutoRefreshChange: (checked: boolean) => void;
}

function getBreadcrumbItems(pathname: string, trail: MenuVO[]) {
  if (pathname === '/dashboard') {
    return [{ label: '首页', path: '/dashboard', icon: <IconHome /> }] satisfies BreadcrumbItemConfig[];
  }
  if (pathname === '/profile') {
    return [
      { label: '首页', path: '/dashboard', icon: <IconHome /> },
      { label: '个人信息', path: pathname },
    ] satisfies BreadcrumbItemConfig[];
  }
  if (pathname.startsWith('/approval/detail/')) {
    return [
      { label: '首页', path: '/dashboard', icon: <IconHome /> },
      { label: '审批中心', path: '/approval/pending' },
      { label: '审批详情', path: pathname },
    ] satisfies BreadcrumbItemConfig[];
  }
  if (trail.length > 0) {
    return [
      { label: '首页', path: '/dashboard', icon: <IconHome /> },
      ...trail.map((menu) => ({ label: menu.permName, path: menu.path })),
    ] satisfies BreadcrumbItemConfig[];
  }
  return [
    { label: '首页', path: '/dashboard', icon: <IconHome /> },
    { label: '工作台', path: '/dashboard' },
  ];
}

export default function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useAppDispatch();
  const user = useAppSelector((s) => s.auth.user);
  const menus = useAppSelector((s) => s.auth.menus);
  const [collapsed, setCollapsed] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [themeMode, setThemeMode] = useState<'light' | 'dark'>('light');
  const [openKeys, setOpenKeys] = useState<string[]>([]);
  const [messageAutoRefresh, setMessageAutoRefresh] = useState(true);

  useEffect(() => {
    const savedTheme = localStorage.getItem(THEME_STORAGE_KEY) as 'light' | 'dark';
    if (savedTheme === 'dark') {
      setThemeMode('dark');
      document.body.setAttribute('theme-mode', 'dark');
    }
    const savedAutoRefresh = localStorage.getItem(MESSAGE_AUTO_REFRESH_STORAGE_KEY);
    if (savedAutoRefresh != null) {
      setMessageAutoRefresh(savedAutoRefresh === 'true');
    }
  }, []);

  const applyTheme = (newTheme: 'light' | 'dark') => {
    setThemeMode(newTheme);
    if (newTheme === 'dark') {
      document.body.setAttribute('theme-mode', 'dark');
    } else {
      document.body.removeAttribute('theme-mode');
    }
    localStorage.setItem(THEME_STORAGE_KEY, newTheme);
  };

  const toggleTheme = () => {
    applyTheme(themeMode === 'light' ? 'dark' : 'light');
  };

  const accessibleMenus = useMemo(() => filterAccessibleMenus(user, menus), [menus, user]);
  const menuItems = useMemo(() => buildNavItems(accessibleMenus), [accessibleMenus]);
  const currentMenuTrail = useMemo(
    () => findMenuTrailByPath(accessibleMenus, location.pathname),
    [accessibleMenus, location.pathname],
  );
  const currentOpenKeys = useMemo(() => getOpenKeysFromTrail(currentMenuTrail), [currentMenuTrail]);

  useEffect(() => {
    if (collapsed || currentOpenKeys.length === 0) {
      return;
    }
    setOpenKeys((current) => {
      const merged = new Set([...current, ...currentOpenKeys]);
      return Array.from(merged);
    });
  }, [collapsed, currentOpenKeys]);

  const canReadMessages = hasPermission(user, PERM.MESSAGES);
  const currentTenantCode = useMemo(
    () => user?.tenantCode || getPreferredTenantCode(),
    [user?.tenantCode, user?.tenantId, user?.tenantName],
  );
  const recentTenants = useMemo(() => {
    const storedTenants = getRecentTenants();
    if (!currentTenantCode || !user?.tenantName) {
      return storedTenants;
    }
    return [
      {
        tenantCode: currentTenantCode,
        tenantName: user.tenantName,
        username: user.username,
        lastUsedAt: Date.now(),
      },
      ...storedTenants.filter((item) => item.tenantCode !== currentTenantCode),
    ];
  }, [currentTenantCode, user]);
  const switchableTenants = useMemo(
    () => recentTenants.filter((item) => item.tenantCode !== currentTenantCode),
    [currentTenantCode, recentTenants],
  );
  const activeThemeColor = user?.themeColor?.trim() || DEFAULT_THEME_COLOR;
  const breadcrumbItems = useMemo(
    () => getBreadcrumbItems(location.pathname, currentMenuTrail),
    [currentMenuTrail, location.pathname],
  );

  const fetchUnread = useCallback(async () => {
    if (!canReadMessages) {
      setUnreadCount(0);
      return;
    }
    try {
      const res = await getUnreadCount();
      setUnreadCount(res.data);
    } catch {
      setUnreadCount(0);
    }
  }, [canReadMessages]);

  useEffect(() => {
    fetchUnread();
    if (!canReadMessages || !messageAutoRefresh) return undefined;
    const timer = setInterval(fetchUnread, 30000);
    return () => clearInterval(timer);
  }, [fetchUnread, canReadMessages, location.pathname, messageAutoRefresh]);

  useEffect(() => {
    const softColor = themeMode === 'dark'
      ? `color-mix(in srgb, ${activeThemeColor} 22%, transparent)`
      : `color-mix(in srgb, ${activeThemeColor} 12%, white)`;
    const borderColor = themeMode === 'dark'
      ? `color-mix(in srgb, ${activeThemeColor} 38%, var(--semi-color-border))`
      : `color-mix(in srgb, ${activeThemeColor} 24%, white)`;
    const hoverColor = themeMode === 'dark'
      ? `color-mix(in srgb, ${activeThemeColor} 78%, white)`
      : `color-mix(in srgb, ${activeThemeColor} 88%, white)`;
    const activeColor = themeMode === 'dark'
      ? `color-mix(in srgb, ${activeThemeColor} 72%, black)`
      : `color-mix(in srgb, ${activeThemeColor} 82%, black)`;

    document.documentElement.style.setProperty('--fc-theme-primary', activeThemeColor);
    document.documentElement.style.setProperty('--fc-theme-primary-soft', softColor);
    document.documentElement.style.setProperty('--fc-theme-primary-border', borderColor);
    document.documentElement.style.setProperty('--fc-theme-primary-hover', hoverColor);
    document.documentElement.style.setProperty('--fc-theme-primary-active', activeColor);
    document.documentElement.style.setProperty('--semi-color-primary', activeThemeColor);
  }, [activeThemeColor, themeMode]);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const handleMessageAutoRefreshChange = (checked: boolean) => {
    setMessageAutoRefresh(checked);
    localStorage.setItem(MESSAGE_AUTO_REFRESH_STORAGE_KEY, String(checked));
    Toast.success(checked ? '已开启消息角标自动刷新' : '已关闭消息角标自动刷新');
  };

  const handleTenantSwitch = (tenantCode: string) => {
    dispatch(logout());
    Toast.info(`已切换到企业 ${tenantCode}，请重新登录`);
    navigate(`/login?tenantCode=${encodeURIComponent(tenantCode)}`);
  };

  const handleOpenProfile = () => {
    navigate('/profile');
  };

  return (
    <Layout style={{ height: '100vh', backgroundColor: 'var(--semi-color-bg-0)' }}>
      <Sider
        style={{
          backgroundColor: 'var(--semi-color-bg-1)',
          borderRight: '1px solid var(--semi-color-border)',
          width: collapsed ? 84 : 256,
          minWidth: collapsed ? 84 : 256,
          transition: 'all 0.2s ease',
        }}
      >
        <div style={{
          height: 78,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: collapsed ? 0 : '0 20px',
          borderBottom: '1px solid var(--semi-color-border)',
          gap: collapsed ? 0 : 12,
          overflow: 'hidden',
          boxSizing: 'border-box',
          width: '100%',
        }}>
          <img src="/logo.png" alt="logo" style={{ height: 56, width: 56, objectFit: 'contain', flexShrink: 0 }} />
          {!collapsed && (
            <Text strong style={{ fontSize: 19, whiteSpace: 'nowrap', letterSpacing: 0.2 }}>审流云</Text>
          )}
        </div>
        <Nav
          style={{ maxWidth: 256, width: '100%', height: 'calc(100% - 78px)', borderRight: 'none' }}
          openKeys={collapsed ? [] : openKeys}
          selectedKeys={[location.pathname]}
          items={menuItems}
          onSelect={(data) => {
            const targetPath = data.itemKey as string;
            if (targetPath.startsWith('/')) {
              navigate(targetPath);
            }
          }}
          onOpenChange={(data) => setOpenKeys((data.openKeys as string[]) ?? [])}
          footer={{ collapseButton: true }}
          onCollapseChange={(isCollapsed) => {
            setCollapsed(isCollapsed);
            if (!isCollapsed) {
              setOpenKeys(currentOpenKeys);
            }
          }}
        />
      </Sider>
      <Layout style={{ minWidth: 0 }}>
        <Header className="glass-header" style={{
          padding: '0 32px', display: 'flex',
          alignItems: 'center', justifyContent: 'space-between',
          height: 68,
        }}>
          <Dropdown
            trigger="click"
            position="bottomLeft"
            render={
              <Dropdown.Menu>
                <Dropdown.Item disabled>
                  当前企业: {user?.tenantName || '-'}
                </Dropdown.Item>
                <Dropdown.Item disabled>
                  企业编码: {currentTenantCode || '-'}
                </Dropdown.Item>
                <Dropdown.Divider />
                <Dropdown.Item onClick={() => navigate('/system/tenant')}>
                  进入租户中心
                </Dropdown.Item>
                {switchableTenants.length > 0 && <Dropdown.Divider />}
                {switchableTenants.map((item) => (
                  <Dropdown.Item key={item.tenantCode} onClick={() => handleTenantSwitch(item.tenantCode)}>
                    切换到 {item.tenantName} ({item.tenantCode})
                  </Dropdown.Item>
                ))}
                {switchableTenants.length === 0 && (
                  <Dropdown.Item disabled>
                    暂无其他最近登录企业
                  </Dropdown.Item>
                )}
              </Dropdown.Menu>
            }
          >
            <Button
              type="tertiary"
              theme="light"
              icon={<IconChevronDown />}
              iconPosition="right"
              className="header-tenant-button"
            >
              {user?.tenantName} · 让审批像流水一样高效
            </Button>
          </Dropdown>
          <Space spacing={18}>
            <Button
              type="tertiary"
              theme="borderless"
              icon={themeMode === 'light' ? <IconMoon /> : <IconSun />}
              onClick={toggleTheme}
              className="header-icon-button"
            />
            <Badge count={unreadCount} type="danger" overflowCount={99} className="header-message-badge">
              <Button
                type="tertiary"
                theme="borderless"
                icon={<IconBell />}
                className="header-message-button"
                onClick={() => navigate('/messages')}
              />
            </Badge>
            <Dropdown
              trigger="click"
              position="bottomRight"
              render={
                <Dropdown.Menu>
                  <Dropdown.Item onClick={handleOpenProfile}>
                    <IconUser style={{ marginRight: 8 }} />个人信息
                  </Dropdown.Item>
                  <Dropdown.Divider />
                  <Dropdown.Item onClick={() => navigate('/messages')}>
                    <IconBell style={{ marginRight: 8 }} />消息中心
                  </Dropdown.Item>
                  <Dropdown.Item onClick={handleLogout}>
                    <IconExit style={{ marginRight: 8 }} />退出登录
                  </Dropdown.Item>
                </Dropdown.Menu>
              }
            >
              <Button
                type="tertiary"
                theme="borderless"
                className="header-user-trigger"
              >
                <Avatar size="small" color="blue" src={user?.avatar}>
                  {user?.realName?.[0] || 'U'}
                </Avatar>
                <Text strong>{user?.realName}</Text>
                <IconChevronDown className="header-user-chevron" />
              </Button>
            </Dropdown>
          </Space>
        </Header>
        <Content style={{ backgroundColor: 'var(--semi-color-bg-0)', overflow: 'auto', minWidth: 0 }}>
          <div className="content-shell">
            <div className="content-breadcrumb-shell">
              <Breadcrumb className="content-breadcrumb">
                {breadcrumbItems.map((item, index) => {
                  const isLast = index === breadcrumbItems.length - 1;
                  return (
                    <Breadcrumb.Item
                      key={`${item.label}-${item.path || index}`}
                      icon={item.icon}
                      onClick={!isLast && item.path ? () => navigate(item.path as string) : undefined}
                    >
                      {item.label}
                    </Breadcrumb.Item>
                  );
                })}
              </Breadcrumb>
            </div>
            <div className="content-inner">
              <Outlet context={{
                themeMode,
                applyTheme,
                messageAutoRefresh,
                handleMessageAutoRefreshChange,
              }} />
            </div>
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
