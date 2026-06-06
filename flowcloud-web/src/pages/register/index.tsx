import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Button, Typography, Card, Toast } from '@douyinfe/semi-ui';
import { register } from '@/api/auth';
import type { RegisterForm } from '@/types';

const { Text } = Typography;

export default function RegisterPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: RegisterForm) => {
    setLoading(true);
    try {
      await register(values);
      Toast.success('注册成功，请登录');
      navigate('/login');
    } catch {
      // handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', padding: 24,
    }}>
      <Card style={{ width: 520, padding: '20px 10px' }} shadows="always">
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <img src="/logo.png" alt="审流云" style={{ height: 48, objectFit: 'contain', marginBottom: 8 }} />
          <br />
          <Text type="tertiary">创建您的审流云企业账号</Text>
        </div>
        <Form onSubmit={handleSubmit} labelPosition="left" labelWidth={100}>
          <Form.Input field="tenantName" label="企业名称"
            rules={[{ required: true, message: '请输入企业名称' }]} />
          <Form.Input field="tenantCode" label="企业编码"
            rules={[{ required: true, message: '请输入企业编码' }]}
            placeholder="2-32位字母数字" />
          <Form.Input field="contactName" label="联系人"
            rules={[{ required: true, message: '请输入联系人' }]} />
          <Form.Input field="contactPhone" label="联系电话"
            rules={[{ required: true, message: '请输入联系电话' }]} />
          <Form.Input field="contactEmail" label="联系邮箱" />
          <Form.Input field="realName" label="管理员姓名"
            rules={[{ required: true, message: '请输入管理员姓名' }]} />
          <Form.Input field="username" label="登录用户名"
            rules={[{ required: true, message: '请输入用户名' }]} />
          <Form.Input field="password" label="登录密码" type="password"
            rules={[{ required: true, message: '请输入密码' }]} />
          <Button htmlType="submit" type="primary" theme="solid" block loading={loading}
            style={{ marginTop: 16 }}>
            注 册
          </Button>
        </Form>
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Text>已有账号？<Link to="/login">去登录</Link></Text>
        </div>
      </Card>
    </div>
  );
}
