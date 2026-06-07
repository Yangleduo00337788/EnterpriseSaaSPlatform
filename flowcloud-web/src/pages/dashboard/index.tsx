import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Button, Spin } from '@douyinfe/semi-ui';
import { IconPlus, IconList } from '@douyinfe/semi-icons';
import ReactECharts from 'echarts-for-react';
import { getDashboard } from '@/api/report';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import type { DashboardVO } from '@/types';

export default function DashboardPage() {
  const navigate = useNavigate();
  const { hasPermission } = usePermission();
  const { labelMap: categoryLabelMap } = useApprovalCategory();
  const [data, setData] = useState<DashboardVO | null>(null);
  const [loading, setLoading] = useState(true);

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
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: (data?.categoryStats || []).map((item) => ({
        name: categoryLabelMap[item.category] || item.category,
        value: item.count,
      })),
    }],
  };

  const stats = [
    { label: '审批总数', value: data?.totalInstances || 0, color: '#3370ff' },
    { label: '审批中', value: data?.pendingCount || 0, color: '#ff7d00' },
    { label: '已通过', value: data?.approvedCount || 0, color: '#00b42a' },
    ...(hasPermission(PERM.PENDING) ? [{ label: '待我审批', value: data?.myPendingTasks || 0, color: '#f53f3f' }] : []),
  ];

  const quickEntries = [
    { title: '发起请假', path: '/approval/submit?category=leave', color: '#3370ff', show: hasPermission(PERM.SUBMIT) },
    { title: '发起报销', path: '/approval/submit?category=expense', color: '#00b42a', show: hasPermission(PERM.SUBMIT) },
    { title: '我的申请', path: '/approval/my', color: '#ff7d00', show: hasPermission(PERM.MY) },
    { title: '流程模板', path: '/templates', color: '#722ed1', show: hasPermission(PERM.TEMPLATE) },
  ].filter((item) => item.show);

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2>工作台</h2>
          <p>欢迎回来，查看审批概览</p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          {hasPermission(PERM.SUBMIT) && (
            <Button icon={<IconPlus />} type="primary" onClick={() => navigate('/approval/submit')}>
              发起审批
            </Button>
          )}
          {hasPermission(PERM.PENDING) && (
            <Button icon={<IconList />} onClick={() => navigate('/approval/pending')}>
              待我审批
            </Button>
          )}
        </div>
      </div>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        {stats.map((stat) => (
          <Col span={24 / stats.length} key={stat.label}>
            <Card>
              <div className="stat-card">
                <div className="value" style={{ color: stat.color }}>{stat.value}</div>
                <div className="label">{stat.label}</div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Card title="审批类型分布">
            <ReactECharts option={chartOption} style={{ height: 300 }} />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="快捷入口">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, padding: 16 }}>
              {quickEntries.map((item) => (
                <div key={item.title} onClick={() => navigate(item.path)} style={{ cursor: 'pointer' }}>
                  <Card style={{ textAlign: 'center', borderTop: `3px solid ${item.color}` }}>
                    <div style={{ fontSize: 16, fontWeight: 600 }}>{item.title}</div>
                  </Card>
                </div>
              ))}
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
