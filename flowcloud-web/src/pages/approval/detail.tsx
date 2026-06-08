import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  Card, Tag, Timeline, Spin, Descriptions, Button, Form, Modal, Toast, Collapse, Space,
} from '@douyinfe/semi-ui';
import { cancelInstance, completeTask, getInstanceDetail, getTemplate, remindTask } from '@/api/approval';
import { deleteAttachment, getAttachments } from '@/api/attachment';
import type { AttachmentVO } from '@/api/attachment';
import { PageActionGroup, PageFormActions, PageHeader } from '@/components/page-kit';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import { useApprovalStatus } from '@/hooks/useApprovalStatus';
import { useAppSelector } from '@/hooks/useAppDispatch';
import { openAttachmentPreview } from '@/utils/attachmentPreview';
import type { InstanceVO, TemplateVO } from '@/types';

interface SchemaField {
  name: string;
  label: string;
  type: string;
}

function parseSchema(raw?: string): SchemaField[] {
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed.fields) ? parsed.fields : [];
  } catch {
    return [];
  }
}

function parseFormData(raw?: string): Record<string, unknown> {
  if (!raw) return {};
  try {
    return JSON.parse(raw);
  } catch {
    return { _raw: raw };
  }
}

function formatValue(val: unknown): string {
  if (val === null || val === undefined || val === '') return '-';
  if (typeof val === 'object') return JSON.stringify(val);
  return String(val);
}

