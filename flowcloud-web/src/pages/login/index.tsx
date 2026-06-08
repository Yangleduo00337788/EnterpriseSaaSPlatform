import { useMemo, useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { Form, Button, Typography, Card, Toast, Row, Col, Space } from '@douyinfe/semi-ui';
import { IconUser, IconLock, IconHome } from '@douyinfe/semi-icons';
import { useAppDispatch } from '@/hooks/useAppDispatch';
import { fetchCurrentMenus, login } from '@/store/authSlice';
import type { LoginForm } from '@/types';
import { findFirstMenuPath } from '@/utils/menuTree';
import { getPreferredTenantCode, getRecentTenantByCode } from '@/utils/tenantSession';

const { Text, Title, Paragraph } = Typography;

export default function LoginPage() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [loading, setLoading] = useState(false);
  const [searchParams] = useSearchParams();

  const initialValues = useMemo(() => {
    const tenantCode = searchParams.get('tenantCode') ?? getPreferredTenantCode();
    const recentTenant = getRecentTenantByCode(tenantCode);
    return {
      tenantCode,
      username: recentTenant?.username ?? '',
    };
  }, [searchParams]);

  const handleSubmit = async (values: LoginForm) => {
    setLoading(true);
    try {
      await dispatch(login(values)).unwrap();
      const menus = await dispatch(fetchCurrentMenus()).unwrap();
      Toast.success('登录成功');
      navigate(findFirstMenuPath(menus) || '/dashboard');
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="semi-auth-page">
      <Row className="semi-auth-layout" align="middle">
        <Col span={12} className="semi-auth-side">
          <Space vertical spacing={16} align="start">
            <Space spacing={16} align="center">
              <img src="/logo.png" alt="审流云" className="semi-auth-logo" />
              <Title heading={2} style={{ margin: 0 }}>审流云</Title>
            </Space>
            <Title heading={3} style={{ margin: 0 }}>让审批像流水一样高效</Title>
            <Paragraph className="semi-auth-desc">
              极简、专业、安全的企业级审批管理平台。支持多租户隔离、自定义工作流、数据分析与审计，全面提升企业协同效率。
            </Paragraph>
          </Space>
        </Col>
        <Col span={12} className="semi-auth-main">
          <Card className="semi-auth-card">
            <Space vertical spacing={24} style={{ width: '100%' }}>
              <div style={{ textAlign: 'center' }}>
                <Title heading={3} style={{ margin: 0 }}>欢迎登录</Title>
                <Text type="tertiary" style={{ marginTop: 8, display: 'block' }}>请输入您的账号信息</Text>
              </div>
          <Form
            key={`${initialValues.tenantCode}-${initialValues.username}`}
            initValues={initialValues}
            onSubmit={handleSubmit}
            labelPosition="top"
          >
            <Form.Input
              field="tenantCode" label="企业编码" prefix={<IconHome />}
              rules={[{ required: true, message: '请输入企业编码' }]}
              placeholder="请输入企业编码"
              size="large"
            />
            <Form.Input
              field="username" label="用户名" prefix={<IconUser />}
              rules={[{ required: true, message: '请输入用户名' }]}
              placeholder="请输入用户名"
              size="large"
            />
            <Form.Input
              field="password" label="密码" type="password" prefix={<IconLock />}
              rules={[{ required: true, message: '请输入密码' }]}
              placeholder="请输入密码"
              size="large"
            />
            <Button htmlType="submit" type="primary" theme="solid" block loading={loading}
              size="large" style={{ marginTop: 24, borderRadius: 4 }}>
              登 录
            </Button>
          </Form>
              <div style={{ textAlign: 'center' }}>
                <Text>还没有账号？<Link to="/register" style={{ color: 'var(--semi-color-primary)', textDecoration: 'none', fontWeight: 600 }}>企业注册</Link></Text>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
