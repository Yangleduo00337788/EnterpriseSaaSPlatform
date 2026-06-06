import { useEffect, useState } from 'react';
import {
  Table, Button, Card, Modal, Form, Select, Tag, Toast, SideSheet, Timeline, TextArea,
} from '@douyinfe/semi-ui';
import {
  getAllTemplates, createTemplate, updateTemplate, deleteTemplate,
  publishTemplate, disableTemplate, getTemplateVersions,
} from '@/api/approval';
import { getUserList } from '@/api/user';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { PERM } from '@/utils/permissions';
import { CATEGORY_MAP, APPROVER_TYPE_MAP } from '@/utils/constants';
import type { TemplateVO, UserVO } from '@/types';

const STATUS_COLOR: Record<number, 'green' | 'grey' | 'red' | 'orange'> = {
  0: 'orange', // 草稿
  1: 'green',  // 已发布
  2: 'grey',   // 已停用
};

export default function TemplateListPage() {
  const { hasPermission } = usePermission();
  const canManage = hasPermission(PERM.TEMPLATE_MANAGE);
  const [data, setData] = useState<TemplateVO[]>([]);
  const [users, setUsers] = useState<UserVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<TemplateVO | null>(null);
  const [publishTarget, setPublishTarget] = useState<TemplateVO | null>(null);
  const [publishRemark, setPublishRemark] = useState('');
  const [versionsTarget, setVersionsTarget] = useState<TemplateVO | null>(null);
  const [versions, setVersions] = useState<unknown[]>([]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = canManage ? await getAllTemplates() : await import('@/api/approval').then(m => m.getTemplates());
      setData(res.data);
    } finally {
      setLoading(false);
    }
  };

  useRouteRefresh(() => fetchData());

  useEffect(() => {
    if (canManage) {
      getUserList({ pageSize: 100 }).then((res) => setUsers(res.data.records));
    }
  }, [canManage]);

  const openVersions = async (record: TemplateVO) => {
    setVersionsTarget(record);
    const res = await getTemplateVersions(record.id);
    setVersions(res.data);
  };

  const handlePublish = async () => {
    if (!publishTarget) return;
    await publishTemplate(publishTarget.id, publishRemark);
    Toast.success('发布成功');
    setPublishTarget(null);
    setPublishRemark('');
    fetchData();
  };

  const handleDisable = async (record: TemplateVO) => {
    await disableTemplate(record.id);
    Toast.success('已停用');
    fetchData();
  };

  const handleSubmit = async (values: Record<string, unknown>) => {
    const flowNodes = (values.flowNodes as { name: string; type?: string; approverIds: number[] }[] || [])
      .map((node, index) => ({
        index,
        name: node.name,
        type: node.type || 'approval',
        approverIds: node.type === 'self' ? [] : (node.approverIds || []),
      }));
    const payload = {
      templateCode: values.templateCode as string,
      templateName: values.templateName as string,
      category: values.category as string,
      description: values.description as string,
      flowNodes,
    };
    if (editing) {
      await updateTemplate(editing.id, payload);
      Toast.success('保存成功，模板已退回草稿，请重新发布');
    } else {
      await createTemplate(payload);
      Toast.success('创建成功，请发布后方可使用');
    }
    setModalVisible(false);
    setEditing(null);
    fetchData();
  };

  const columns = [
    { title: '模板编码', dataIndex: 'templateCode', width: 140 },
    { title: '模板名称', dataIndex: 'templateName' },
    {
      title: '分类', dataIndex: 'category', width: 100,
      render: (v: string) => <Tag>{CATEGORY_MAP[v] || v}</Tag>,
    },
    {
      title: '状态', dataIndex: 'status', width: 100,
      render: (v: number, r: TemplateVO) => (
        <Tag color={STATUS_COLOR[v] ?? 'grey'}>
          {r.statusLabel || ['草稿', '已发布', '已停用'][v] || v}
        </Tag>
      ),
    },
    { title: '版本', dataIndex: 'pubVersion', width: 70, render: (v: number) => v > 0 ? `v${v}` : '-' },
    { title: '节点数', width: 70, render: (_: unknown, r: TemplateVO) => r.flowNodes?.length || 0 },
    ...(canManage ? [{
      title: '操作', width: 260,
      render: (_: unknown, record: TemplateVO) => (
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' as const }}>
          <Button size="small" onClick={() => { setEditing(record); setModalVisible(true); }}>
            编辑
          </Button>
          {record.status !== 1 && (
            <Button size="small" type="primary"
              onClick={() => { setPublishTarget(record); setPublishRemark(''); }}>
              发布
            </Button>
          )}
          {record.status === 1 && (
            <Button size="small" onClick={() => handleDisable(record)}>
              停用
            </Button>
          )}
          <Button size="small" onClick={() => openVersions(record)}>版本</Button>
          <Button size="small" type="danger"
            onClick={async () => { await deleteTemplate(record.id); fetchData(); }}>
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
          <h2>流程模板</h2>
          <p>草稿 → 发布（生效）→ 停用；发布后产生版本快照</p>
        </div>
        {canManage && (
          <Button type="primary" onClick={() => { setEditing(null); setModalVisible(true); }}>
            新建模板
          </Button>
        )}
      </div>
      <Card>
        <Table columns={columns} dataSource={data} loading={loading} rowKey="id" pagination={false} />
      </Card>

      {/* 新建/编辑 Modal */}
      {canManage && (
        <Modal
          title={editing ? '编辑模板（保存后退回草稿）' : '新建模板'}
          visible={modalVisible}
          onCancel={() => { setModalVisible(false); setEditing(null); }}
          footer={null}
          width={600}
        >
          <Form
            onSubmit={handleSubmit}
            initValues={editing ? {
              templateCode: editing.templateCode,
              templateName: editing.templateName,
              category: editing.category,
              description: editing.description,
              flowNodes: editing.flowNodes || [{ name: '部门主管审批', approverIds: [] }],
            } : {
              flowNodes: [{ name: '部门主管审批', approverIds: [] }],
            }}
          >
            <Form.Input field="templateCode" label="模板编码" rules={[{ required: true, message: '必填' }]} />
            <Form.Input field="templateName" label="模板名称" rules={[{ required: true, message: '必填' }]} />
            <Form.Select field="category" label="分类" rules={[{ required: true, message: '必填' }]}>
              {Object.entries(CATEGORY_MAP).map(([k, v]) => (
                <Select.Option key={k} value={k}>{v}</Select.Option>
              ))}
            </Form.Select>
            <Form.TextArea field="description" label="描述" />
            <Form.Section text="审批节点（发布前可继续调整）">
              {[0, 1].map((i) => (
                <div key={i} style={{ border: '1px solid #f0f0f0', borderRadius: 6, padding: '12px 12px 0', marginBottom: 8 }}>
                  <Form.Input field={`flowNodes[${i}].name`} label={`节点${i + 1}名称`}
                    initValue={i === 0 ? '部门主管审批' : 'HR审批'} />
                  <Form.Select field={`flowNodes[${i}].type`} label="审批人来源" initValue="approval">
                    {Object.entries(APPROVER_TYPE_MAP).map(([k, v]) => (
                      <Select.Option key={k} value={k}>{v}</Select.Option>
                    ))}
                  </Form.Select>
                  <Form.Select field={`flowNodes[${i}].approverIds`} label="指定审批人" multiple filter>
                    {users.map((u) => (
                      <Select.Option key={u.id} value={u.id}>{u.realName}</Select.Option>
                    ))}
                  </Form.Select>
                </div>
              ))}
            </Form.Section>
            <Button htmlType="submit" type="primary" theme="solid" block style={{ marginTop: 16 }}>
              保存草稿
            </Button>
          </Form>
        </Modal>
      )}

      {/* 发布确认 Modal */}
      <Modal
        title={`发布模板：${publishTarget?.templateName}`}
        visible={!!publishTarget}
        onOk={handlePublish}
        onCancel={() => setPublishTarget(null)}
        okText="确认发布"
      >
        <TextArea
          value={publishRemark}
          onChange={(v) => setPublishRemark(v)}
          placeholder="发布备注（可选）"
          rows={3}
        />
      </Modal>

      {/* 版本历史 SideSheet */}
      <SideSheet
        title={`版本历史：${versionsTarget?.templateName}`}
        visible={!!versionsTarget}
        onCancel={() => { setVersionsTarget(null); setVersions([]); }}
        width={400}
      >
        {versions.length === 0
          ? <p style={{ color: '#86909c' }}>暂无发布记录</p>
          : (
            <Timeline>
              {(versions as Array<{ version: number; remark?: string; createTime: string }>).map((v) => (
                <Timeline.Item key={v.version}>
                  <strong>v{v.version}</strong>
                  {v.remark && <span style={{ marginLeft: 8, color: '#4e5969' }}>{v.remark}</span>}
                  <div style={{ color: '#86909c', fontSize: 12 }}>{v.createTime}</div>
                </Timeline.Item>
              ))}
            </Timeline>
          )}
      </SideSheet>
    </div>
  );
}