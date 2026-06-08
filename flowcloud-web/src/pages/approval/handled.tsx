import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Card, Tag } from '@douyinfe/semi-ui';
import { getHandledTasks } from '@/api/approval';
import { PageHeader } from '@/components/page-kit';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { TASK_RESULT_META } from '@/utils/approvalDisplay';
import type { TaskVO } from '@/types';

export default function HandledTasksPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<TaskVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);

  const fetchData = async (page = pageNum) => {
    setLoading(true);
    try {
      const res = await getHandledTasks({ pageNum: page, pageSize: 10 });
      setData(res.data.records);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData(pageNum));

  useEffect(() => { fetchData(pageNum); }, [pageNum]);

  return (
    <div className="page-container">
      <PageHeader
        title="已办任务"
        description="查看我已处理的审批任务"
      />
      <Card className="page-table-card">
        <Table
          columns={[
            { title: '审批单号', dataIndex: 'instanceNo', width: 180 },
            {
              title: '标题',
              dataIndex: 'title',
              render: (value: string) => <span className="page-table-text-ellipsis" title={value}>{value}</span>,
            },
            { title: '处理节点', dataIndex: 'nodeName', width: 120 },
            {
              title: '处理结果',
              dataIndex: 'statusLabel',
              width: 100,
              render: (value: string, record: TaskVO) => {
                const meta = TASK_RESULT_META[record.status];
                return (
                  <Tag theme="light" color={meta?.color ?? 'grey'} className={`page-status-tag page-status-tag-${meta?.tone ?? 'neutral'}`}>
                    {value || meta?.text || record.status}
                  </Tag>
                );
              },
            },
            { title: '处理时间', dataIndex: 'handleTime', width: 180 },
            {
              title: '操作',
              width: 120,
              render: (_: unknown, record: TaskVO) => (
                <Button
                  size="small"
                  type="tertiary"
                  theme="light"
                  className="page-action-button page-action-button-view"
                  onClick={() => navigate(`/approval/detail/${record.instanceId}`)}
                >
                  详情
                </Button>
              ),
            },
          ]}
          dataSource={data}
          loading={loading}
          pagination={{
            currentPage: pageNum,
            pageSize: 10,
            total,
            onPageChange: setPageNum,
          }}
          rowKey="id"
        />
      </Card>
    </div>
  );
}
