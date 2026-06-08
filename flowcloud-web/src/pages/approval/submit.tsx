import { useEffect, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Card, Form, Button, Select, Toast, Upload, Tag, Steps,
} from '@douyinfe/semi-ui';
import type { FormApi } from '@douyinfe/semi-ui/lib/es/form';
import { getTemplates, submitApproval } from '@/api/approval';
import { uploadAttachment } from '@/api/attachment';
import { PageFormActions, PageHeader } from '@/components/page-kit';
import { useApprovalCategory } from '@/hooks/useApprovalCategory';
import type { TemplateVO } from '@/types';

interface SchemaField {
  name: string;
  label: string;
  type: 'text' | 'textarea' | 'number' | 'date' | 'select' | 'attachment';
  required?: boolean;
  options?: string[];
  placeholder?: string;
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

function SchemaFieldItem({
  field,
  attachments,
  onAddAttachment,
  onRemoveAttachment,
}: {
  field: SchemaField;
  attachments: File[];
  onAddAttachment: (fieldName: string, file: File) => void;
  onRemoveAttachment: (fieldName: string, index: number) => void;
}) {
  const baseRules = field.required ? [{ required: true, message: `请填写${field.label}` }] : [];
  const ph = field.placeholder || `请输入${field.label}`;

  switch (field.type) {
    case 'select':
      return (
        <Form.Select
          field={`__schema_${field.name}`}
          label={field.label}
          rules={baseRules}
          placeholder={`请选择${field.label}`}
          style={{ width: '100%' }}
        >
          {(field.options || []).map((opt) => (
            <Select.Option key={opt} value={opt}>{opt}</Select.Option>
          ))}
        </Form.Select>
      );
    case 'date':
      return (
        <Form.DatePicker
          field={`__schema_${field.name}`}
          label={field.label}
          rules={baseRules}
          placeholder={`请选择${field.label}`}
          style={{ width: '100%' }}
          format="yyyy-MM-dd"
          type="date"
        />
      );
    case 'number':
      return (
        <Form.InputNumber
          field={`__schema_${field.name}`}
          label={field.label}
          rules={baseRules}
          placeholder={ph}
          style={{ width: '100%' }}
        />
      );
    case 'textarea':
      return (
        <Form.TextArea
          field={`__schema_${field.name}`}
          label={field.label}
          rules={baseRules}
          placeholder={ph}
          rows={3}
        />
      );
    case 'attachment':
      return (
        <Form.Slot label={field.label}>
          <Upload
            action=""
            customRequest={async ({ file, onSuccess, onError }) => {
              try {
                const fileObj = (file as { fileInstance?: File }).fileInstance ?? (file as unknown as File);
                onAddAttachment(field.name, fileObj);
                onSuccess?.({});
              } catch {
                onError?.({ status: 500 });
              }
            }}
            multiple
            accept="*/*"
            showUploadList={false}
          >
            <Button theme="light" className="page-toolbar-button-secondary">点击上传附件</Button>
          </Upload>
          {attachments.length > 0 && (
            <div className="page-link-row" style={{ marginTop: 8 }}>
              {attachments.map((file, index) => (
                <Tag
                  key={`${field.name}-${file.name}-${index}`}
                  closable
                  onClose={() => onRemoveAttachment(field.name, index)}
                >
                  {file.name}
                </Tag>
              ))}
            </div>
          )}
        </Form.Slot>
      );
    default:
      return (
        <Form.Input
          field={`__schema_${field.name}`}
          label={field.label}
          rules={baseRules}
          placeholder={ph}
        />
      );
  }
}

export default function SubmitApprovalPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const category = searchParams.get('category');
  const [templates, setTemplates] = useState<TemplateVO[]>([]);
  const [schemaFields, setSchemaFields] = useState<SchemaField[]>([]);
  const [loading, setLoading] = useState(false);
  const [formApi, setFormApi] = useState<FormApi>();
  const prevTplId = useRef<number | null>(null);
  const [selectedTemplateId, setSelectedTemplateId] = useState<number>();
  const [pendingAttachments, setPendingAttachments] = useState<Record<string, File[]>>({});
  const { labelMap: categoryLabelMap } = useApprovalCategory();
  const submitSteps = [
    { title: '选择模板', description: '确认审批类型与流程模板' },
    { title: '填写内容', description: '补充申请信息与附件材料' },
    { title: '提交审批', description: '创建流程并进入审批详情' },
  ];
  const currentStep = loading ? 2 : selectedTemplateId ? 1 : 0;
  const currentStepInfo = submitSteps[currentStep];

  useEffect(() => {
    getTemplates(category || undefined).then((res) => setTemplates(res.data));
  }, [category]);

  useEffect(() => {
    if (!formApi || templates.length === 0) return;
    const tpl = category
      ? templates.find((t) => t.category === category)
      : templates[0];
    if (tpl) {
      formApi.setValue('templateId', tpl.id);
      if (!formApi.getValue('title')) {
        formApi.setValue('title', `${tpl.templateName}申请`);
      }
      setSelectedTemplateId(tpl.id);
      setSchemaFields(parseSchema(tpl.formSchema));
      prevTplId.current = tpl.id;
      setPendingAttachments({});
    }
  }, [formApi, templates, category]);

