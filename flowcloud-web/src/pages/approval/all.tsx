import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Tag, Button, Card, Select } from '@douyinfe/semi-ui';
import { getAllInstances } from '@/api/approval';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { STATUS_MAP, CATEGORY_MAP } from '@/utils/constants';
import type { InstanceVO } from '@/types';

export default function AllInstancesPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<InstanceVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [status, setStatus] = useState<string>();
  const [category, setCategory] = useState<string>();

  const fetchData = async (page = pageNum) => {
    setLoading(true);
    try {
      const res = await getAllInstances({ status, category, pageNum: page, pageSize: 10 });
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
  }, [status, category]);

  const columns = [
    { title: '审批单号', dataIndex: 'instanceNo', width: 180 },
    { title: '标题', dataIndex: 'title' },
    { title: '申请人', dataIndex: 'applicantName', width: 100 },
    {
      title: '类型', dataIndex: 'category', width: 100,
      render: (v: string) => CATEGORY_MAP[v] || v,
    },
    {
      title: '状态', dataIndex: 'status', width: 100,
      render: (v: string) => {
        const s = STATUS_MAP[v];
        return <Tag color={s?.color as 'blue'}>{s?.text || v}</Tag>;
      },
    },
    { title: '提交时间', dataIndex: 'submitTime', width: 180 },
    {
      title: '操作', width: 100,
      render: (_: unknown, record: InstanceVO) => (
        <Button size="small" onClick={() => navigate(`/approval/detail/${record.id}`)}>
          详情
        </Button>
      ),
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <h2>全部审批</h2>
          <p>查看企业所有审批记录</p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <Select
            placeholder="类型" style={{ width: 120 }}
            value={category} onChange={(v) => setCategory(v as string)}
            optionList={[
              { value: '', label: '全部' },
              ...Object.entries(CATEGORY_MAP).map(([k, v]) => ({ value: k, label: v })),
            ]}
          />
          <Select
            placeholder="状态" style={{ width: 120 }}
            value={status} onChange={(v) => setStatus(v as string)}
            optionList={[
              { value: '', label: '全部' },
              ...Object.entries(STATUS_MAP).map(([k, v]) => ({ value: k, label: v.text })),
            ]}
          />
        </div>
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
