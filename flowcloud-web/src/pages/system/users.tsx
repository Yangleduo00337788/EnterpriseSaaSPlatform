import { useEffect, useMemo, useState } from 'react';
import { Table, Button, Card, Modal, Form, Tag, Toast, Input, Switch } from '@douyinfe/semi-ui';
import { getUserList, createUser, updateUser, deleteUser, toggleUserStatus, getRoleOptions, getUserOptions, exportUsers, importUsers } from '@/api/user';
import { getDeptTree } from '@/api/dept';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import type { DeptVO, RoleOptionVO, UserOptionVO, UserVO } from '@/types';

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
  const [userOptions, setUserOptions] = useState<UserOptionVO[]>([]);

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
    getUserOptions().then((res) => setUserOptions(res.data)).catch(() => {});
  }, []);

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
      render: (v: number, record: UserVO) =>
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
            <span style={{ color: v === 1 ? '#00b42a' : '#f53f3f', fontSize: 12 }}>
              {v === 1 ? '启用' : '禁用'}
            </span>
          </div>
        ) : (
          <Tag color={v === 1 ? 'green' : 'red'}>{v === 1 ? '启用' : '禁用'}</Tag>
        ),
    },
    ...(canEditUser ? [{
      title: '操作', width: 140,
      render: (_: unknown, record: UserVO) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, whiteSpace: 'nowrap' }}>
          <Button size="small" onClick={() => { setEditing(record); setModalVisible(true); }}>
            编辑
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
          <Form.Select field="workStatus" label="在岗状态" optionList={[
            { label: '在岗', value: 'active' },
            { label: '试用', value: 'probation' },
            { label: '离职', value: 'inactive' },
          ]} />
          <Form.Select
            field="roleIds"
            label="角色"
            multiple
            optionList={roles.map((role) => ({ label: role.roleName, value: role.id }))}
          />
          <Form.Select field="status" label="状态" optionList={[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]} />
          <Button htmlType="submit" type="primary" theme="solid" block style={{ marginTop: 16 }}>
            保存
          </Button>
        </Form>
      </Modal>
    </div>
  );
}