  const handleTemplateChange = (id: number) => {
    const tpl = templates.find((t) => t.id === id);
    if (!tpl || !formApi) return;

    // 清空上一个模板的 schema 字段值
    if (prevTplId.current !== null) {
      const prev = templates.find((t) => t.id === prevTplId.current);
      if (prev) {
        parseSchema(prev.formSchema).forEach((f) => {
          formApi.setValue(`__schema_${f.name}`, undefined);
        });
      }
    }
    prevTplId.current = id;
    setSelectedTemplateId(id);
    setPendingAttachments({});

    if (!formApi.getValue('title') || formApi.getValue('title').endsWith('申请')) {
      formApi.setValue('title', `${tpl.templateName}申请`);
    }
    setSchemaFields(parseSchema(tpl.formSchema));
  };

  const addAttachment = (fieldName: string, file: File) => {
    setPendingAttachments((prev) => ({
      ...prev,
      [fieldName]: [...(prev[fieldName] || []), file],
    }));
  };

  const removeAttachment = (fieldName: string, index: number) => {
    setPendingAttachments((prev) => ({
      ...prev,
      [fieldName]: (prev[fieldName] || []).filter((_, i) => i !== index),
    }));
  };

  const handleSubmit = async (values: Record<string, unknown>) => {
    const { templateId, title, ...rest } = values;
    let formData: string | undefined;

    if (schemaFields.length === 0) {
      const fallbackRemark = rest.__fallback_remark;
      formData = fallbackRemark ? String(fallbackRemark) : undefined;
    } else {
      // 把 __schema_xxx 字段合并为 formData JSON
      const formDataObj: Record<string, unknown> = {};
      schemaFields.forEach((f) => {
        if (f.type === 'attachment') {
          return;
        }
        const v = rest[`__schema_${f.name}`];
        if (v !== undefined && v !== null && v !== '') {
          formDataObj[f.name] = v;
        }
      });
      formData = Object.keys(formDataObj).length ? JSON.stringify(formDataObj) : undefined;
    }

    setLoading(true);
    try {
      const res = await submitApproval({
        templateId: templateId as number,
        title: title as string,
        formData,
      });
      const instanceId = res.data;
      const uploadTasks = schemaFields
        .filter((field) => field.type === 'attachment')
        .flatMap((field) =>
          (pendingAttachments[field.name] || []).map((file) =>
            uploadAttachment(file, 'instance', instanceId, field.name),
          ),
        );
      const uploadResults = await Promise.allSettled(uploadTasks);
      const failedUploads = uploadResults.filter((item) => item.status === 'rejected').length;
      if (failedUploads > 0) {
        Toast.warning(`审批已提交，但有 ${failedUploads} 个附件上传失败`);
      } else {
        Toast.success('提交成功');
      }
      navigate(`/approval/detail/${res.data}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <PageHeader
        title="发起审批"
        description="选择审批类型并填写信息"
      />
      <Card className="page-step-card submit-step-card">
        <div className="submit-step-shell">
          <div className="submit-step-scroll">
            <Steps current={currentStep} type="nav" className="submit-nav-steps">
              {submitSteps.map((step) => (
                <Steps.Step key={step.title} title={step.title} />
              ))}
            </Steps>
          </div>
          <div className="submit-step-meta">
            <span className="submit-step-label">当前阶段</span>
            <div className="submit-step-copy">
              <div className="submit-step-title">{currentStepInfo.title}</div>
              <div className="submit-step-description">{currentStepInfo.description}</div>
            </div>
          </div>
        </div>
      </Card>
      <Card className="page-form-shell">
        <Form
          getFormApi={setFormApi}
          onSubmit={handleSubmit}
          labelPosition="left"
          labelWidth={110}
        >
          <Form.Select
            field="templateId"
            label="审批类型"
            rules={[{ required: true, message: '请选择审批类型' }]}
            placeholder="请选择"
            style={{ width: '100%' }}
            onChange={(v) => handleTemplateChange(v as number)}
          >
            {templates.map((t) => (
              <Select.Option key={t.id} value={t.id}>
                {t.templateName}（{categoryLabelMap[t.category] || t.category}）
              </Select.Option>
            ))}
          </Form.Select>

          <Form.Input
            field="title"
            label="审批标题"
            rules={[{ required: true, message: '请输入标题' }]}
            placeholder="请输入审批标题"
          />

          {schemaFields.map((field) => (
            <SchemaFieldItem
              key={field.name}
              field={field}
              attachments={pendingAttachments[field.name] || []}
              onAddAttachment={addAttachment}
              onRemoveAttachment={removeAttachment}
            />
          ))}

          {schemaFields.length === 0 && (
            <Form.TextArea
              field="__fallback_remark"
              label="申请说明"
              placeholder="请输入申请详情"
              rows={4}
            />
          )}

          <PageFormActions>
            <Button
              htmlType="submit"
              type="primary"
              theme="solid"
              loading={loading}
            >
              提交审批
            </Button>
          </PageFormActions>
        </Form>
      </Card>
    </div>
  );
}
