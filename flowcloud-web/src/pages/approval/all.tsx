import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Table, Tag, Button, Card, Select, Space, Form,
} from '@douyinfe/semi-ui';
import { getAllInstances } from '@/api/approval';
import { PageFilterCard, PageHeader } from '@/components/page-kit';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import { useApprovalStatus } from '@/hooks/useApprovalStatus';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import type { InstanceVO } from '@/types';

export default function AllInstancesPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<InstanceVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [status, setStatus] = useState<string>();
  const [category, setCategory] = useState<string>();
  const { options: statusOptions, getStatusMeta } = useApprovalStatus();
  const { options: categoryOptions, labelMap: categoryLabelMap } = useApprovalCategory();

  const fetchData = async (page = pageNum, nextStatus = status, nextCategory = category) => {
    setLoading(true);
    try {
      const res = await getAllInstances({
        status: nextStatus || undefined,
        category: nextCategory || undefined,
        pageNum: page,
        pageSize: 10,
      });
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
    { title: '申请人', dataIndex: 'applicantName', width: 100 },
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
      title: '操作', width: 120,
      render: (_: unknown, record: InstanceVO) => (
        <Button
          size="small"
          type="tertiary"
          theme="light"
          className="page-action-button page-action-button-view"
          onClick={() => navigate(`/approval/detail/${record.id}`)}
        >
          详情
        </Button>
      ),
    },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="全部审批"
        description="查看企业所有审批记录"
      />
      <PageFilterCard>
        <Form>
          <Space wrap spacing={16} align="center" className="page-filter-space">
            <Space wrap spacing={12} className="page-filter-fields">
              <Select
                className="page-filter-control"
                placeholder="类型"
                value={category}
                onChange={(v) => setCategory(v as string)}
                optionList={[
                  { value: '', label: '全部' },
                  ...categoryOptions,
                ]}
              />
              <Select
                className="page-filter-control"
                placeholder="状态"
                value={status}
                onChange={(v) => setStatus(v as string)}
                optionList={[
                  { value: '', label: '全部' },
                  ...statusOptions,
                ]}
              />
            </Space>
            <Space spacing={8} className="page-filter-actions">
              <Button type="primary" theme="solid" onClick={() => fetchData(1)}>查询</Button>
              <Button theme="solid" onClick={() => {
                setCategory('');
                setStatus('');
                setPageNum(1);
                fetchData(1, '', '');
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
