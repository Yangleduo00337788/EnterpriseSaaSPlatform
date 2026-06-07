import { useEffect, useState } from 'react';
import { Button, Card, Form, Modal, Table, Tag, Toast } from '@douyinfe/semi-ui';
import { createMessageTemplate, deleteMessageTemplate, getMessageTemplates, updateMessageTemplate } from '@/api/messageTemplate';
import { useDictOptions } from '@/hooks/useDictOptions';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS } from '@/utils/statusDisplay';
import type { MessageTemplateVO } from '@/types';

export default function MessageTemplatesPage() {
  const { hasPermission } = usePermission();
  const canEdit = hasPermission(PERM.MESSAGE_TEMPLATE_EDIT);
  const { options: eventOptions, labelMap: eventLabelMap } = useDictOptions('message_event_type', [
    { value: 'TASK_ASSIGNED', label: '任务分配' },
    { value: 'APPROVED', label: '审批通过' },
    { value: 'REJECTED', label: '审批驳回' },
    { value: 'CANCELLED', label: '审批撤销' },
    { value: 'REMIND', label: '审批催办' },
  ]);
  const [data, setData] = useState<MessageTemplateVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<MessageTemplateVO | null>(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await getMessageTemplates();
      setData(res.data);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(fetchData);
  useEffect(() => { fetchData(); }, []);

  const handleSubmit = async (values: Record<string, unknown>) => {
    if (editing) {
      await updateMessageTemplate(editing.id, values as Partial<MessageTemplateVO>);
      Toast.success('更新成功');
    } else {
      await createMessageTemplate(values as Partial<MessageTemplateVO>);
      Toast.success('创建成功');
    }
    setModalVisible(false);
    fetchData();
  };

  const columns = [
    { title: '模板编码', dataIndex: 'templateCode', width: 140 },
    { title: '模板名称', dataIndex: 'templateName', width: 140 },
    { title: '事件类型', dataIndex: 'eventType', width: 120, render: (value: string) => eventLabelMap[value] || value },
    { title: '标题模板', dataIndex: 'titleTemplate', ellipsis: true },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: (v: number) => <Tag color={ENABLED_STATUS_META[v]?.color ?? 'grey'}>{ENABLED_STATUS_META[v]?.text || v}</Tag>,
    },
    {
      title: '操作', width: 160,
      render: (_: unknown, record: MessageTemplateVO) => (
        <>
          <Button size="small" style={{ marginRight: 8 }} onClick={() => { setEditing(record); setModalVisible(true); }}>编辑</Button>
          {canEdit && (
            <Button size="small" type="danger" onClick={() => Modal.confirm({
              title: '确认删除',
              content: `删除模板「${record.templateName}」？`,
              onOk: async () => { await deleteMessageTemplate(record.id); Toast.success('删除成功'); fetchData(); },
            })}>删除</Button>
          )}
        </>
      ),
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <h2>消息模板</h2>
          <p>配置审批通知标题与正文，支持变量：{'{operator}'}、{'{title}'}、{'{comment}'}</p>
        </div>
        {canEdit && <Button type="primary" onClick={() => { setEditing(null); setModalVisible(true); }}>新建模板</Button>}
      </div>
      <Card>
        <Table columns={columns} dataSource={data} loading={loading} rowKey="id" pagination={false} />
      </Card>
      <Modal title={editing ? '编辑模板' : '新建模板'} visible={modalVisible} onCancel={() => setModalVisible(false)} footer={null} width={640}>
        <Form key={editing?.id || 'new'} initValues={editing || { status: 1, eventType: 'TASK_ASSIGNED' }} onSubmit={handleSubmit} disabled={!canEdit}>
          <Form.Input field="templateCode" label="模板编码" rules={[{ required: true, message: '必填' }]} disabled={!!editing} />
          <Form.Input field="templateName" label="模板名称" rules={[{ required: true, message: '必填' }]} />
          <Form.Select field="eventType" label="事件类型" optionList={eventOptions} />
          <Form.Input field="titleTemplate" label="标题模板" rules={[{ required: true, message: '必填' }]} />
          <Form.TextArea field="contentTemplate" label="内容模板" rules={[{ required: true, message: '必填' }]} />
          <Form.Select field="status" label="状态" optionList={ENABLED_STATUS_OPTIONS} />
          {canEdit && <Button type="primary" htmlType="submit">保存</Button>}
        </Form>
      </Modal>
    </div>
  );
}
