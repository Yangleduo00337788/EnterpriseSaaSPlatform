import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Button, Typography, Card, Toast } from '@douyinfe/semi-ui';
import { IconUser, IconLock, IconHome } from '@douyinfe/semi-icons';
import { useAppDispatch } from '@/hooks/useAppDispatch';
import { login } from '@/store/authSlice';
import type { LoginForm } from '@/types';

const { Text } = Typography;

export default function LoginPage() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: LoginForm) => {
    setLoading(true);
    try {
      await dispatch(login(values)).unwrap();
      Toast.success('登录成功');
      navigate('/dashboard');
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    }}>
      <Card style={{ width: 420, padding: '20px 10px' }} shadows="always">
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <img src="/logo.png" alt="审流云" style={{ height: 56, objectFit: 'contain', marginBottom: 8 }} />
          <br />
          <Text type="tertiary">企业审批平台 · 让审批像流水一样高效</Text>
        </div>
        <Form onSubmit={handleSubmit} labelPosition="left" labelWidth={80}>
          <Form.Input
            field="tenantCode" label="企业编码" prefix={<IconHome />}
            rules={[{ required: true, message: '请输入企业编码' }]}
            placeholder="请输入企业编码"
          />
          <Form.Input
            field="username" label="用户名" prefix={<IconUser />}
            rules={[{ required: true, message: '请输入用户名' }]}
            placeholder="请输入用户名"
          />
          <Form.Input
            field="password" label="密码" type="password" prefix={<IconLock />}
            rules={[{ required: true, message: '请输入密码' }]}
            placeholder="请输入密码"
          />
          <Button htmlType="submit" type="primary" theme="solid" block loading={loading}
            style={{ marginTop: 16 }}>
            登 录
          </Button>
        </Form>
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Text>还没有账号？<Link to="/register">企业注册</Link></Text>
        </div>
      </Card>
    </div>
  );
}
