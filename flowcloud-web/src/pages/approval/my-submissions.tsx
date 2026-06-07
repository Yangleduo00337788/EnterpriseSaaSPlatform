import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Tag, Button, Card, Select } from '@douyinfe/semi-ui';
import { getMySubmissions, cancelInstance } from '@/api/approval';
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

  const fetchData = async (page = pageNum) => {
    setLoading(true);
    try {
      const res = await getMySubmissions({ status, pageNum: page, pageSize: 10 });
      setData(res.data.records);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData(pageNum));

  useEffect(() => { fetchData(pageNum); }, [pageNum]);

  useEffect(() => {
    setPageNum(1);
    fetchData(1);
  }, [status]);

  const columns = [
    { title: '审批单号', dataIndex: 'instanceNo', width: 180 },
    { title: '标题', dataIndex: 'title' },
    {
      title: '类型', dataIndex: 'category', width: 100,
      render: (v: string) => categoryLabelMap[v] || v,
    },
    {
      title: '状态', dataIndex: 'status', width: 100,
      render: (v: string) => {
        const s = getStatusMeta(v);
        return <Tag color={s?.color as 'blue'}>{s?.text || v}</Tag>;
      },
    },
    { title: '提交时间', dataIndex: 'submitTime', width: 180 },
    {
      title: '操作', width: 160,
      render: (_: unknown, record: InstanceVO) => (
        <>
          <Button size="small" onClick={() => navigate(`/approval/detail/${record.id}`)}>
            详情
          </Button>
          {record.status === 'pending' && (
            <Button size="small" type="danger" style={{ marginLeft: 8 }}
              onClick={async () => { await cancelInstance(record.id); fetchData(); }}>
              撤销
            </Button>
          )}
        </>
      ),
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <h2>我的申请</h2>
          <p>查看我发起的审批</p>
        </div>
        <Select
          placeholder="筛选状态" style={{ width: 160 }}
          value={status} onChange={(v) => setStatus(v as string)}
          optionList={[
            { value: '', label: '全部' },
            ...statusOptions,
          ]}
        />
      </div>
      <Card>
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
