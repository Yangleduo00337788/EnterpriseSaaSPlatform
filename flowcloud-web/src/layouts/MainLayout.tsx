import { useState, useEffect, useCallback, useMemo } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Nav, Avatar, Dropdown, Badge } from '@douyinfe/semi-ui';
import {
  IconHome, IconList, IconUser, IconSetting, IconExit,
  IconFile, IconSend, IconPlus, IconBell,
} from '@douyinfe/semi-icons';
import { useAppDispatch, useAppSelector } from '@/hooks/useAppDispatch';
import { logout } from '@/store/authSlice';
import { getUnreadCount } from '@/api/message';
import { canAccessPath, hasPermission, PERM } from '@/utils/permissions';

const { Header, Sider, Content } = Layout;

const ALL_MENU_ITEMS = [
  { itemKey: '/dashboard', text: '工作台', icon: <IconHome /> },
  { itemKey: '/approval/pending', text: '待我审批', icon: <IconList /> },
  { itemKey: '/approval/my', text: '我的申请', icon: <IconSend /> },
  { itemKey: '/approval/submit', text: '发起审批', icon: <IconPlus /> },
  { itemKey: '/approval/all', text: '全部审批', icon: <IconFile /> },
  { itemKey: '/templates', text: '流程模板', icon: <IconSetting /> },
  { itemKey: '/messages', text: '消息中心', icon: <IconBell /> },
  { itemKey: '/report', text: '报表分析', icon: <IconFile /> },
  { itemKey: '/system/users', text: '员工管理', icon: <IconUser /> },
  { itemKey: '/system/roles', text: '角色管理', icon: <IconSetting /> },
  { itemKey: '/system/depts', text: '组织架构', icon: <IconSetting /> },
  { itemKey: '/system/positions', text: '岗位管理', icon: <IconSetting /> },
  { itemKey: '/system/tenant', text: '租户中心', icon: <IconSetting /> },
  { itemKey: '/system/audit-logs', text: '审计日志', icon: <IconFile /> },
  { itemKey: '/system/dicts', text: '系统字典', icon: <IconSetting /> },
  { itemKey: '/system/message-templates', text: '消息模板', icon: <IconBell /> },
];

export default function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useAppDispatch();
  const user = useAppSelector((s) => s.auth.user);
  const [collapsed, setCollapsed] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  const menuItems = useMemo(
    () => ALL_MENU_ITEMS.filter((item) => canAccessPath(user, item.itemKey)),
    [user],
  );

  const canReadMessages = hasPermission(user, PERM.MESSAGES);

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
    if (!canReadMessages) return undefined;
    const timer = setInterval(fetchUnread, 30000);
    return () => clearInterval(timer);
  }, [fetchUnread, canReadMessages, location.pathname]);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <Layout style={{ height: '100vh' }}>
      <Sider style={{ background: '#fff' }}>
        <div style={{
          height: 60,
          display: 'flex',
          alignItems: 'center',
          justifyContent: collapsed ? 'center' : 'flex-start',
          padding: collapsed ? 0 : '0 20px',
          borderBottom: '1px solid #f0f0f0',
          gap: 10,
          overflow: 'hidden',
          boxSizing: 'border-box',
        }}>
          <img src="/logo.png" alt="logo" style={{ height: 40, width: 40, objectFit: 'contain', flexShrink: 0 }} />
          {!collapsed && (
            <span style={{ fontWeight: 700, fontSize: 17, color: '#3370ff', whiteSpace: 'nowrap' }}>
              审流云
            </span>
          )}
        </div>
        <Nav
          style={{ maxWidth: 220, height: 'calc(100% - 60px)' }}
          selectedKeys={[location.pathname]}
          items={menuItems}
          onSelect={(data) => navigate(data.itemKey as string)}
          footer={{ collapseButton: true }}
          onCollapseChange={setCollapsed}
        />
      </Sider>
      <Layout>
        <Header style={{
          background: '#fff', padding: '0 24px', display: 'flex',
          alignItems: 'center', justifyContent: 'space-between',
          borderBottom: '1px solid #f0f0f0', height: 60,
        }}>
          <span style={{ color: '#86909c', fontSize: 14 }}>
            {user?.tenantName} · 让审批像流水一样高效
          </span>
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <Badge count={unreadCount} type="danger" overflowCount={99}>
              <IconBell
                style={{ fontSize: 18, cursor: 'pointer', color: '#4e5969' }}
                onClick={() => navigate('/messages')}
              />
            </Badge>
            <Dropdown
              trigger="click"
              position="bottomRight"
              render={
                <Dropdown.Menu>
                  <Dropdown.Item onClick={() => navigate('/messages')}>
                    <IconBell style={{ marginRight: 8 }} />消息中心
                  </Dropdown.Item>
                  <Dropdown.Item onClick={handleLogout}>
                    <IconExit style={{ marginRight: 8 }} />退出登录
                  </Dropdown.Item>
                </Dropdown.Menu>
              }
            >
              <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer', gap: 8 }}>
                <Avatar size="small" color="blue">{user?.realName?.[0] || 'U'}</Avatar>
                <span>{user?.realName}</span>
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
