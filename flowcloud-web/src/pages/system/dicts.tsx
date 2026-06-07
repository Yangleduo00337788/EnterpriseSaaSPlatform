import { useEffect, useState } from 'react';
import { Button, Card, Form, Input, InputNumber, Modal, Switch, Table, Tag, Toast } from '@douyinfe/semi-ui';
import { createDict, deleteDict, getDictById, getDictList, updateDict } from '@/api/dict';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS } from '@/utils/statusDisplay';
import type { DictDataVO, DictTypeVO } from '@/types';

export default function DictsPage() {
  const { hasPermission } = usePermission();
  const canEdit = hasPermission(PERM.DICT_EDIT);
  const [data, setData] = useState<DictTypeVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<DictTypeVO | null>(null);
  const [items, setItems] = useState<DictDataVO[]>([]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await getDictList();
      setData(res.data);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(fetchData);

  useEffect(() => { fetchData(); }, []);

  const openCreate = () => {
    setEditing(null);
    setItems([{ id: 0, dictTypeId: 0, dictLabel: '', dictValue: '', sort: 0, status: 1 }]);
    setModalVisible(true);
  };

  const openEdit = async (record: DictTypeVO) => {
    const res = await getDictById(record.id);
    setEditing(res.data);
    setItems(res.data.items || []);
    setModalVisible(true);
  };

  const handleSubmit = async (values: Record<string, unknown>) => {
    const payload = { ...values, items };
    if (editing) {
      await updateDict(editing.id, payload as Partial<DictTypeVO>);
      Toast.success('更新成功');
    } else {
      await createDict(payload as Partial<DictTypeVO>);
      Toast.success('创建成功');
    }
    setModalVisible(false);
    fetchData();
  };

  const columns = [
    { title: '字典编码', dataIndex: 'dictCode', width: 160 },
    { title: '字典名称', dataIndex: 'dictName', width: 160 },
    { title: '备注', dataIndex: 'remark', ellipsis: true },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: (v: number) => <Tag color={ENABLED_STATUS_META[v]?.color ?? 'grey'}>{ENABLED_STATUS_META[v]?.text || v}</Tag>,
    },
    {
      title: '操作', width: 160,
      render: (_: unknown, record: DictTypeVO) => (
        <>
          <Button size="small" style={{ marginRight: 8 }} onClick={() => openEdit(record)}>编辑</Button>
          {canEdit && (
            <Button size="small" type="danger" onClick={() => Modal.confirm({
              title: '确认删除',
              content: `删除字典「${record.dictName}」？`,
              onOk: async () => { await deleteDict(record.id); Toast.success('删除成功'); fetchData(); },
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
          <h2>系统字典</h2>
          <p>维护审批分类、状态等可配置字典项</p>
        </div>
        {canEdit && <Button type="primary" onClick={openCreate}>新建字典</Button>}
      </div>
      <Card>
        <Table columns={columns} dataSource={data} loading={loading} rowKey="id" pagination={false} />
      </Card>
      <Modal title={editing ? '编辑字典' : '新建字典'} visible={modalVisible} onCancel={() => setModalVisible(false)} footer={null} width={760}>
        <Form key={editing?.id || 'new'} initValues={editing || { status: 1 }} onSubmit={handleSubmit} disabled={!canEdit}>
          <Form.Input field="dictCode" label="字典编码" rules={[{ required: true, message: '必填' }]} disabled={!!editing} />
          <Form.Input field="dictName" label="字典名称" rules={[{ required: true, message: '必填' }]} />
          <Form.TextArea field="remark" label="备注" />
          <Form.Select field="status" label="状态" optionList={ENABLED_STATUS_OPTIONS} />
          <div style={{ margin: '12px 0', fontWeight: 600 }}>字典项</div>
          {items.map((item, index) => (
            <div key={index} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 80px 80px auto', gap: 8, marginBottom: 8 }}>
              <Input value={item.dictLabel} onChange={(v) => setItems((prev) => prev.map((x, i) => i === index ? { ...x, dictLabel: v } : x))} placeholder="标签" />
              <Input value={item.dictValue} onChange={(v) => setItems((prev) => prev.map((x, i) => i === index ? { ...x, dictValue: v } : x))} placeholder="值" />
              <InputNumber value={item.sort} onChange={(v) => setItems((prev) => prev.map((x, i) => i === index ? { ...x, sort: Number(v) } : x))} placeholder="排序" />
              <Switch checked={item.status === 1} onChange={(v) => setItems((prev) => prev.map((x, i) => i === index ? { ...x, status: v ? 1 : 0 } : x))} />
              <Button type="danger" onClick={() => setItems((prev) => prev.filter((_, i) => i !== index))}>删</Button>
            </div>
          ))}
          <Button style={{ marginBottom: 16 }} onClick={() => setItems((prev) => [...prev, { id: 0, dictTypeId: 0, dictLabel: '', dictValue: '', sort: prev.length, status: 1 }])}>添加字典项</Button>
          {canEdit && <Button type="primary" htmlType="submit">保存</Button>}
        </Form>
      </Modal>
    </div>
  );
}
