import { useState } from 'react';
import type { CSSProperties } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Button, Spin, Avatar, Tag } from '@douyinfe/semi-ui';
import { IconPlus, IconList } from '@douyinfe/semi-icons';
import ReactECharts from 'echarts-for-react';
import { getDashboard } from '@/api/report';
import { PageHeader } from '@/components/page-kit';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import { useAppSelector } from '@/hooks/useAppDispatch';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import type { DashboardVO } from '@/types';

export default function DashboardPage() {
  const navigate = useNavigate();
  const user = useAppSelector((s) => s.auth.user);
  const { hasPermission } = usePermission();
  const { labelMap: categoryLabelMap } = useApprovalCategory();
  const [data, setData] = useState<DashboardVO | null>(null);
  const [loading, setLoading] = useState(true);
  const welcomeName = user?.realName?.trim() || user?.username?.trim() || '用户';
  const welcomePositionName = user?.jobTitle?.trim() || '';

  useRouteRefresh(() => {
    setLoading(true);
    getDashboard().then((res) => {
      setData(res.data);
    }).catch(() => {
      setData(null);
    }).finally(() => setLoading(false));
  });

  if (loading) return <Spin style={{ display: 'block', margin: '100px auto' }} />;

  const chartOption = {
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'vertical',
      left: 0,
      top: 'middle',
      textStyle: { color: 'var(--semi-color-text-1)' },
    },
    series: [{
      type: 'pie',
      radius: ['46%', '74%'],
      data: (data?.categoryStats || []).map((item) => ({
        name: categoryLabelMap[item.category] || item.category,
        value: item.count,
      })),
      itemStyle: {
        borderRadius: 8,
        borderColor: 'var(--semi-color-bg-0)',
        borderWidth: 2
      },
      label: {
        color: 'var(--semi-color-text-1)'
      }
    }],
  };

  const stats = [
    { label: '审批总数', value: data?.totalInstances || 0, color: 'var(--semi-color-primary)' },
    { label: '审批中', value: data?.pendingCount || 0, color: 'var(--semi-color-warning)' },
    { label: '已通过', value: data?.approvedCount || 0, color: 'var(--semi-color-success)' },
    ...(hasPermission(PERM.PENDING) ? [{ label: '待我审批', value: data?.myPendingTasks || 0, color: 'var(--semi-color-danger)' }] : []),
  ];

  const quickEntries = [
    {
      title: '发起请假',
      description: '快速进入请假审批发起流程',
      path: '/approval/submit?category=leave',
      color: 'var(--semi-color-primary)',
      show: hasPermission(PERM.SUBMIT)
    },
    {
      title: '发起报销',
      description: '快速进入报销审批发起流程',
      path: '/approval/submit?category=expense',
      color: 'var(--semi-color-success)',
      show: hasPermission(PERM.SUBMIT)
    },
    {
      title: '我的申请',
      description: '查看当前账号提交的审批记录',
      path: '/approval/my',
      color: 'var(--semi-color-warning)',
      show: hasPermission(PERM.MY)
    },
    {
      title: '流程模板',
      description: '查看和维护现有审批流程模板',
      path: '/templates',
      color: 'var(--semi-color-info)',
      show: hasPermission(PERM.TEMPLATE)
    },
  ].filter((item) => item.show);
  const systemInfoItems = [
    { label: '系统名称', value: '审流云企业审批平台' },
    { label: '系统版本', value: 'v1.0.0' },
    { label: '前端框架', value: 'React 18 + Vite 6 + Semi UI' },
    { label: '后端框架', value: 'Spring Boot 3.3.5 + MyBatis-Flex 1.9.7' },
    { label: '数据库', value: 'MySQL 8.x' },
    { label: '缓存', value: 'Redis 7.x' },
    { label: '运行环境', value: 'JDK 21 + Maven 多模块单体架构' },
    { label: '核心能力', value: 'RBAC、审批流、消息通知、报表分析、租户中心' },
  ];
  const authorLinks = [
    {
      label: 'Gitee',
      type: 'link',
      value: 'https://gitee.com/yangleduo7788',
      displayValue: 'gitee.com/yangleduo7788',
    },
    {
      label: '微信',
      type: 'qr',
      value: '鼠标移入查看二维码',
      qrTitle: '微信二维码',
      qrImage: '/author/wechat-qr.png',
    },
    {
      label: 'QQ',
      type: 'qr',
      value: '鼠标移入查看二维码',
      qrTitle: 'QQ 二维码',
      qrImage: '/author/qq-qr.png',
    },
  ];

  return (
    <div className="page-container">
      <PageHeader
        className="dashboard-page-header"
        title={(
          <span className="dashboard-title-row">
            <span className="dashboard-welcome">
              <span className="dashboard-welcome-prefix">欢迎登录，</span>
              <span className="dashboard-welcome-name-badge">
                {welcomePositionName ? (
                  <span className="dashboard-welcome-name-label">{welcomePositionName}</span>
                ) : null}
                <span className="dashboard-welcome-name">{welcomeName}</span>
              </span>
            </span>
          </span>
        )}
        actions={(
          <>
          {hasPermission(PERM.SUBMIT) && (
            <Button
              size="large"
              icon={<IconPlus />}
              type="primary"
              theme="solid"
              className="page-toolbar-button"
              onClick={() => navigate('/approval/submit')}
            >
              发起审批
            </Button>
          )}
          {hasPermission(PERM.PENDING) && (
            <Button
              size="large"
              type="tertiary"
              theme="light"
              icon={<IconList />}
              className="page-toolbar-button page-toolbar-button-secondary"
              onClick={() => navigate('/approval/pending')}
            >
              待我审批
            </Button>
          )}
          </>
        )}
      />

      <Row gutter={[18, 18]} style={{ marginBottom: 20 }} className="dashboard-stat-row">
        {stats.map((stat) => (
          <Col span={24 / stats.length} key={stat.label}>
            <Card className="dashboard-stat-panel">
              <div className="stat-card semi-stat-card" style={{ border: 'none', background: 'transparent' }}>
                <div className="value" style={{ color: stat.color }}>{stat.value}</div>
                <div className="label">{stat.label}</div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[18, 18]} className="dashboard-content-row">
        <Col span={12}>
          <Card title="审批类型分布" className="dashboard-section-card dashboard-chart-card">
            <div className="dashboard-chart-shell">
              <ReactECharts option={chartOption} style={{ height: 248, width: '100%' }} />
            </div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="快捷入口" className="dashboard-section-card dashboard-quick-card">
            <div className="semi-quick-grid">
              {quickEntries.map((item) => (
                <Button
                  key={item.title}
                  theme="light"
                  type="tertiary"
                  size="large"
                  block
                  className="semi-quick-action"
                  style={{ '--quick-action-accent': item.color } as CSSProperties}
                  onClick={() => navigate(item.path)}
                >
                  <span className="semi-quick-action-inner">
                    <span className="semi-quick-action-title">{item.title}</span>
                    <span className="semi-quick-action-desc">{item.description}</span>
                  </span>
                </Button>
              ))}
            </div>
          </Card>
        </Col>
      </Row>

      <Row gutter={[18, 18]} style={{ marginTop: 18 }} className="dashboard-bottom-row">
        <Col span={12}>
          <Card title="系统信息" className="dashboard-section-card dashboard-info-card">
            <div className="dashboard-info-grid">
              {systemInfoItems.map((item) => (
                <div key={item.label} className="dashboard-info-item">
                  <div className="dashboard-info-label">{item.label}</div>
                  <div className="dashboard-info-value">{item.value}</div>
                </div>
              ))}
            </div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="关于作者" className="dashboard-section-card dashboard-author-panel">
            <div className="dashboard-author-card">
              <div className="dashboard-author-header">
                <Avatar size="large" color="blue" src="/author/avatar.jpg">养</Avatar>
                <div className="dashboard-author-copy">
                  <div className="dashboard-author-name">养乐多</div>
                  <div className="dashboard-author-intro">
                    个人开发者独立开发，专注企业级业务系统建设，具备全栈开发能力，覆盖产品设计、前后端实现、系统落地与持续迭代。
                  </div>
                </div>
              </div>
              <div className="dashboard-author-tags">
                <Tag color="blue">独立开发</Tag>
                <Tag color="green">全栈开发</Tag>
                <Tag color="cyan">SaaS 平台</Tag>
              </div>
              <div className="dashboard-author-links">
                {authorLinks.map((item) => (
                  <div key={item.label} className="dashboard-author-link-item">
                    <span className="dashboard-author-link-label">{item.label}</span>
                    {item.type === 'link' ? (
                      <a
                        className="dashboard-author-link-value dashboard-author-link-anchor"
                        href={item.value}
                        target="_blank"
                        rel="noreferrer"
                      >
                        {item.displayValue}
                      </a>
                    ) : (
                      <div className="dashboard-author-link-hover">
                        <span className="dashboard-author-link-value dashboard-author-link-hover-trigger">
                          {item.value}
                        </span>
                        <div className="dashboard-author-qr-panel" role="tooltip">
                          <img src={item.qrImage} alt={item.qrTitle} className="dashboard-author-qr-image" />
                          <div className="dashboard-author-qr-title">{item.qrTitle}</div>
                          <div className="dashboard-author-qr-tip">欢迎扫码联系开发者！</div>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
              <div className="dashboard-author-system">
                <div className="dashboard-author-system-title">系统介绍</div>
                <div className="dashboard-author-system-copy">
                  审流云是一套面向企业审批协同场景的后台平台，聚焦审批流转、组织权限、消息触达与经营分析，适用于 OA、CRM、ERP 等 SaaS 化办公业务。
                </div>
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
