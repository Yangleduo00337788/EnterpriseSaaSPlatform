import { useEffect, useMemo, useState } from 'react';
import {
  Button, Card, Form, Modal, Table, Tag, Toast, Space,
} from '@douyinfe/semi-ui';
import { createDept, deleteDept, getDeptTree, updateDept } from '@/api/dept';
import { getUserOptions } from '@/api/user';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { usePermission } from '@/hooks/usePermission';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS } from '@/utils/statusDisplay';
import { PERM } from '@/utils/permissions';
import type { DeptVO, UserOptionVO } from '@/types';

function flattenTree(nodes: DeptVO[], level = 0): Array<{ value: number; label: string }> {
  return nodes.flatMap((node) => [
    { value: node.id, label: `${'　'.repeat(level)}${node.deptName}` },
    ...flattenTree(node.children || [], level + 1),
  ]);
}

export default function DeptPage() {
  const { hasPermission } = usePermission();
  const canEditDept = hasPermission(PERM.DEPT_EDIT);
  const [data, setData] = useState<DeptVO[]>([]);
  const [visible, setVisible] = useState(false);
  const [editing, setEditing] = useState<DeptVO | null>(null);
  const [userOptions, setUserOptions] = useState<UserOptionVO[]>([]);
  const [deptOptions, setDeptOptions] = useState<Array<{ value: number; label: string }>>([]);

  const userSelectOptions = useMemo(
    () => userOptions.map((u) => ({
      value: u.id,
      label: `${u.realName}${u.jobTitle ? `（${u.jobTitle}）` : ''}`,
    })),
    [userOptions],
  );

  const fetchData = async () => {
    const res = await getDeptTree();
    setData(res.data);
    setDeptOptions(flattenTree(res.data));
  };

  useEffect(() => {
    fetchData();
    getUserOptions().then((res) => setUserOptions(res.data)).catch(() => {});
  }, []);

  const handleSubmit = async (values: Record<string, unknown>) => {
    if (editing) {
      await updateDept(editing.id, values as Partial<DeptVO>);
      Toast.success('更新成功');
    } else {
      await createDept(values as Partial<DeptVO>);
      Toast.success('创建成功');
    }
    setVisible(false);
    setEditing(null);
    fetchData();
  };

  return (
    <div className="page-container">
      <PageHeader
        title="组织架构"
        description="维护部门树、负责人和启停状态"
        actions={canEditDept ? <Button type="primary" theme="solid" onClick={() => { setEditing(null); setVisible(true); }}>新增部门</Button> : undefined}
      />
      <Card>
        <Table
          rowKey="id"
          dataSource={data}
          pagination={false}
          columns={[
            { title: '部门名称', dataIndex: 'deptName' },
            { title: '负责人', dataIndex: 'leader', render: (value?: string) => value || '-' },
            { title: '排序', dataIndex: 'sort', width: 100 },
            {
              title: '状态', dataIndex: 'status', width: 100,
              render: (value: number) => <Tag color={ENABLED_STATUS_META[value]?.color ?? 'grey'}>{ENABLED_STATUS_META[value]?.text || value}</Tag>,
            },
            ...(canEditDept ? [{
              title: '操作', width: 180,
              render: (_: unknown, record: DeptVO) => (
                <Space spacing={8} className="page-inline-actions">
                  <Button size="small" onClick={() => { setEditing(record); setVisible(true); }}>编辑</Button>
                  <Button size="small" type="danger" onClick={async () => { await deleteDept(record.id); fetchData(); }}>删除</Button>
                </Space>
              ),
            }] : []),
          ]}
        />
      </Card>

      <Modal
        title={editing ? '编辑部门' : '新增部门'}
        visible={visible}
        onCancel={() => { setVisible(false); setEditing(null); }}
        footer={null}
        width={480}
      >
        <Form
          key={editing?.id || 'new-dept'}
          onSubmit={handleSubmit}
          initValues={editing || { parentId: 0, status: 1, sort: 0 }}
        >
          <Form.Select
            field="parentId"
            label="上级部门"
            optionList={[{ value: 0, label: '（无，根部门）' }, ...deptOptions]}
            style={{ width: '100%' }}
          />
          <Form.Input field="deptName" label="部门名称" rules={[{ required: true, message: '必填' }]} />
          <Form.Select
            field="leaderUserId"
            label="负责人"
            optionList={userSelectOptions}
            filter
            style={{ width: '100%' }}
            placeholder="请选择负责人（可搜索）"
          />
          <Form.Input field="leader" label="负责人姓名" placeholder="自动从选人带入，或手动输入" />
          <Form.InputNumber field="sort" label="排序" />
          <Form.Select field="status" label="状态" optionList={ENABLED_STATUS_OPTIONS} />
          <PageFormActions>
            <Button onClick={() => { setVisible(false); setEditing(null); }}>取消</Button>
            <Button htmlType="submit" type="primary">保存</Button>
          </PageFormActions>
        </Form>
      </Modal>
    </div>
  );
}
