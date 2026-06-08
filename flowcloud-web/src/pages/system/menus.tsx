import { useEffect, useMemo, useState } from 'react';
import {
  Button, Card, Form, Modal, Table, Tag, Toast, Space,
} from '@douyinfe/semi-ui';
import { createMenu, deleteMenu, getMenuTree, updateMenu } from '@/api/menu';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { ENABLED_STATUS_META, ENABLED_STATUS_OPTIONS } from '@/utils/statusDisplay';
import type { MenuVO } from '@/types';

const MENU_TYPE_OPTIONS = [
  { value: 'menu', label: '菜单' },
  { value: 'button', label: '按钮' },
];

const MENU_TYPE_META: Record<string, { label: string; color: 'blue' | 'green' | 'grey' }> = {
  menu: { label: '菜单', color: 'blue' },
  button: { label: '按钮', color: 'green' },
};

function flattenMenuTree(nodes: MenuVO[], level = 0): Array<{ value: number; label: string }> {
  return nodes.flatMap((node) => {
    if (node.permType === 'button') {
      return [];
    }
    return [
      { value: node.id, label: `${'　'.repeat(level)}${node.permName}` },
      ...flattenMenuTree(node.children || [], level + 1),
    ];
  });
}

export default function MenusPage() {
  const { hasPermission } = usePermission();
  const canEdit = hasPermission(PERM.MENU_EDIT);
  const [data, setData] = useState<MenuVO[]>([]);
  const [menuOptions, setMenuOptions] = useState<Array<{ value: number; label: string }>>([]);
  const [visible, setVisible] = useState(false);
  const [editing, setEditing] = useState<MenuVO | null>(null);

  const fetchData = async () => {
    const res = await getMenuTree();
    setData(res.data);
    setMenuOptions(flattenMenuTree(res.data));
  };

  useEffect(() => {
    fetchData();
  }, []);

  const parentOptions = useMemo(
    () => [{ value: 0, label: '（无，根节点）' }, ...menuOptions.filter((item) => item.value !== editing?.id)],
    [editing?.id, menuOptions],
  );

  const handleSubmit = async (values: Record<string, unknown>) => {
    const payload = values as Partial<MenuVO>;
    if (editing) {
      await updateMenu(editing.id, payload);
      Toast.success('更新成功');
    } else {
      await createMenu(payload);
      Toast.success('创建成功');
    }
    setVisible(false);
    setEditing(null);
    fetchData();
  };

  const handleDelete = (record: MenuVO) => {
    Modal.confirm({
      title: '删除菜单',
      content: `确定删除「${record.permName}」吗？请确保已先删除其子节点。`,
      onOk: async () => {
        await deleteMenu(record.id);
        Toast.success('删除成功');
        fetchData();
      },
    });
  };

  return (
    <div className="page-container">
      <PageHeader
        title="菜单管理"
        description="维护系统菜单与按钮权限树，角色授权会自动使用这里的配置"
        actions={canEdit ? (
          <Button type="primary" theme="solid" onClick={() => { setEditing(null); setVisible(true); }}>
            新建菜单
          </Button>
        ) : undefined}
      />
      <Card>
        <Table
          rowKey="id"
          dataSource={data}
          pagination={false}
          columns={[
            { title: '菜单名称', dataIndex: 'permName', width: 220 },
            { title: '权限编码', dataIndex: 'permCode', width: 220 },
            {
              title: '类型',
              dataIndex: 'permType',
              width: 100,
              render: (value: string) => {
                const meta = MENU_TYPE_META[value] || { label: value || '-', color: 'grey' as const };
                return <Tag color={meta.color}>{meta.label}</Tag>;
              },
            },
            { title: '路由', dataIndex: 'path', width: 220, render: (value?: string) => value || '-' },
            { title: '图标', dataIndex: 'icon', width: 140, render: (value?: string) => value || '-' },
            { title: '排序', dataIndex: 'sort', width: 80 },
            {
              title: '状态',
              dataIndex: 'status',
              width: 90,
              render: (value: number) => <Tag color={ENABLED_STATUS_META[value]?.color ?? 'grey'}>{ENABLED_STATUS_META[value]?.text || value}</Tag>,
            },
            ...(canEdit ? [{
              title: '操作',
              width: 180,
              render: (_: unknown, record: MenuVO) => (
                <Space spacing={8} className="page-inline-actions">
                  <Button size="small" onClick={() => { setEditing(record); setVisible(true); }}>编辑</Button>
                  <Button size="small" type="danger" onClick={() => handleDelete(record)}>删除</Button>
                </Space>
              ),
            }] : []),
          ]}
        />
      </Card>

      <Modal
        title={editing ? '编辑菜单' : '新建菜单'}
        visible={visible}
        onCancel={() => { setVisible(false); setEditing(null); }}
        footer={null}
        width={520}
      >
        <Form
          key={editing?.id || 'new-menu'}
          onSubmit={handleSubmit}
          initValues={editing || { parentId: 0, permType: 'menu', sort: 0, status: 1 }}
        >
          <Form.Select
            field="parentId"
            label="上级菜单"
            optionList={parentOptions}
            style={{ width: '100%' }}
          />
          <Form.Input field="permName" label="菜单名称" rules={[{ required: true, message: '必填' }]} />
          <Form.Input field="permCode" label="权限编码" rules={[{ required: true, message: '必填' }]} />
          <Form.Select
            field="permType"
            label="菜单类型"
            optionList={MENU_TYPE_OPTIONS}
            style={{ width: '100%' }}
          />
          <Form.Input field="path" label="路由路径" placeholder="菜单必填，如 /system/menus" />
          <Form.Input field="icon" label="图标标识" placeholder="如 IconSetting" />
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
