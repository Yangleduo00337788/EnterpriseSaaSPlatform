import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Card, Modal, Form, Toast } from '@douyinfe/semi-ui';
import { getPendingTasks, completeTask } from '@/api/approval';
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
    { title: '标题', dataIndex: 'title' },
    { title: '当前节点', dataIndex: 'nodeName', width: 120 },
    { title: '提交时间', dataIndex: 'createTime', width: 180 },
    {
      title: '操作', width: 240,
      render: (_: unknown, record: TaskVO) => (
        <>
          <Button size="small" onClick={() => navigate(`/approval/detail/${record.instanceId}`)}>
            详情
          </Button>
          <Button size="small" type="primary" style={{ marginLeft: 8 }}
            onClick={() => setActionModal({ visible: true, task: record, action: 'approve' })}>
            通过
          </Button>
          <Button size="small" type="danger" style={{ marginLeft: 8 }}
            onClick={() => setActionModal({ visible: true, task: record, action: 'reject' })}>
            驳回
          </Button>
        </>
      ),
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>待我审批</h2>
        <p>处理需要您审批的任务</p>
      </div>
      <Card>
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
          <Button htmlType="submit" type="primary" theme="solid" block style={{ marginTop: 16 }}>
            确认
          </Button>
        </Form>
      </Modal>
    </div>
  );
}
