import { useEffect, useMemo, useState } from 'react';
import { Button, Card, Form, Modal, Table, Tag, Toast } from '@douyinfe/semi-ui';
import {
  createPosition, deletePosition, getPositions, updatePosition,
} from '@/api/position';
import { getDeptTree } from '@/api/dept';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import type { DeptVO, PositionVO } from '@/types';

function flattenTree(nodes: DeptVO[], level = 0): Array<{ value: number; label: string }> {
  return nodes.flatMap((node) => [
    { value: node.id, label: `${'　'.repeat(level)}${node.deptName}` },
    ...flattenTree(node.children || [], level + 1),
  ]);
}

export default function PositionsPage() {
  const { hasPermission } = usePermission();
  const canEdit = hasPermission(PERM.POSITION_EDIT);
  const [data, setData] = useState<PositionVO[]>([]);
  const [deptOptions, setDeptOptions] = useState<Array<{ value: number; label: string }>>([]);
  const [visible, setVisible] = useState(false);
  const [editing, setEditing] = useState<PositionVO | null>(null);

  const deptMap = useMemo(
    () => Object.fromEntries(deptOptions.map((d) => [d.value, d.label.trim()])),
    [deptOptions],
  );

  const fetchData = async () => {
    const res = await getPositions();
    setData(res.data);
  };

  useEffect(() => {
    fetchData();
    getDeptTree().then((res) => setDeptOptions(flattenTree(res.data))).catch(() => {});
  }, []);

  const handleSubmit = async (values: Record<string, unknown>) => {
    if (editing) {
      await updatePosition(editing.id, values as Partial<PositionVO>);
      Toast.success('更新成功');
    } else {
      await createPosition(values as Partial<PositionVO>);
      Toast.success('创建成功');
    }
    setVisible(false);
    setEditing(null);
    fetchData();
  };

  const handleDelete = async (id: number) => {
    await deletePosition(id);
    Toast.success('删除成功');
    fetchData();
  };

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <h2>岗位管理</h2>
          <p>维护岗位信息，用于审批人来源配置</p>
        </div>
        {canEdit && (
          <Button type="primary" onClick={() => { setEditing(null); setVisible(true); }}>
            新增岗位
          </Button>
        )}
      </div>
      <Card>
        <Table
          rowKey="id"
          dataSource={data}
          pagination={false}
          columns={[
            { title: '岗位编码', dataIndex: 'positionCode', width: 160 },
            { title: '岗位名称', dataIndex: 'positionName' },
            {
              title: '所属部门', dataIndex: 'deptId', width: 180,
              render: (v?: number) => (v ? (deptMap[v] || v) : '-'),
            },
            { title: '排序', dataIndex: 'sort', width: 80 },
            {
              title: '状态', dataIndex: 'status', width: 90,
              render: (v: number) => (
                <Tag color={v === 1 ? 'green' : 'red'}>{v === 1 ? '启用' : '禁用'}</Tag>
              ),
            },
            { title: '备注', dataIndex: 'remark', render: (v?: string) => v || '-' },
            ...(canEdit ? [{
              title: '操作', width: 160,
              render: (_: unknown, record: PositionVO) => (
                <div style={{ display: 'flex', gap: 8 }}>
                  <Button size="small" onClick={() => { setEditing(record); setVisible(true); }}>
                    编辑
                  </Button>
                  <Button
                    size="small" type="danger"
                    onClick={() => handleDelete(record.id)}
                  >
                    删除
                  </Button>
                </div>
              ),
            }] : []),
          ]}
        />
      </Card>

      <Modal
        title={editing ? '编辑岗位' : '新增岗位'}
        visible={visible}
        onCancel={() => { setVisible(false); setEditing(null); }}
        footer={null}
        width={480}
      >
        <Form
          key={editing?.id || 'new-pos'}
          onSubmit={handleSubmit}
          initValues={editing || { sort: 0, status: 1 }}
        >
          <Form.Input
            field="positionCode" label="岗位编码"
            rules={[{ required: true, message: '必填' }]}
            placeholder="如 ENGINEER / MANAGER"
          />
          <Form.Input
            field="positionName" label="岗位名称"
            rules={[{ required: true, message: '必填' }]}
          />
          <Form.Select
            field="deptId" label="所属部门"
            optionList={[{ value: undefined as unknown as number, label: '（不限部门）' }, ...deptOptions]}
            style={{ width: '100%' }}
            filter
            placeholder="可搜索部门"
          />
          <Form.InputNumber field="sort" label="排序" />
          <Form.Select
            field="status" label="状态"
            optionList={[{ label: '启用', value: 1 }, { label: '禁用', value: 0 }]}
          />
          <Form.TextArea field="remark" label="备注" rows={2} />
          <Button htmlType="submit" type="primary" block style={{ marginTop: 16 }}>
            保存
          </Button>
        </Form>
      </Modal>
    </div>
  );
}