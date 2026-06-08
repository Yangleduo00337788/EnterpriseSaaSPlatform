import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Button, Typography, Card, Toast, Row, Col, Space } from '@douyinfe/semi-ui';
import { register } from '@/api/auth';
import type { RegisterForm } from '@/types';

const { Text, Title, Paragraph } = Typography;

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
                <Title heading={3} style={{ margin: 0 }}>企业注册</Title>
                <Text type="tertiary" style={{ marginTop: 8, display: 'block' }}>创建您的审流云企业账号</Text>
              </div>
          <Form onSubmit={handleSubmit} labelPosition="top">
            <div style={{ display: 'flex', gap: 16 }}>
              <Form.Input field="tenantName" label="企业名称"
                rules={[{ required: true, message: '请输入企业名称' }]} style={{ flex: 1 }} />
              <Form.Input field="tenantCode" label="企业编码"
                rules={[{ required: true, message: '请输入企业编码' }]}
                placeholder="2-32位字母数字" style={{ flex: 1 }} />
            </div>
            <div style={{ display: 'flex', gap: 16 }}>
              <Form.Input field="contactName" label="联系人"
                rules={[{ required: true, message: '请输入联系人' }]} style={{ flex: 1 }} />
              <Form.Input field="contactPhone" label="联系电话"
                rules={[{ required: true, message: '请输入联系电话' }]} style={{ flex: 1 }} />
            </div>
            <Form.Input field="contactEmail" label="联系邮箱" />
            <div style={{ display: 'flex', gap: 16 }}>
              <Form.Input field="realName" label="管理员姓名"
                rules={[{ required: true, message: '请输入管理员姓名' }]} style={{ flex: 1 }} />
              <Form.Input field="username" label="登录用户名"
                rules={[{ required: true, message: '请输入用户名' }]} style={{ flex: 1 }} />
            </div>
            <Form.Input field="password" label="登录密码" type="password"
              rules={[{ required: true, message: '请输入密码' }]} />
            <Button htmlType="submit" type="primary" theme="solid" block loading={loading}
              size="large" style={{ marginTop: 24, borderRadius: 4 }}>
              注 册
            </Button>
          </Form>
              <div style={{ textAlign: 'center' }}>
                <Text>已有账号？<Link to="/login" style={{ color: 'var(--semi-color-primary)', textDecoration: 'none', fontWeight: 600 }}>去登录</Link></Text>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
