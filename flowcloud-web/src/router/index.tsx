import { useEffect } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { Spin } from '@douyinfe/semi-ui';
import { useAppDispatch, useAppSelector } from '@/hooks/useAppDispatch';
import { fetchCurrentUser } from '@/store/authSlice';
import { canAccessPath } from '@/utils/permissions';
import MainLayout from '@/layouts/MainLayout';
import LoginPage from '@/pages/login';
import RegisterPage from '@/pages/register';
import DashboardPage from '@/pages/dashboard';
import PendingTasksPage from '@/pages/approval/pending';
import HandledTasksPage from '@/pages/approval/handled';
import MySubmissionsPage from '@/pages/approval/my-submissions';
import SubmitApprovalPage from '@/pages/approval/submit';
import InstanceDetailPage from '@/pages/approval/detail';
import TemplateListPage from '@/pages/template';
import UserListPage from '@/pages/system/users';
import DeptPage from '@/pages/system/depts';
import TenantPage from '@/pages/system/tenant';
import AllInstancesPage from '@/pages/approval/all';
import MessagesPage from '@/pages/messages';

import PositionsPage from '@/pages/system/positions';
import AuditLogsPage from '@/pages/system/audit-logs';
import RolesPage from '@/pages/system/roles';
import DictsPage from '@/pages/system/dicts';
import ReportPage from '@/pages/report';
import MessageTemplatesPage from '@/pages/system/message-templates';

const ACCESSIBLE_FALLBACK_PATHS = [
  '/dashboard',
  '/approval/pending',
  '/approval/handled',
  '/approval/my',
  '/approval/all',
  '/approval/submit',
  '/templates',
  '/messages',
  '/system/users',
  '/system/depts',
  '/system/positions',
  '/system/tenant',
  '/system/audit-logs',
  '/system/roles',
  '/system/dicts',
  '/report',
  '/system/message-templates',
] as const;

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token');
  if (!token) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function RoleRoute({ children, path }: { children: React.ReactNode; path: string }) {
  const user = useAppSelector((s) => s.auth.user);
  const loading = useAppSelector((s) => s.auth.loading);
  const location = useLocation();
  const token = localStorage.getItem('token');

  // Keep the user on the current route while auth state is restoring from /auth/me.
  if (loading || (token && !user)) {
    return <Spin style={{ display: 'block', margin: '100px auto' }} />;
  }
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  if (!canAccessPath(user, path)) {
    const fallbackPath = ACCESSIBLE_FALLBACK_PATHS.find((item) => canAccessPath(user, item));
    if (!fallbackPath || fallbackPath === path) {
      return <Navigate to="/login" replace state={{ from: location }} />;
    }
    return <Navigate to={fallbackPath} replace state={{ from: location }} />;
  }
  return <>{children}</>;
}

export default function AppRouter() {
  const dispatch = useAppDispatch();
  const user = useAppSelector((s) => s.auth.user);
  const loading = useAppSelector((s) => s.auth.loading);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token && !user && !loading) {
      dispatch(fetchCurrentUser());
    }
  }, [dispatch, loading, user]);

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/" element={<PrivateRoute><MainLayout /></PrivateRoute>}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<RoleRoute path="/dashboard"><DashboardPage /></RoleRoute>} />
        <Route path="approval/pending" element={<RoleRoute path="/approval/pending"><PendingTasksPage /></RoleRoute>} />
        <Route path="approval/handled" element={<RoleRoute path="/approval/handled"><HandledTasksPage /></RoleRoute>} />
        <Route path="approval/my" element={<RoleRoute path="/approval/my"><MySubmissionsPage /></RoleRoute>} />
        <Route path="approval/all" element={<RoleRoute path="/approval/all"><AllInstancesPage /></RoleRoute>} />
        <Route path="approval/submit" element={<RoleRoute path="/approval/submit"><SubmitApprovalPage /></RoleRoute>} />
        <Route path="approval/detail/:id" element={<InstanceDetailPage />} />
        <Route path="templates" element={<RoleRoute path="/templates"><TemplateListPage /></RoleRoute>} />
        <Route path="messages" element={<RoleRoute path="/messages"><MessagesPage /></RoleRoute>} />
        <Route path="system/users" element={<RoleRoute path="/system/users"><UserListPage /></RoleRoute>} />
        <Route path="system/depts" element={<RoleRoute path="/system/depts"><DeptPage /></RoleRoute>} />
        <Route path="system/positions" element={<RoleRoute path="/system/positions"><PositionsPage /></RoleRoute>} />
        <Route path="system/tenant" element={<RoleRoute path="/system/tenant"><TenantPage /></RoleRoute>} />
        <Route path="system/audit-logs" element={<RoleRoute path="/system/audit-logs"><AuditLogsPage /></RoleRoute>} />
        <Route path="system/roles" element={<RoleRoute path="/system/roles"><RolesPage /></RoleRoute>} />
        <Route path="system/dicts" element={<RoleRoute path="/system/dicts"><DictsPage /></RoleRoute>} />
        <Route path="report" element={<RoleRoute path="/report"><ReportPage /></RoleRoute>} />
        <Route path="system/message-templates" element={<RoleRoute path="/system/message-templates"><MessageTemplatesPage /></RoleRoute>} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
