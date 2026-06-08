import { useEffect, useState } from 'react';
import { Button, Card, Input, Select, Space, Tag, Toast, Typography } from '@douyinfe/semi-ui';
import { getStorageSettings, testStorageSettings, updateStorageSettings, type StorageSettingsVO } from '@/api/systemSettings';
import { PageHeader } from '@/components/page-kit';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';

const { Text } = Typography;

const STORAGE_OPTIONS = [
  { label: '本地存储', value: 'LOCAL' },
  { label: 'MinIO', value: 'MINIO' },
] as const;

const EMPTY_SETTINGS: StorageSettingsVO = {
  storageType: 'LOCAL',
  localPath: '',
  localBaseUrl: '',
  minioEndpoint: '',
  minioAccessKey: '',
  minioSecretKey: '',
  minioBucket: '',
  minioBaseUrl: '',
  minioConsoleUrl: '',
};

export default function SystemSettingsPage() {
  const { hasPermission } = usePermission();
  const canEdit = hasPermission(PERM.TENANT_EDIT);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);
  const [settings, setSettings] = useState<StorageSettingsVO>(EMPTY_SETTINGS);

  useEffect(() => {
    setLoading(true);
    getStorageSettings()
      .then((res) => setSettings(res.data))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (field: keyof StorageSettingsVO, value: string) => {
    setSettings((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      await updateStorageSettings(settings);
      Toast.success('系统存储设置已保存');
    } finally {
      setSaving(false);
    }
  };

  const handleTestMinio = async () => {
    setTesting(true);
    try {
      const res = await testStorageSettings({
        ...settings,
        storageType: 'MINIO',
      });
      Toast.success(res.data || 'MinIO 连通性测试通过');
    } finally {
      setTesting(false);
    }
  };

  return (
    <div className="page-container">
      <PageHeader
        title="系统设置"
        description="维护当前租户的文件存储策略，支持本地磁盘和 MinIO 两种方式"
        actions={(
          <Space wrap spacing={12}>
            <Tag color={settings.storageType === 'MINIO' ? 'green' : 'blue'}>
              当前策略: {settings.storageType === 'MINIO' ? 'MinIO' : '本地存储'}
            </Tag>
            <Button
              loading={testing}
              disabled={!canEdit}
              onClick={handleTestMinio}
            >
              测试 MinIO 连通性
            </Button>
            <Button theme="solid" type="primary" loading={saving} disabled={!canEdit} onClick={handleSave}>
              保存设置
            </Button>
          </Space>
        )}
      />

      <div className="profile-page-grid">
        <div className="profile-page-main-column">
          <Card title="存储策略" loading={loading} className="profile-page-card">
            <div className="profile-page-form-grid">
              <div className="profile-page-field profile-page-field-wide">
                <div className="profile-page-label">文件存储方式</div>
                <Select
                  value={settings.storageType}
                  optionList={[...STORAGE_OPTIONS]}
                  disabled={!canEdit}
                  onChange={(value) => handleChange('storageType', String(value) as StorageSettingsVO['storageType'])}
                />
              </div>
              <div className="profile-page-field profile-page-field-wide">
                <div className="profile-page-label">说明</div>
                <Text type="tertiary">
                  切换后，后续头像上传和审批附件上传会按照当前策略写入；已存在文件仍保留原存储位置。
                </Text>
              </div>
            </div>
          </Card>

          <Card title="本地存储配置" loading={loading} className="profile-page-card">
            <div className="profile-page-form-grid">
              <div className="profile-page-field profile-page-field-wide">
                <div className="profile-page-label">本地目录</div>
                <Input
                  value={settings.localPath}
                  placeholder="例如 D:/flowcloud-uploads"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('localPath', value)}
                />
              </div>
              <div className="profile-page-field profile-page-field-wide">
                <div className="profile-page-label">访问 Base URL</div>
                <Input
                  value={settings.localBaseUrl}
                  placeholder="例如 http://localhost:8080/uploads"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('localBaseUrl', value)}
                />
              </div>
            </div>
          </Card>

          <Card title="MinIO 配置" loading={loading} className="profile-page-card">
            <div className="profile-page-form-grid">
              <div className="profile-page-field">
                <div className="profile-page-label">Endpoint</div>
                <Input
                  value={settings.minioEndpoint}
                  placeholder="例如 http://localhost:9000"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('minioEndpoint', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">Bucket</div>
                <Input
                  value={settings.minioBucket}
                  placeholder="例如 flowcloud"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('minioBucket', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">Access Key</div>
                <Input
                  value={settings.minioAccessKey}
                  placeholder="请输入 Access Key"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('minioAccessKey', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">Secret Key</div>
                <Input
                  value={settings.minioSecretKey}
                  type="password"
                  placeholder="请输入 Secret Key"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('minioSecretKey', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">对外访问 Base URL</div>
                <Input
                  value={settings.minioBaseUrl}
                  placeholder="为空时默认使用 Endpoint"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('minioBaseUrl', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">控制台地址</div>
                <Input
                  value={settings.minioConsoleUrl}
                  placeholder="例如 http://localhost:9001"
                  disabled={!canEdit}
                  onChange={(value) => handleChange('minioConsoleUrl', value)}
                />
              </div>
            </div>
          </Card>
        </div>

        <div className="profile-page-side-column">
          <Card title="使用建议" className="profile-page-card">
            <div className="profile-page-info-list">
              <div className="profile-page-info-item">
                <div className="profile-page-info-label">本地存储</div>
                <div className="profile-page-info-value">适合单机部署或内网场景，配置简单，依赖最少。</div>
              </div>
              <div className="profile-page-info-item">
                <div className="profile-page-info-label">MinIO</div>
                <div className="profile-page-info-value">适合生产环境统一对象存储，便于扩展、迁移和备份。</div>
              </div>
              <div className="profile-page-info-item">
                <div className="profile-page-info-label">生效范围</div>
                <div className="profile-page-info-value">当前租户后续上传的头像和附件。</div>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
