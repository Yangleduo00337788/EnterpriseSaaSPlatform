import { useEffect, useState } from 'react';
import {
  Table, Button, Card, Modal, Form, Select, Tag, Toast, SideSheet, Timeline, TextArea,
} from '@douyinfe/semi-ui';
import {
  getAllTemplates, createTemplate, updateTemplate, deleteTemplate,
  publishTemplate, disableTemplate, getTemplateVersions,
} from '@/api/approval';
import { getRoleOptions } from '@/api/user';
import { getUserList } from '@/api/user';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import { useRouteRefresh } from '@/hooks/useRouteRefresh';
import { usePermission } from '@/hooks/usePermission';
import { TEMPLATE_STATUS_META } from '@/utils/approvalDisplay';
import { PERM } from '@/utils/permissions';
import { APPROVER_SOURCE_OPTIONS, NODE_MODE_OPTIONS, NODE_TYPE_OPTIONS } from '@/utils/workflowDisplay';
import type { FlowNode, RoleOptionVO, TemplateVO, UserVO } from '@/types';

export default function TemplateListPage() {
  const defaultSchemaJson = JSON.stringify({
    fields: [
      { name: 'reason', label: '事由', type: 'textarea', required: true, placeholder: '请输入事由' },
      { name: 'startDate', label: '开始日期', type: 'date', required: true },
      { name: 'attachment', label: '附件', type: 'attachment' },
    ],
  }, null, 2);
  const { hasPermission } = usePermission();
  const canManage = hasPermission(PERM.TEMPLATE_MANAGE);
  const { options: categoryOptions, labelMap: categoryLabelMap } = useApprovalCategory();
  const [data, setData] = useState<TemplateVO[]>([]);
  const [users, setUsers] = useState<UserVO[]>([]);
  const [roles, setRoles] = useState<RoleOptionVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<TemplateVO | null>(null);
  const [publishTarget, setPublishTarget] = useState<TemplateVO | null>(null);
  const [publishRemark, setPublishRemark] = useState('');
  const [versionsTarget, setVersionsTarget] = useState<TemplateVO | null>(null);
  const [versions, setVersions] = useState<unknown[]>([]);
  const [nodeCount, setNodeCount] = useState(1);
  const [nodeTypes, setNodeTypes] = useState<Record<number, string>>({});
  const [nodeSources, setNodeSources] = useState<Record<number, string>>({});

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
      getRoleOptions().then((res) => setRoles(res.data)).catch(() => {});
    }
  }, [canManage]);

  const openCreate = () => {
    setEditing(null);
    setNodeCount(1);
    setNodeTypes({ 0: 'approval' });
    setNodeSources({ 0: 'users' });
    setModalVisible(true);
  };

  const openEdit = (record: TemplateVO) => {
    const nodes = record.flowNodes?.length ? record.flowNodes : [{ name: '部门主管审批', type: 'approval', approverSource: 'users', approverIds: [] }];
    setEditing(record);
    setNodeCount(nodes.length);
    setNodeTypes(Object.fromEntries(nodes.map((node, index) => [index, node.type || 'approval'])));
    setNodeSources(Object.fromEntries(nodes.map((node, index) => [index, node.approverSource || 'users'])));
    setModalVisible(true);
  };

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
    let formSchema: string | undefined;
    const formSchemaText = String(values.formSchema || '').trim();
    if (formSchemaText) {
      try {
        const parsed = JSON.parse(formSchemaText) as { fields?: unknown[] };
        if (!Array.isArray(parsed.fields)) {
          Toast.error('表单结构 JSON 必须包含 fields 数组');
          return;
        }
        formSchema = JSON.stringify(parsed);
      } catch {
        Toast.error('表单结构 JSON 格式不正确');
        return;
      }
    }
    const flowNodes = ((values.flowNodes as FlowNode[] | undefined) || [])
      .slice(0, nodeCount)
      .filter((node) => !!node?.name)
      .map((node, index) => ({
        index,
        name: node.name,
        type: node.type || 'approval',
        nodeMode: node.type === 'approval' ? (node.nodeMode || 'sequential') : undefined,
        approverSource: node.type === 'self' ? undefined : (node.approverSource || 'users'),
        approverIds: node.type === 'self'
          || node.approverSource === 'dept_leader'
          || node.approverSource === 'manager'
          ? []
          : (node.approverIds || []),
      }));
    if (flowNodes.length === 0) {
      Toast.error('至少配置一个审批节点');
      return;
    }
    const payload = {
      templateCode: values.templateCode as string,
      templateName: values.templateName as string,
      category: values.category as string,
      description: values.description as string,
      formSchema,
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
      render: (v: string) => <Tag>{categoryLabelMap[v] || v}</Tag>,
    },
    {
      title: '状态', dataIndex: 'status', width: 100,
      render: (v: number, r: TemplateVO) => (
        <Tag color={TEMPLATE_STATUS_META[v]?.color ?? 'grey'}>
          {r.statusLabel || TEMPLATE_STATUS_META[v]?.text || v}
        </Tag>
      ),
    },
    { title: '版本', dataIndex: 'pubVersion', width: 70, render: (v: number) => v > 0 ? `v${v}` : '-' },
    { title: '节点数', width: 70, render: (_: unknown, r: TemplateVO) => r.flowNodes?.length || 0 },
    ...(canManage ? [{
      title: '操作', width: 260,
      render: (_: unknown, record: TemplateVO) => (
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' as const }}>
          <Button size="small" onClick={() => openEdit(record)}>
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
          <Button type="primary" onClick={openCreate}>
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
              formSchema: editing.formSchema ? JSON.stringify(JSON.parse(editing.formSchema), null, 2) : defaultSchemaJson,
              flowNodes: editing.flowNodes?.length
                ? editing.flowNodes
                : [{ name: '部门主管审批', type: 'approval', nodeMode: 'sequential', approverSource: 'users', approverIds: [] }],
            } : {
              formSchema: defaultSchemaJson,
              flowNodes: [{ name: '部门主管审批', type: 'approval', nodeMode: 'sequential', approverSource: 'users', approverIds: [] }],
            }}
          >
            <Form.Input field="templateCode" label="模板编码" rules={[{ required: true, message: '必填' }]} />
            <Form.Input field="templateName" label="模板名称" rules={[{ required: true, message: '必填' }]} />
            <Form.Select field="category" label="分类" rules={[{ required: true, message: '必填' }]}>
              {categoryOptions.map((option) => (
                <Select.Option key={option.value} value={option.value}>{option.label}</Select.Option>
              ))}
            </Form.Select>
            <Form.TextArea field="description" label="描述" />
            <Form.TextArea
              field="formSchema"
              label="表单结构 JSON"
              rows={10}
              placeholder="请配置动态表单结构"
            />
            <div style={{ marginBottom: 16, color: '#86909c', fontSize: 12, lineHeight: 1.8 }}>
              支持字段类型: text / textarea / number / date / select / attachment。<br />
              select 字段可配置 options 数组，例如: {"{ \"name\": \"type\", \"label\": \"类型\", \"type\": \"select\", \"options\": [\"请假\", \"报销\"] }"}
            </div>
            <Form.Section text="审批节点（发布前可继续调整）">
              {Array.from({ length: nodeCount }, (_, i) => {
                const nodeType = nodeTypes[i] || editing?.flowNodes?.[i]?.type || 'approval';
                const approverSource = nodeSources[i] || editing?.flowNodes?.[i]?.approverSource || 'users';
                const approverOptions = approverSource === 'role'
                  ? roles.map((role) => ({ value: role.id, label: role.roleName }))
                  : users.map((u) => ({ value: u.id, label: u.realName }));
                return (
                <div key={i} style={{ border: '1px solid #f0f0f0', borderRadius: 6, padding: '12px 12px 0', marginBottom: 8 }}>
                  <Form.Input field={`flowNodes[${i}].name`} label={`节点${i + 1}名称`}
                    initValue={i === 0 ? '部门主管审批' : 'HR审批'} />
                  <Form.Select
                    field={`flowNodes[${i}].type`}
                    label="节点类型"
                    initValue={nodeType}
                    onChange={(value) => setNodeTypes((prev) => ({ ...prev, [i]: String(value) }))}
                  >
                    {NODE_TYPE_OPTIONS.map((option) => (
                      <Select.Option key={option.value} value={option.value}>{option.label}</Select.Option>
                    ))}
                  </Form.Select>
                  {nodeType === 'approval' && (
                    <>
                      <Form.Select field={`flowNodes[${i}].nodeMode`} label="审批模式" initValue={editing?.flowNodes?.[i]?.nodeMode || 'sequential'}>
                        {NODE_MODE_OPTIONS.map((option) => (
                          <Select.Option key={option.value} value={option.value}>{option.label}</Select.Option>
                        ))}
                      </Form.Select>
                      <Form.Select
                        field={`flowNodes[${i}].approverSource`}
                        label="审批人来源"
                        initValue={approverSource}
                        onChange={(value) => setNodeSources((prev) => ({ ...prev, [i]: String(value) }))}
                      >
                        {APPROVER_SOURCE_OPTIONS.map((option) => (
                          <Select.Option key={option.value} value={option.value}>{option.label}</Select.Option>
                        ))}
                      </Form.Select>
                      {approverSource !== 'dept_leader' && approverSource !== 'manager' && (
                        <Form.Select field={`flowNodes[${i}].approverIds`} label={approverSource === 'role' ? '指定角色' : '指定审批人'} multiple filter>
                          {approverOptions.map((option) => (
                            <Select.Option key={option.value} value={option.value}>{option.label}</Select.Option>
                          ))}
                        </Form.Select>
                      )}
                    </>
                  )}
                  {nodeType === 'self' && (
                    <Tag color="blue" style={{ marginBottom: 12 }}>自审节点不需要额外配置审批人</Tag>
                  )}
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                    <span style={{ color: '#86909c', fontSize: 12 }}>节点顺序由上到下依次执行</span>
                    {nodeCount > 1 && (
                      <Button
                        size="small"
                        type="danger"
                        theme="borderless"
                        onClick={() => {
                          setNodeCount((prev) => Math.max(prev - 1, 1));
                          setNodeTypes((prev) => {
                            const next = { ...prev };
                            delete next[i];
                            return next;
                          });
                          setNodeSources((prev) => {
                            const next = { ...prev };
                            delete next[i];
                            return next;
                          });
                        }}
                      >
                        删除节点
                      </Button>
                    )}
                  </div>
                </div>
                );
              })}
              <Button
                theme="light"
                onClick={() => {
                  const nextIndex = nodeCount;
                  setNodeCount((prev) => prev + 1);
                  setNodeTypes((prev) => ({ ...prev, [nextIndex]: 'approval' }));
                  setNodeSources((prev) => ({ ...prev, [nextIndex]: 'users' }));
                }}
              >
                新增节点
              </Button>
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
