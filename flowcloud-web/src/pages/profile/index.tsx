import { useEffect, useMemo, useRef, useState } from 'react';
import type { ChangeEvent } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import { Avatar, Button, Card, Input, Space, Switch, Tag, Toast, Typography } from '@douyinfe/semi-ui';
import { IconBell, IconMoon, IconSetting, IconSun } from '@douyinfe/semi-icons';
import { changeCurrentPassword, updateCurrentProfile, uploadCurrentUserAvatar } from '@/api/auth';
import { PageHeader } from '@/components/page-kit';
import type { MainLayoutOutletContext } from '@/layouts/MainLayout';
import { useAppDispatch, useAppSelector } from '@/hooks/useAppDispatch';
import { setUser } from '@/store/authSlice';

const { Text } = Typography;

const AVATAR_MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

interface ProfileFormState {
  realName: string;
  phone: string;
  email: string;
  avatar: string;
}

interface PasswordFormState {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export default function ProfilePage() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.auth.user);
  const { themeMode, applyTheme, messageAutoRefresh, handleMessageAutoRefreshChange } = useOutletContext<MainLayoutOutletContext>();
  const [profileSaving, setProfileSaving] = useState(false);
  const [passwordSaving, setPasswordSaving] = useState(false);
  const [avatarUploading, setAvatarUploading] = useState(false);
  const [profileForm, setProfileForm] = useState<ProfileFormState>({
    realName: '',
    phone: '',
    email: '',
    avatar: '',
  });
  const [passwordForm, setPasswordForm] = useState<PasswordFormState>({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const avatarInputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    setProfileForm({
      realName: user?.realName || '',
      phone: user?.phone || '',
      email: user?.email || '',
      avatar: user?.avatar || '',
    });
  }, [user?.avatar, user?.email, user?.phone, user?.realName]);

  const accountItems = useMemo(() => ([
    { label: '所属企业', value: user?.tenantName || '-' },
    { label: '租户编码', value: user?.tenantCode || '-' },
    { label: '用户账号', value: user?.username || '-' },
    { label: '用户编号', value: user?.userId ?? '-' },
    { label: '岗位名称', value: user?.jobTitle || '-' },
  ]), [user]);

  const handleProfileFieldChange = (field: keyof ProfileFormState, value: string) => {
    setProfileForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handlePasswordFieldChange = (field: keyof PasswordFormState, value: string) => {
    setPasswordForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleProfileSave = async () => {
    const realName = profileForm.realName.trim();
    if (!realName) {
      Toast.warning('姓名不能为空');
      return;
    }

    setProfileSaving(true);
    try {
      const res = await updateCurrentProfile({
        realName,
        phone: profileForm.phone.trim() || undefined,
        email: profileForm.email.trim() || undefined,
        avatar: profileForm.avatar.trim() || undefined,
      });
      dispatch(setUser(res.data));
      Toast.success('个人资料已保存');
    } finally {
      setProfileSaving(false);
    }
  };

  const handleAvatarButtonClick = () => {
    avatarInputRef.current?.click();
  };

  const handleAvatarChange = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) {
      return;
    }
    if (file.size > AVATAR_MAX_UPLOAD_SIZE) {
      Toast.warning('头像文件不能超过 5MB，请压缩后再上传');
      return;
    }

    setAvatarUploading(true);
    try {
      const res = await uploadCurrentUserAvatar(file);
      setProfileForm((current) => ({
        ...current,
        avatar: res.data.avatarUrl,
      }));
      Toast.success('头像上传成功，保存资料后生效');
    } finally {
      setAvatarUploading(false);
    }
  };

  const handleResetAvatar = () => {
    setProfileForm((current) => ({
      ...current,
      avatar: '',
    }));
    Toast.info('已标记恢复默认头像，保存资料后生效');
  };

  const handlePasswordSave = async () => {
    const oldPassword = passwordForm.oldPassword.trim();
    const newPassword = passwordForm.newPassword.trim();
    const confirmPassword = passwordForm.confirmPassword.trim();

    if (!oldPassword || !newPassword || !confirmPassword) {
      Toast.warning('请完整填写密码信息');
      return;
    }
    if (newPassword.length < 6) {
      Toast.warning('新密码长度不能少于 6 位');
      return;
    }
    if (newPassword !== confirmPassword) {
      Toast.warning('两次输入的新密码不一致');
      return;
    }

    setPasswordSaving(true);
    try {
      await changeCurrentPassword({ oldPassword, newPassword });
      setPasswordForm({
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
      Toast.success('密码已修改，下次登录请使用新密码');
    } finally {
      setPasswordSaving(false);
    }
  };

  return (
    <div className="page-container profile-page-container">
      <PageHeader
        title="个人信息"
        description="在主区域集中查看并维护个人资料、账号安全与使用偏好"
        actions={(
          <Space wrap spacing={12}>
            <Button type="tertiary" theme="light" icon={<IconSetting />} onClick={() => navigate('/system/tenant')}>
              租户中心
            </Button>
            <Button type="primary" theme="solid" loading={profileSaving} onClick={handleProfileSave}>
              保存资料
            </Button>
          </Space>
        )}
      />

      <Card className="profile-page-hero-card" bodyStyle={{ padding: 0 }}>
        <div className="profile-page-hero">
          <div className="profile-page-hero-main">
            <Avatar size={84} color="blue" src={profileForm.avatar || user?.avatar}>
              {profileForm.realName?.[0] || user?.username?.[0] || 'U'}
            </Avatar>
            <div className="profile-page-hero-copy">
              <div className="profile-page-hero-name-row">
                <span className="profile-page-hero-name">{profileForm.realName || '未命名用户'}</span>
                <Tag color="blue">{user?.tenantName || '未绑定企业'}</Tag>
              </div>
              <Text type="tertiary">{user?.username || '-'}</Text>
              <Text type="tertiary">{user?.jobTitle || '未设置岗位'}</Text>
              <Text type="tertiary">当前页面已替代头像下拉中的弹窗展示，可在一个屏幕内查看完整个人信息。</Text>
            </div>
          </div>
          <div className="profile-page-hero-actions">
            <Space direction="vertical" spacing={12}>
              <Button type="primary" theme="solid" loading={avatarUploading} onClick={handleAvatarButtonClick}>
                上传头像
              </Button>
              <Button
                theme="light"
                type="tertiary"
                disabled={!profileForm.avatar && !user?.avatar}
                onClick={handleResetAvatar}
              >
                恢复默认头像
              </Button>
              <Space spacing={8} wrap>
                <Tag color="blue">仅支持图片</Tag>
                <Tag color="grey">单文件最大 5MB</Tag>
              </Space>
              <Text type="tertiary">支持 JPG/PNG，上传后点击“保存资料”生效，超出 5MB 会在选择后直接提示。</Text>
            </Space>
          </div>
        </div>
      </Card>

      <input
        ref={avatarInputRef}
        type="file"
        accept="image/*"
        className="profile-page-file-input"
        onChange={handleAvatarChange}
      />

      <div className="profile-page-grid">
        <div className="profile-page-main-column">
          <Card title="基础资料" className="profile-page-card">
            <div className="profile-page-form-grid">
              <div className="profile-page-field">
                <div className="profile-page-label">姓名</div>
                <Input
                  value={profileForm.realName}
                  placeholder="请输入姓名"
                  maxLength={32}
                  onChange={(value) => handleProfileFieldChange('realName', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">手机号</div>
                <Input
                  value={profileForm.phone}
                  placeholder="请输入手机号"
                  maxLength={20}
                  onChange={(value) => handleProfileFieldChange('phone', value)}
                />
              </div>
              <div className="profile-page-field profile-page-field-wide">
                <div className="profile-page-label">邮箱</div>
                <Input
                  value={profileForm.email}
                  placeholder="请输入邮箱"
                  maxLength={64}
                  onChange={(value) => handleProfileFieldChange('email', value)}
                />
              </div>
            </div>
          </Card>

          <Card title="安全设置" className="profile-page-card">
            <div className="profile-page-password-grid">
              <div className="profile-page-field">
                <div className="profile-page-label">原密码</div>
                <Input
                  value={passwordForm.oldPassword}
                  type="password"
                  placeholder="请输入原密码"
                  maxLength={32}
                  onChange={(value) => handlePasswordFieldChange('oldPassword', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">新密码</div>
                <Input
                  value={passwordForm.newPassword}
                  type="password"
                  placeholder="至少 6 位"
                  maxLength={32}
                  onChange={(value) => handlePasswordFieldChange('newPassword', value)}
                />
              </div>
              <div className="profile-page-field">
                <div className="profile-page-label">确认新密码</div>
                <Input
                  value={passwordForm.confirmPassword}
                  type="password"
                  placeholder="请再次输入新密码"
                  maxLength={32}
                  onChange={(value) => handlePasswordFieldChange('confirmPassword', value)}
                />
              </div>
            </div>
            <div className="profile-page-card-actions">
              <Button theme="solid" type="primary" loading={passwordSaving} onClick={handlePasswordSave}>
                修改密码
              </Button>
            </div>
          </Card>

          <Card title="偏好设置" className="profile-page-card">
            <div className="profile-page-preference-list">
              <div className="profile-page-preference-item">
                <div className="profile-page-preference-copy">
                  <div className="profile-page-preference-title">
                    <IconMoon className="profile-page-preference-icon" />
                    <span>深色模式</span>
                  </div>
                  <div className="profile-page-preference-description">控制后台整体亮色和暗色外观，与顶部快捷切换保持同步。</div>
                </div>
                <Switch checked={themeMode === 'dark'} onChange={(checked) => applyTheme(checked ? 'dark' : 'light')} />
              </div>
              <div className="profile-page-preference-item">
                <div className="profile-page-preference-copy">
                  <div className="profile-page-preference-title">
                    <IconBell className="profile-page-preference-icon" />
                    <span>消息角标自动刷新</span>
                  </div>
                  <div className="profile-page-preference-description">开启后每 30 秒自动刷新未读消息数，关闭后仅在进入页面时刷新。</div>
                </div>
                <Switch checked={messageAutoRefresh} onChange={handleMessageAutoRefreshChange} />
              </div>
              <div className="profile-page-preference-item profile-page-preference-item-readonly">
                <div className="profile-page-preference-copy">
                  <div className="profile-page-preference-title">
                    {themeMode === 'dark' ? <IconSun className="profile-page-preference-icon" /> : <IconMoon className="profile-page-preference-icon" />}
                    <span>当前主题状态</span>
                  </div>
                  <div className="profile-page-preference-description">当前生效主题为 {themeMode === 'dark' ? '深色模式' : '浅色模式'}。</div>
                </div>
                <Tag color={themeMode === 'dark' ? 'purple' : 'blue'}>{themeMode === 'dark' ? 'Dark' : 'Light'}</Tag>
              </div>
            </div>
          </Card>
        </div>

        <div className="profile-page-side-column">
          <Card title="账号信息" className="profile-page-card">
            <div className="profile-page-info-list">
              {accountItems.map((item) => (
                <div key={item.label} className="profile-page-info-item">
                  <div className="profile-page-info-label">{item.label}</div>
                  <div className="profile-page-info-value">{item.value}</div>
                </div>
              ))}
            </div>
          </Card>

          <Card title="角色与能力" className="profile-page-card">
            <div className="profile-page-meta-group">
              <div className="profile-page-meta-title">角色列表</div>
              <div className="profile-page-tag-list">
                {user?.roles?.length ? user.roles.map((role) => <Tag key={role}>{role}</Tag>) : <span>-</span>}
              </div>
            </div>
            <div className="profile-page-meta-group">
              <div className="profile-page-meta-title">已启用能力</div>
              <div className="profile-page-tag-list">
                {user?.enabledFeatures?.length ? user.enabledFeatures.map((feature) => <Tag key={feature} color="green">{feature}</Tag>) : <span>默认全部启用</span>}
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
