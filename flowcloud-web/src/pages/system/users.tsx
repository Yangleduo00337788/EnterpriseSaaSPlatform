import { useEffect, useMemo, useState } from 'react';
import { Table, Button, Card, Modal, Form, Tag, Toast, Input, Switch } from '@douyinfe/semi-ui';
import { getUserList, createUser, updateUser, deleteUser, toggleUserStatus, getRoleOptions, getUserOptions, exportUsers, importUsers, resetUserPassword } from '@/api/user';
import { getDeptTree } from '@/api/dept';
import { assignUserPositions, getPositions, getUserPositionIds } from '@/api/position';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS, WORK_STATUS_OPTIONS } from '@/utils/statusDisplay';
import type { DeptVO, PositionVO, RoleOptionVO, UserOptionVO, UserVO } from '@/types';

function flattenDeptOptions(nodes: DeptVO[], level = 0): Array<{ value: number; label: string }> {
  return nodes.flatMap((node) => [
    { value: node.id, label: `${'　'.repeat(level)}${node.deptName}` },
    ...flattenDeptOptions(node.children || [], level + 1),
  ]);
}

export default function UserListPage() {
  const { hasPermission } = usePermission();
  const canEditUser = hasPermission(PERM.USER_EDIT);
  const [data, setData] = useState<UserVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<UserVO | null>(null);
  const [deptTree, setDeptTree] = useState<DeptVO[]>([]);
  const [roles, setRoles] = useState<RoleOptionVO[]>([]);
  const [positions, setPositions] = useState<PositionVO[]>([]);
  const [userOptions, setUserOptions] = useState<UserOptionVO[]>([]);
  const [resetPasswordTarget, setResetPasswordTarget] = useState<UserVO | null>(null);
  const [assignPositionTarget, setAssignPositionTarget] = useState<UserVO | null>(null);
  const [selectedPositionIds, setSelectedPositionIds] = useState<number[]>([]);

  const deptOptions = useMemo(() => flattenDeptOptions(deptTree), [deptTree]);
  const managerOptions = useMemo(
    () => userOptions.map((u) => ({
      value: u.id,
      label: `${u.realName}${u.jobTitle ? `（${u.jobTitle}）` : ''}${u.deptName ? ` - ${u.deptName}` : ''}`,
    })),
    [userOptions],
  );

  const fetchData = async (page = pageNum) => {
    setLoading(true);
    try {
      const res = await getUserList({ keyword, pageNum: page, pageSize: 10 });
      setData(res.data.records);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData(pageNum));

  useEffect(() => { fetchData(pageNum); }, [pageNum]);

  useEffect(() => {
    getDeptTree().then((res) => setDeptTree(res.data));
    getRoleOptions().then((res) => setRoles(res.data));
    getPositions().then((res) => setPositions(res.data)).catch(() => {});
    getUserOptions().then((res) => setUserOptions(res.data)).catch(() => {});
  }, []);

  const openAssignPositions = async (record: UserVO) => {
    const res = await getUserPositionIds(record.id);
    setAssignPositionTarget(record);
    setSelectedPositionIds(res.data || []);
  };

  const handleResetPassword = async (values: { password: string }) => {
    if (!resetPasswordTarget) return;
    await resetUserPassword(resetPasswordTarget.id, values.password);
    Toast.success(`已重置 ${resetPasswordTarget.realName} 的密码`);
    setResetPasswordTarget(null);
  };

  const handleAssignPositions = async () => {
    if (!assignPositionTarget) return;
    await assignUserPositions(assignPositionTarget.id, selectedPositionIds);
    Toast.success(`已保存 ${assignPositionTarget.realName} 的岗位设置`);
    setAssignPositionTarget(null);
  };

  const handleSubmit = async (values: Record<string, unknown>) => {
    if (editing) {
      await updateUser(editing.id, values as Partial<UserVO> & { roleIds?: number[] });
      Toast.success('更新成功');
    } else {
      await createUser(values as Partial<UserVO> & { password?: string; roleIds?: number[] });
      Toast.success('创建成功');
    }
    setModalVisible(false);
    setEditing(null);
    fetchData();
  };

  const columns = [
    { title: '用户名', dataIndex: 'username', width: 120 },
    { title: '姓名', dataIndex: 'realName', width: 100 },
    { title: '邮箱', dataIndex: 'email' },
    { title: '手机', dataIndex: 'phone', width: 130 },
    { title: '部门', dataIndex: 'deptName', width: 120 },
    { title: '岗位', dataIndex: 'jobTitle', width: 120, render: (value?: string) => value || '-' },
    { title: '直属上级', dataIndex: 'managerName', width: 120, render: (value?: string) => value || '-' },
    {
      title: '角色', dataIndex: 'roleNames', width: 180,
      render: (roleNames: string[]) => roleNames?.map((r) => <Tag key={r} style={{ marginRight: 4 }}>{r}</Tag>),
    },
    {
      title: '状态', dataIndex: 'status', width: 150,
      render: (v: number, record: UserVO) => {
        const statusMeta = ENABLED_STATUS_META[v];
        return (
        canEditUser ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, whiteSpace: 'nowrap' }}>
            <Switch
              checked={v === 1}
              onChange={async () => {
                await toggleUserStatus(record.id);
                Toast.success(v === 1 ? '已禁用' : '已启用');
                fetchData();
              }}
            />
            <span style={{ color: statusMeta?.color ?? '#86909c', fontSize: 12 }}>
              {statusMeta?.text || v}
            </span>
          </div>
        ) : (
          <Tag color={statusMeta?.color ?? 'grey'}>{statusMeta?.text || v}</Tag>
        )
        );
      },
    },
    ...(canEditUser ? [{
      title: '操作', width: 280,
      render: (_: unknown, record: UserVO) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' as const }}>
          <Button size="small" onClick={() => { setEditing(record); setModalVisible(true); }}>
            编辑
          </Button>
          <Button size="small" onClick={() => setResetPasswordTarget(record)}>
            重置密码
          </Button>
          <Button size="small" onClick={() => openAssignPositions(record)}>
            分配岗位
          </Button>
          <Button size="small" type="danger" onClick={async () => { await deleteUser(record.id); fetchData(); }}>
            删除
          </Button>
        </div>
      ),
    }] : []),
  ];

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <h2>员工管理</h2>
          <p>管理企业员工、部门归属、直属上级和角色</p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <Input
            placeholder="搜索用户名/姓名"
            style={{ width: 200 }}
            value={keyword}
            onChange={setKeyword}
            onEnterPress={() => { setPageNum(1); fetchData(1); }}
          />
          {canEditUser && (
            <>
              <Button onClick={() => exportUsers()}>导出 Excel</Button>
              <Button onClick={() => {
                const input = document.createElement('input');
                input.type = 'file';
                input.accept = '.xlsx,.xls';
                input.onchange = async () => {
                  const file = input.files?.[0];
                  if (!file) return;
                  const res = await importUsers(file);
                  Toast.success(`导入完成：成功 ${res.data.successCount} 条，失败 ${res.data.failCount} 条`);
                  fetchData();
                };
                input.click();
              }}>导入 Excel</Button>
              <Button type="primary" onClick={() => { setEditing(null); setModalVisible(true); }}>
                添加员工
              </Button>
            </>
          )}
        </div>
      </div>
      <Card>
        <Table
          columns={columns}
          dataSource={data}
          loading={loading}
          pagination={{ currentPage: pageNum, pageSize: 10, total, onPageChange: setPageNum }}
          rowKey="id"
        />
      </Card>

      <Modal
        title={editing ? '编辑员工' : '添加员工'}
        visible={modalVisible}
        onCancel={() => { setModalVisible(false); setEditing(null); }}
        footer={null}
        width={520}
      >
        <Form
          key={editing?.id || 'new-user'}
          onSubmit={handleSubmit}
          initValues={editing ? { ...editing, managerId: editing.managerId ?? undefined } : { status: 1, workStatus: 'active' }}
        >
          <Form.Input field="username" label="用户名" rules={[{ required: true, message: '必填' }]} disabled={!!editing} />
          {!editing && <Form.Input field="password" label="密码" type="password" placeholder="默认123456" />}
          <Form.Input field="realName" label="姓名" rules={[{ required: true, message: '必填' }]} />
          <Form.Input field="email" label="邮箱" />
          <Form.Input field="phone" label="手机" />
          <Form.Select field="deptId" label="部门" optionList={deptOptions} style={{ width: '100%' }} />
          <Form.Select
            field="managerId"
            label="直属上级"
            optionList={managerOptions}
            filter
            style={{ width: '100%' }}
            placeholder="请选择直属上级（可搜索）"
          />
          <Form.Input field="jobTitle" label="岗位" />
          <Form.Select field="workStatus" label="在岗状态" optionList={WORK_STATUS_OPTIONS} />
          <Form.Select
            field="roleIds"
            label="角色"
            multiple
            optionList={roles.map((role) => ({ label: role.roleName, value: role.id }))}
          />
          <Form.Select field="status" label="状态" optionList={ENABLED_STATUS_OPTIONS} />
          <Button htmlType="submit" type="primary" theme="solid" block style={{ marginTop: 16 }}>
            保存
          </Button>
        </Form>
      </Modal>

      <Modal
        title={`重置密码${resetPasswordTarget ? ` - ${resetPasswordTarget.realName}` : ''}`}
        visible={!!resetPasswordTarget}
        onCancel={() => setResetPasswordTarget(null)}
        footer={null}
        width={420}
      >
        <Form onSubmit={handleResetPassword} initValues={{ password: '123456' }}>
          <Form.Input
            field="password"
            label="新密码"
            type="password"
            rules={[{ required: true, message: '请输入新密码' }]}
            placeholder="请输入新密码"
          />
          <Button htmlType="submit" type="primary" theme="solid" block style={{ marginTop: 16 }}>
            确认重置
          </Button>
        </Form>
      </Modal>

      <Modal
        title={`分配岗位${assignPositionTarget ? ` - ${assignPositionTarget.realName}` : ''}`}
        visible={!!assignPositionTarget}
        onOk={handleAssignPositions}
        onCancel={() => setAssignPositionTarget(null)}
        okText="保存岗位"
        width={520}
      >
        <Form>
          <Form.Select
            field="positionIds"
            label="岗位"
            multiple
            filter
            value={selectedPositionIds}
            onChange={(value) => setSelectedPositionIds((value as number[]) || [])}
            optionList={positions.map((position) => ({
              label: `${position.positionName}${position.deptName ? ` - ${position.deptName}` : ''}`,
              value: position.id,
            }))}
            placeholder="请选择岗位"
            style={{ width: '100%' }}
          />
        </Form>
      </Modal>
    </div>
  );
}
