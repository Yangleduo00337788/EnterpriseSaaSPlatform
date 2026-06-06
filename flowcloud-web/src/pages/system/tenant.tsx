import { useEffect, useMemo, useState } from 'react';
import { Button, Card, Form, Switch, Tag, Toast } from '@douyinfe/semi-ui';
import { getCurrentTenant, updateCurrentTenant } from '@/api/tenant';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import type { TenantProfileVO } from '@/types';

export default function TenantPage() {
  const { hasPermission } = usePermission();
  const canEditTenant = hasPermission(PERM.TENANT_EDIT);
  const [initialValues, setInitialValues] = useState<Partial<TenantProfileVO>>({});
  const [featureFlags, setFeatureFlags] = useState({
    approval: true,
    report: true,
    message: true,
    tenantSettings: true,
  });
  const featureLabels = useMemo(() => {
    const mapping: Record<string, string> = {
      approval: '审批流',
      report: '报表分析',
      message: '消息中心',
      tenantSettings: '租户设置',
    };
    return (initialValues.enabledFeatures || []).map((key) => mapping[key] || key);
  }, [initialValues.enabledFeatures]);

  useEffect(() => {
    getCurrentTenant().then((res) => {
      setInitialValues(res.data);
      if (res.data.featureConfig) {
        try {
          const parsed = JSON.parse(res.data.featureConfig) as Record<string, boolean>;
          setFeatureFlags((prev) => ({ ...prev, ...parsed }));
        } catch {
          // ignore invalid json
        }
      } else if (res.data.enabledFeatures) {
        const flags = { approval: false, report: false, message: false, tenantSettings: false };
        res.data.enabledFeatures.forEach((key) => {
          if (key in flags) flags[key as keyof typeof flags] = true;
        });
        setFeatureFlags(flags);
      }
    });
  }, []);

  const handleSubmit = async (values: Record<string, unknown>) => {
    if (!canEditTenant) {
      return;
    }
    await updateCurrentTenant({
      ...(values as Partial<TenantProfileVO>),
      featureConfig: JSON.stringify(featureFlags),
    });
    Toast.success('保存成功');
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>租户中心</h2>
        <p>维护当前租户的品牌、联系人和套餐信息</p>
      </div>
      <Card style={{ marginBottom: 16 }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: 16 }}>
          <div>
            <div style={{ color: '#86909c', fontSize: 12 }}>租户状态</div>
            <div style={{ marginTop: 8 }}>
              <Tag color={initialValues.status === 1 ? 'green' : 'red'}>
                {initialValues.status === 1 ? '启用中' : '已停用'}
              </Tag>
            </div>
          </div>
          <div>
            <div style={{ color: '#86909c', fontSize: 12 }}>套餐</div>
            <div style={{ marginTop: 8, fontWeight: 600 }}>{initialValues.planType || '-'}</div>
          </div>
          <div>
            <div style={{ color: '#86909c', fontSize: 12 }}>用户配额</div>
            <div style={{ marginTop: 8, fontWeight: 600 }}>
              {initialValues.currentUsers || 0} / {initialValues.maxUsers || '-'}
            </div>
          </div>
          <div>
            <div style={{ color: '#86909c', fontSize: 12 }}>剩余席位</div>
            <div style={{ marginTop: 8, fontWeight: 600 }}>{initialValues.remainingUserSlots ?? '-'}</div>
          </div>
        </div>
        <div style={{ marginTop: 16, display: 'flex', gap: 24, flexWrap: 'wrap' }}>
          <div>
            <span style={{ color: '#86909c', marginRight: 8 }}>到期时间</span>
            <span>{initialValues.expireTime || '-'}</span>
            {initialValues.expired && <Tag color="red" style={{ marginLeft: 8 }}>已到期</Tag>}
          </div>
          <div>
            <span style={{ color: '#86909c', marginRight: 8 }}>已启用能力</span>
            {featureLabels.length > 0 ? featureLabels.map((item) => <Tag key={item} color="blue" style={{ marginRight: 8 }}>{item}</Tag>) : '-'}
          </div>
        </div>
      </Card>
      <Card>
        <Form key={initialValues.id || 0} initValues={initialValues} onSubmit={handleSubmit}>
          <Form.Input field="tenantCode" label="企业编码" disabled />
          <Form.Input field="tenantName" label="企业名称" rules={[{ required: true, message: '必填' }]} disabled={!canEditTenant} />
          <Form.Input field="contactName" label="联系人" disabled={!canEditTenant} />
          <Form.Input field="contactPhone" label="联系电话" disabled={!canEditTenant} />
          <Form.Input field="contactEmail" label="联系邮箱" disabled={!canEditTenant} />
          <Form.Input field="logo" label="Logo URL" disabled={!canEditTenant} />
          <Form.Input field="themeColor" label="主题色" disabled={!canEditTenant} />
          <Form.Input field="planType" label="套餐类型" disabled={!canEditTenant} />
          <Form.InputNumber field="maxUsers" label="最大用户数" disabled={!canEditTenant} />
          <Form.Input field="expireTime" label="到期时间" placeholder="yyyy-MM-dd HH:mm:ss" disabled={!canEditTenant} />
          <Form.TextArea field="packageConfig" label="套餐配置JSON" disabled={!canEditTenant} />
          <div style={{ marginBottom: 16 }}>
            <div style={{ fontWeight: 600, marginBottom: 8 }}>功能开关</div>
            {[
              { key: 'approval', label: '审批流' },
              { key: 'report', label: '报表分析' },
              { key: 'message', label: '消息中心' },
              { key: 'tenantSettings', label: '租户设置' },
            ].map((item) => (
              <div key={item.key} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                <span>{item.label}</span>
                <Switch
                  checked={featureFlags[item.key as keyof typeof featureFlags]}
                  disabled={!canEditTenant}
                  onChange={(v) => setFeatureFlags((prev) => ({ ...prev, [item.key]: v }))}
                />
              </div>
            ))}
          </div>
          {canEditTenant && <Button htmlType="submit" type="primary">保存</Button>}
        </Form>
      </Card>
    </div>
  );
}