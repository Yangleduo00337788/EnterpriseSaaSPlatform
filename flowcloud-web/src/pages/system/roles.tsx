import { useEffect, useState } from 'react';
import {
  Button, Card, Form, Modal, Table, Tag, Toast, Tree, Space,
} from '@douyinfe/semi-ui';
import { createRole, deleteRole, getPermissionTree, getRoleById, getRoleList, updateRole } from '@/api/role';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { useDictOptions } from '@/hooks/useDictOptions';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS } from '@/utils/statusDisplay';
import type { PermissionVO, RoleVO } from '@/types';

export default function RolesPage() {
  const { hasPermission } = usePermission();
  const canEdit = hasPermission(PERM.ROLE_EDIT);
  const { options: dataScopeOptions, labelMap: dataScopeLabelMap } = useDictOptions('role_data_scope', [
    { value: 'ALL', label: '全部数据' },
    { value: 'DEPT', label: '本部门' },
    { value: 'SELF', label: '仅本人' },
  ]);
  const [data, setData] = useState<RoleVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<RoleVO | null>(null);
  const [permissionTree, setPermissionTree] = useState<PermissionVO[]>([]);
  const [selectedPermIds, setSelectedPermIds] = useState<number[]>([]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await getRoleList();
      setData(res.data);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(fetchData);

  useEffect(() => {
    fetchData();
    getPermissionTree().then((res) => setPermissionTree(res.data)).catch(() => {});
  }, []);

  const openCreate = () => {
    setEditing(null);
    setSelectedPermIds([]);
    setModalVisible(true);
  };

  const openEdit = async (record: RoleVO) => {
    const res = await getRoleById(record.id);
    setEditing(res.data);
    setSelectedPermIds(res.data.permissionIds || []);
    setModalVisible(true);
  };

  const handleSubmit = async (values: Record<string, unknown>) => {
    const payload = { ...values, permissionIds: selectedPermIds };
    if (editing) {
      await updateRole(editing.id, payload as Partial<RoleVO> & { permissionIds?: number[] });
      Toast.success('更新成功');
    } else {
      await createRole(payload as Partial<RoleVO> & { permissionIds?: number[] });
      Toast.success('创建成功');
    }
    setModalVisible(false);
    setEditing(null);
    fetchData();
  };

  const handleDelete = (record: RoleVO) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定删除角色「${record.roleName}」吗？`,
      onOk: async () => {
        await deleteRole(record.id);
        Toast.success('删除成功');
        fetchData();
      },
    });
  };

  const columns = [
    { title: '角色编码', dataIndex: 'roleCode', width: 140 },
    { title: '角色名称', dataIndex: 'roleName', width: 140 },
    { title: '数据范围', dataIndex: 'dataScope', width: 100, render: (value: string) => dataScopeLabelMap[value] || value },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: (v: number) => <Tag color={ENABLED_STATUS_META[v]?.color ?? 'grey'}>{ENABLED_STATUS_META[v]?.text || v}</Tag>,
    },
    { title: '排序', dataIndex: 'sort', width: 70 },
    {
      title: '操作', width: 160,
      render: (_: unknown, record: RoleVO) => (
        <Space spacing={8} className="page-inline-actions">
          <Button size="small" onClick={() => openEdit(record)}>编辑</Button>
          {canEdit && !['admin', 'approver', 'employee'].includes(record.roleCode) && (
            <Button size="small" type="danger" onClick={() => handleDelete(record)}>删除</Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="角色管理"
        description="配置角色权限与数据范围"
        actions={canEdit ? <Button type="primary" theme="solid" onClick={openCreate}>新建角色</Button> : undefined}
      />
      <Card>
        <Table columns={columns} dataSource={data} loading={loading} rowKey="id" pagination={false} />
      </Card>
      <Modal
        title={editing ? '编辑角色' : '新建角色'}
        visible={modalVisible}
        onCancel={() => { setModalVisible(false); setEditing(null); }}
        footer={null}
        width={720}
      >
        <Form
          key={editing?.id || 'new'}
          initValues={editing || { status: 1, sort: 0, dataScope: 'SELF' }}
          onSubmit={handleSubmit}
          disabled={!canEdit}
        >
          <Form.Input field="roleCode" label="角色编码" rules={[{ required: true, message: '必填' }]} disabled={!!editing} />
          <Form.Input field="roleName" label="角色名称" rules={[{ required: true, message: '必填' }]} />
          <Form.TextArea field="description" label="描述" />
          <Form.Select field="dataScope" label="数据范围" optionList={dataScopeOptions} />
          <Form.InputNumber field="sort" label="排序" />
          <Form.Select field="status" label="状态" optionList={ENABLED_STATUS_OPTIONS} />
          <div className="page-tree-section">
            <div className="page-tree-section-title">权限配置</div>
            <Tree
              treeData={permissionTree.map((p) => ({
                key: String(p.id),
                label: p.permName,
                children: (p.children || []).map((c) => ({
                  key: String(c.id),
                  label: c.permName,
                  children: (c.children || []).map((g) => ({ key: String(g.id), label: g.permName })),
                })),
              }))}
              multiple
              defaultExpandAll
              value={selectedPermIds.map(String)}
              onChange={(value) => setSelectedPermIds((value as string[]).map(Number))}
            />
            <div className="page-muted-text">
              已选 {selectedPermIds.length} 项权限
            </div>
          </div>
          {canEdit && (
            <PageFormActions>
              <Button onClick={() => { setModalVisible(false); setEditing(null); }}>取消</Button>
              <Button type="primary" htmlType="submit">保存</Button>
            </PageFormActions>
          )}
        </Form>
      </Modal>
    </div>
  );
}