function FormDataDisplay({
  formData,
  formSchema,
  attachments,
  canDeleteAttachment,
  onDeleteAttachment,
}: {
  formData?: string;
  formSchema?: string;
  attachments: AttachmentVO[];
  canDeleteAttachment: boolean;
  onDeleteAttachment: (attachment: AttachmentVO) => void;
}) {
  const fields = parseSchema(formSchema);
  const data = parseFormData(formData);

  if (fields.length === 0) {
    return formData ? (
      <Descriptions.Item itemKey="申请内容">{formData}</Descriptions.Item>
    ) : null;
  }

  return (
    <>
      {fields.map((field) => {
        if (field.type === 'attachment') {
          const fieldAttachments = attachments.filter((a) => a.fieldName === field.name);
          return (
            <Descriptions.Item key={field.name} itemKey={field.label}>
              {fieldAttachments.length === 0 ? '-' : (
                <div className="page-link-list">
                  {fieldAttachments.map((att) => (
                    <div key={att.id} className="page-link-row">
                      <a
                        href="#"
                        onClick={(event) => {
                          event.preventDefault();
                          openAttachmentPreview(att);
                        }}
                      >
                        {att.originalName}
                        {att.fileSize ? ` (${(att.fileSize / 1024).toFixed(1)}KB)` : ''}
                      </a>
                      {canDeleteAttachment && (
                        <Button
                          size="small"
                          type="danger"
                          theme="light"
                          className="page-action-button page-action-button-danger"
                          onClick={() => onDeleteAttachment(att)}
                        >
                          删除
                        </Button>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </Descriptions.Item>
          );
        }
        return (
          <Descriptions.Item key={field.name} itemKey={field.label}>
            {formatValue(data[field.name])}
          </Descriptions.Item>
        );
      })}
    </>
  );
}

export default function InstanceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const user = useAppSelector((s) => s.auth.user);
  const [data, setData] = useState<InstanceVO | null>(null);
  const [template, setTemplate] = useState<TemplateVO | null>(null);
  const [attachments, setAttachments] = useState<AttachmentVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionModal, setActionModal] = useState<{ visible: boolean; action?: string }>({ visible: false });
  const { getStatusMeta } = useApprovalStatus();
  const { labelMap: categoryLabelMap } = useApprovalCategory();

  const loadDetail = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const res = await getInstanceDetail(Number(id));
      setData(res.data);

      // 并行拉取模板 schema 和附件
      if (res.data?.templateId) {
        getTemplate(res.data.templateId).then((r) => setTemplate(r.data)).catch(() => {});
      }
      getAttachments('instance', Number(id)).then((r) => setAttachments(r.data)).catch(() => {});
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadDetail(); }, [id]);

  const currentPendingTask = useMemo(
    () => data?.tasks?.find((task) => task.status === 'pending' && task.approverId === user?.userId),
    [data?.tasks, user?.userId],
  );
  const canCancel = data?.status === 'pending' && data.applicantId === user?.userId;
  const pendingTaskForRemind = useMemo(
    () => data?.tasks?.find((task) => task.status === 'pending'),
    [data?.tasks],
  );
  const canRemind = data?.status === 'pending' && data.applicantId === user?.userId && !!pendingTaskForRemind;
  const canDeleteAttachment = data?.status === 'pending' && data.applicantId === user?.userId;

  const handleTaskAction = async (values: { comment?: string }) => {
    if (!currentPendingTask || !actionModal.action) return;
    await completeTask({
      taskId: currentPendingTask.id,
      action: actionModal.action,
      comment: values.comment,
    });
    Toast.success(actionModal.action === 'approve' ? '审批通过' : '已驳回');
    setActionModal({ visible: false });
    loadDetail();
  };

  const handleCancel = async () => {
    if (!data) return;
    await cancelInstance(data.id);
    Toast.success('已撤销');
    loadDetail();
  };

  const handleRemind = async () => {
    if (!pendingTaskForRemind) return;
    await remindTask(pendingTaskForRemind.id);
    Toast.success('已发送催办提醒');
  };

  const handleDeleteAttachment = (attachment: AttachmentVO) => {
    Modal.confirm({
      title: '确认删除附件',
      content: `确定删除附件「${attachment.originalName}」吗？`,
      onOk: async () => {
        await deleteAttachment(attachment.id);
        Toast.success('附件已删除');
        setAttachments((prev) => prev.filter((item) => item.id !== attachment.id));
      },
    });
  };

  if (loading) return <Spin className="page-loading-block" />;
  if (!data) return <div className="page-container">审批单不存在</div>;

  const statusInfo = getStatusMeta(data.status);

  return (
    <div className="page-container">
      <PageHeader
        title="审批详情"
        description={data.instanceNo}
        actions={(
          <PageActionGroup>
            {currentPendingTask && (
              <>
                <Button
                  type="primary"
                  theme="solid"
                  className="page-action-button page-action-button-primary"
                  onClick={() => setActionModal({ visible: true, action: 'approve' })}
                >
                  通过
                </Button>
                <Button
                  type="danger"
                  theme="light"
                  className="page-action-button page-action-button-danger"
                  onClick={() => setActionModal({ visible: true, action: 'reject' })}
                >
                  驳回
                </Button>
              </>
            )}
            {canRemind && (
              <Button
                type="tertiary"
                theme="light"
                className="page-action-button page-action-button-secondary"
                onClick={handleRemind}
              >
                催办
              </Button>
            )}
            {canCancel && (
              <Button
                type="danger"
                theme="light"
                className="page-action-button page-action-button-danger"
                onClick={handleCancel}
              >
                撤销申请
              </Button>
            )}
          </PageActionGroup>
        )}
      />

      <Card title="基本信息" className="page-card-stack page-detail-card">
        <Descriptions align="left" className="page-detail-descriptions">
          <Descriptions.Item itemKey="标题">{data.title}</Descriptions.Item>
          <Descriptions.Item itemKey="类型">{categoryLabelMap[data.category] || data.category}</Descriptions.Item>
          <Descriptions.Item itemKey="申请人">{data.applicantName}</Descriptions.Item>
          <Descriptions.Item itemKey="状态">
            <Tag
              theme="light"
              color={statusInfo?.color as 'blue'}
              className={`page-status-tag page-status-tag-${statusInfo.tone}`}
            >
              {statusInfo?.text || data.status}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item itemKey="提交时间">{data.submitTime}</Descriptions.Item>
          {data.finishTime && (
            <Descriptions.Item itemKey="完成时间">{data.finishTime}</Descriptions.Item>
          )}
        </Descriptions>
      </Card>

      <Collapse defaultActiveKey={['content', 'timeline']} className="page-detail-collapse">
        <Collapse.Panel header="申请内容" itemKey="content">
          <Descriptions align="left" className="page-detail-descriptions">
            <FormDataDisplay
              formData={data.formData}
              formSchema={template?.formSchema}
              attachments={attachments}
              canDeleteAttachment={!!canDeleteAttachment}
              onDeleteAttachment={handleDeleteAttachment}
            />
          </Descriptions>
        </Collapse.Panel>
        <Collapse.Panel header="审批流程" itemKey="timeline">
          <Timeline className="page-detail-timeline">
            {(data.records || []).map((record, index) => (
              <Timeline.Item
                key={index}
                type={record.action === 'reject' ? 'error' : record.action === 'approve' ? 'success' : 'ongoing'}
                time={record.createTime}
              >
                <div className="page-timeline-title">{record.nodeName}</div>
                <div className="page-timeline-meta">
                  {record.operatorName} · {record.action === 'submit' ? '提交' : record.action === 'approve' ? '通过' : '驳回'}
                </div>
                {record.comment && <div className="page-timeline-comment">{record.comment}</div>}
              </Timeline.Item>
            ))}
          </Timeline>
        </Collapse.Panel>
      </Collapse>

      <Modal
        title={actionModal.action === 'approve' ? '审批通过' : '审批驳回'}
        visible={actionModal.visible}
        onCancel={() => setActionModal({ visible: false })}
        footer={null}
      >
        <Form onSubmit={handleTaskAction}>
          <Form.TextArea field="comment" label="审批意见" placeholder="请输入审批意见（可选）" />
          <PageFormActions>
            <Button
              type="tertiary"
              theme="light"
              className="page-action-button"
              onClick={() => setActionModal({ visible: false })}
            >
              取消
            </Button>
            <Button
              htmlType="submit"
              type="primary"
              theme="solid"
              className="page-action-button page-action-button-primary"
            >
              确认
            </Button>
          </PageFormActions>
        </Form>
      </Modal>
    </div>
  );
}
