import { useEffect, useMemo, useState } from 'react';
import { Button, Card, Form, Switch, Tag, Toast, Descriptions, Space, Typography, Divider } from '@douyinfe/semi-ui';
import { getCurrentTenant, updateCurrentTenant } from '@/api/tenant';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { useAppDispatch, useAppSelector } from '@/hooks/useAppDispatch';
import { usePermission } from '@/hooks/usePermission';
import { setUser } from '@/store/authSlice';
import { PERM } from '@/utils/permissions';
import {
  TENANT_FEATURE_DEFAULTS,
  TENANT_FEATURE_DISABLED_DEFAULTS,
  TENANT_FEATURE_LABEL_MAP,
  TENANT_FEATURE_OPTIONS,
} from '@/utils/featureDisplay';
import { EXPIRY_STATUS_META, TENANT_STATUS_META } from '@/utils/statusDisplay';
import type { TenantProfileVO } from '@/types';

const { Title, Text } = Typography;

export default function TenantPage() {
  const dispatch = useAppDispatch();
  const currentUser = useAppSelector((s) => s.auth.user);
  const { hasPermission } = usePermission();
  const canEditTenant = hasPermission(PERM.TENANT_EDIT);
  const [initialValues, setInitialValues] = useState<Partial<TenantProfileVO>>({});
  const [featureFlags, setFeatureFlags] = useState({ ...TENANT_FEATURE_DEFAULTS });
  const featureLabels = useMemo(
    () => (initialValues.enabledFeatures || []).map((key) => TENANT_FEATURE_LABEL_MAP[key] || key),
    [initialValues.enabledFeatures],
  );
  const tenantStatusMeta = TENANT_STATUS_META[initialValues.status ?? 0];
  const tenantSummary = useMemo(() => ([
    {
      key: '租户状态',
      value: <Tag color={tenantStatusMeta?.color ?? 'grey'}>{tenantStatusMeta?.text || initialValues.status || '-'}</Tag>,
    },
    { key: '套餐', value: initialValues.planType || '-' },
    { key: '用户配额', value: `${initialValues.currentUsers || 0} / ${initialValues.maxUsers || '-'}` },
    { key: '剩余席位', value: initialValues.remainingUserSlots ?? '-' },
    {
      key: '到期时间',
      value: (
        <Space spacing={8}>
          <span>{initialValues.expireTime || '-'}</span>
          {initialValues.expired && <Tag color={EXPIRY_STATUS_META.expired.color}>{EXPIRY_STATUS_META.expired.text}</Tag>}
        </Space>
      ),
    },
    {
      key: '已启用能力',
      value: featureLabels.length > 0
        ? <Space wrap>{featureLabels.map((item) => <Tag key={item} color="blue">{item}</Tag>)}</Space>
        : '-',
    },
  ]), [featureLabels, initialValues, tenantStatusMeta]);

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
        const flags = { ...TENANT_FEATURE_DISABLED_DEFAULTS };
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
    const payload = {
      ...(values as Partial<TenantProfileVO>),
      featureConfig: JSON.stringify(featureFlags),
    };
    await updateCurrentTenant(payload);
    setInitialValues((current) => ({ ...current, ...payload }));
    if (currentUser) {
      dispatch(setUser({
        ...currentUser,
        tenantName: String(payload.tenantName ?? currentUser.tenantName),
        themeColor: String(payload.themeColor ?? currentUser.themeColor ?? ''),
      }));
    }
    Toast.success('保存成功');
  };

  return (
    <div className="page-container">
      <PageHeader
        title="租户中心"
        description="维护当前租户的品牌、联系人和套餐信息"
      />
      <Card className="page-card-stack">
        <Descriptions data={tenantSummary} columns={3} />
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
          <Divider margin="24px" />
          <Form.Slot>
            <Space vertical align="start" spacing={12} className="page-section-stack">
              <Title heading={6}>功能开关</Title>
              {TENANT_FEATURE_OPTIONS.map((item) => (
                <div key={item.key} className="tenant-feature-item">
                  <Text>{item.label}</Text>
                  <Switch
                    checked={featureFlags[item.key as keyof typeof featureFlags]}
                    disabled={!canEditTenant}
                    onChange={(v) => setFeatureFlags((prev) => ({ ...prev, [item.key]: v }))}
                  />
                </div>
              ))}
            </Space>
          </Form.Slot>
          {canEditTenant && (
            <PageFormActions>
              <Button htmlType="submit" theme="solid" type="primary">保存</Button>
            </PageFormActions>
          )}
        </Form>
      </Card>
    </div>
  );
}
