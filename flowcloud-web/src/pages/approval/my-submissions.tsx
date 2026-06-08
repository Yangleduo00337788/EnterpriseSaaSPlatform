import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Table, Tag, Button, Card, Select, Space, Form,
} from '@douyinfe/semi-ui';
import { getMySubmissions, cancelInstance } from '@/api/approval';
import { PageActionGroup, PageFilterCard, PageHeader } from '@/components/page-kit';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import { useApprovalStatus } from '@/hooks/useApprovalStatus';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import type { InstanceVO } from '@/types';

export default function MySubmissionsPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<InstanceVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [status, setStatus] = useState<string>();
  const { options: statusOptions, getStatusMeta } = useApprovalStatus();
  const { labelMap: categoryLabelMap } = useApprovalCategory();

  const fetchData = async (page = pageNum, nextStatus = status) => {
    setLoading(true);
    try {
      const res = await getMySubmissions({ status: nextStatus || undefined, pageNum: page, pageSize: 10 });
      setData(res.data.records);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData(pageNum));

  useEffect(() => { fetchData(pageNum); }, [pageNum]);

  const columns = [
    { title: '审批单号', dataIndex: 'instanceNo', width: 180 },
    {
      title: '标题', dataIndex: 'title',
      render: (value: string) => <span className="page-table-text-ellipsis" title={value}>{value}</span>,
    },
    {
      title: '类型', dataIndex: 'category', width: 100,
      render: (v: string) => categoryLabelMap[v] || v,
    },
    {
      title: '状态', dataIndex: 'status', width: 100,
      render: (v: string) => {
        const s = getStatusMeta(v);
        return <Tag theme="light" color={s?.color as 'blue'} className={`page-status-tag page-status-tag-${s.tone}`}>{s?.text || v}</Tag>;
      },
    },
    { title: '提交时间', dataIndex: 'submitTime', width: 180 },
    {
      title: '操作', width: 180,
      render: (_: unknown, record: InstanceVO) => (
        <PageActionGroup>
          <Button
            size="small"
            type="tertiary"
            theme="light"
            className="page-action-button page-action-button-view"
            onClick={() => navigate(`/approval/detail/${record.id}`)}
          >
            详情
          </Button>
          {record.status === 'pending' && (
            <Button
              size="small"
              type="danger"
              theme="light"
              className="page-action-button page-action-button-danger"
              onClick={async () => { await cancelInstance(record.id); fetchData(); }}
            >
              撤销
            </Button>
          )}
        </PageActionGroup>
      ),
    },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="我的申请"
        description="查看我发起的审批"
      />
      <PageFilterCard>
        <Form>
          <Space wrap spacing={16} align="center" className="page-filter-space">
            <Space wrap spacing={12} className="page-filter-fields">
              <Select
                className="page-filter-control"
                placeholder="筛选状态"
                value={status}
                onChange={(v) => setStatus(v as string)}
                optionList={[
                  { value: '', label: '全部' },
                  ...statusOptions,
                ]}
              />
            </Space>
            <Space spacing={8} className="page-filter-actions">
              <Button type="primary" theme="solid" onClick={() => { setPageNum(1); fetchData(1); }}>查询</Button>
              <Button theme="solid" onClick={() => {
                setStatus('');
                setPageNum(1);
                fetchData(1, '');
              }}
              >
                重置
              </Button>
            </Space>
          </Space>
        </Form>
      </PageFilterCard>
      <Card className="page-table-card">
        <Table
          columns={columns}
          dataSource={data}
          loading={loading}
          pagination={{ currentPage: pageNum, pageSize: 10, total, onPageChange: setPageNum }}
          rowKey="id"
        />
      </Card>
    </div>
  );
}
