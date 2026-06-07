import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Card, Tag, Button, Toast } from '@douyinfe/semi-ui';
import { getMessages, markMessageRead, markAllMessagesRead, markBatchMessagesRead } from '@/api/message';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { MESSAGE_READ_STATUS_META, MESSAGE_TYPE_META } from '@/utils/statusDisplay';
import type { MessageVO } from '@/types';

export default function MessagesPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<MessageVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [selectedKeys, setSelectedKeys] = useState<number[]>([]);

  const fetchData = async (page = pageNum) => {
    setLoading(true);
    try {
      const res = await getMessages({ pageNum: page, pageSize: 10 });
      setData(res.data.records);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData(pageNum));

  const handleRead = async (record: MessageVO) => {
    if (record.isRead === 0) {
      await markMessageRead(record.id);
      fetchData();
    }
    if (record.bizType === 'approval' && record.bizId) {
      navigate(`/approval/detail/${record.bizId}`);
    }
  };

  const columns = [
    {
      title: '类型', dataIndex: 'type', width: 80,
      render: (v: string) => {
        const t = MESSAGE_TYPE_META[v] || { text: v, color: 'grey' as const };
        return <Tag color={t.color as 'blue'}>{t.text}</Tag>;
      },
    },
    { title: '标题', dataIndex: 'title' },
    { title: '内容', dataIndex: 'content', ellipsis: true },
    {
      title: '状态', dataIndex: 'isRead', width: 80,
      render: (v: number) => <Tag color={MESSAGE_READ_STATUS_META[v]?.color ?? 'grey'}>{MESSAGE_READ_STATUS_META[v]?.text || v}</Tag>,
    },
    { title: '时间', dataIndex: 'createTime', width: 180 },
    {
      title: '操作', width: 120,
      render: (_: unknown, record: MessageVO) => (
        <Button size="small" onClick={() => handleRead(record)}>
          {record.bizType === 'approval' && record.bizId ? '查看审批' : '标为已读'}
        </Button>
      ),
    },
  ];

  const markAllRead = async () => {
    await markAllMessagesRead();
    Toast.success('已全部标为已读');
    setSelectedKeys([]);
    fetchData();
  };

  const markSelectedRead = async () => {
    const unreadIds = data.filter((m) => selectedKeys.includes(m.id) && m.isRead === 0).map((m) => m.id);
    if (unreadIds.length === 0) {
      Toast.info('请选择未读消息');
      return;
    }
    await markBatchMessagesRead(unreadIds);
    Toast.success('已标为已读');
    setSelectedKeys([]);
    fetchData();
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <h2>消息中心</h2>
          <p>查看审批提醒与系统通知</p>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <Button disabled={selectedKeys.length === 0} onClick={markSelectedRead}>批量已读</Button>
          <Button onClick={markAllRead}>全部标为已读</Button>
        </div>
      </div>
      <Card>
        <Table
          columns={columns}
          dataSource={data}
          loading={loading}
          rowSelection={{ selectedRowKeys: selectedKeys, onChange: (keys) => setSelectedKeys(keys as number[]) }}
          pagination={{ currentPage: pageNum, pageSize: 10, total, onPageChange: (p) => { setPageNum(p); fetchData(p); } }}
          rowKey="id"
        />
      </Card>
    </div>
  );
}
