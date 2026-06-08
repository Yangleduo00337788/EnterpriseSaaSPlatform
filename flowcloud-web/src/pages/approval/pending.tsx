import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Table, Button, Card, Modal, Form, Toast, Space,
} from '@douyinfe/semi-ui';
import { getPendingTasks, completeTask } from '@/api/approval';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import type { TaskVO } from '@/types';

export default function PendingTasksPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<TaskVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [actionModal, setActionModal] = useState<{ visible: boolean; task?: TaskVO; action?: string }>({ visible: false });

  const fetchData = async (page = pageNum) => {
    setLoading(true);
    try {
      const res = await getPendingTasks({ pageNum: page, pageSize: 10 });
      setData(res.data.records);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData(pageNum));

  useEffect(() => { fetchData(pageNum); }, [pageNum]);

  const handleComplete = async (values: { comment?: string }) => {
    if (!actionModal.task || !actionModal.action) return;
    await completeTask({
      taskId: actionModal.task.id,
      action: actionModal.action,
      comment: values.comment,
    });
    Toast.success(actionModal.action === 'approve' ? '审批通过' : '已驳回');
    setActionModal({ visible: false });
    fetchData();
  };

  const columns = [
    { title: '审批单号', dataIndex: 'instanceNo', width: 180 },
    {
      title: '标题', dataIndex: 'title',
      render: (value: string) => <span className="page-table-text-ellipsis" title={value}>{value}</span>,
    },
    { title: '当前节点', dataIndex: 'nodeName', width: 120 },
    { title: '提交时间', dataIndex: 'createTime', width: 180 },
    {
      title: '操作', width: 228,
      render: (_: unknown, record: TaskVO) => (
        <Space spacing={8} className="page-inline-actions">
          <Button
            size="small"
            type="tertiary"
            theme="light"
            className="page-action-button page-action-button-view"
            onClick={() => navigate(`/approval/detail/${record.instanceId}`)}
          >
            详情
          </Button>
          <Button
            size="small"
            type="primary"
            theme="solid"
            className="page-action-button page-action-button-primary"
            onClick={() => setActionModal({ visible: true, task: record, action: 'approve' })}
          >
            通过
          </Button>
          <Button
            size="small"
            type="danger"
            theme="light"
            className="page-action-button page-action-button-danger"
            onClick={() => setActionModal({ visible: true, task: record, action: 'reject' })}
          >
            驳回
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="待我审批"
        description="处理需要您审批的任务"
      />
      <Card className="page-table-card">
        <Table
          columns={columns}
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

      <Modal
        title={actionModal.action === 'approve' ? '审批通过' : '审批驳回'}
        visible={actionModal.visible}
        onCancel={() => setActionModal({ visible: false })}
        footer={null}
      >
        <Form onSubmit={handleComplete}>
          <Form.TextArea field="comment" label="审批意见" placeholder="请输入审批意见（可选）" />
          <PageFormActions>
            <Button type="tertiary" theme="light" className="page-action-button" onClick={() => setActionModal({ visible: false })}>取消</Button>
            <Button htmlType="submit" type="primary" theme="solid" className="page-action-button page-action-button-primary">确认</Button>
          </PageFormActions>
        </Form>
      </Modal>
    </div>
  );
}
