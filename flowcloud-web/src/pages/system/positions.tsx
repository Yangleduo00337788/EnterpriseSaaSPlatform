import { useEffect, useMemo, useState } from 'react';
import {
  Button, Card, Form, Modal, Table, Tag, Toast, Space,
} from '@douyinfe/semi-ui';
import {
  createPosition, deletePosition, getPositions, updatePosition,
} from '@/api/position';
import { getDeptTree } from '@/api/dept';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS } from '@/utils/statusDisplay';
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
      <PageHeader
        title="岗位管理"
        description="维护岗位信息，用于审批人来源配置"
        actions={canEdit ? (
          <Button
            type="primary"
            theme="solid"
            className="page-toolbar-button"
            onClick={() => { setEditing(null); setVisible(true); }}
          >
            新增岗位
          </Button>
        ) : undefined}
      />
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
                <Tag color={ENABLED_STATUS_META[v]?.color ?? 'grey'}>{ENABLED_STATUS_META[v]?.text || v}</Tag>
              ),
            },
            { title: '备注', dataIndex: 'remark', render: (v?: string) => v || '-' },
            ...(canEdit ? [{
              title: '操作', width: 160,
              render: (_: unknown, record: PositionVO) => (
                <Space spacing={8} className="page-inline-actions">
                  <Button
                    size="small"
                    type="primary"
                    theme="light"
                    className="page-action-button page-action-button-secondary"
                    onClick={() => { setEditing(record); setVisible(true); }}
                  >
                    编辑
                  </Button>
                  <Button
                    size="small"
                    type="danger"
                    theme="light"
                    className="page-action-button page-action-button-danger"
                    onClick={() => handleDelete(record.id)}
                  >
                    删除
                  </Button>
                </Space>
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
            optionList={ENABLED_STATUS_OPTIONS}
          />
          <Form.TextArea field="remark" label="备注" rows={2} />
          <PageFormActions>
            <Button theme="borderless" onClick={() => { setVisible(false); setEditing(null); }}>取消</Button>
            <Button htmlType="submit" type="primary" theme="solid">保存</Button>
          </PageFormActions>
        </Form>
      </Modal>
    </div>
  );
}
