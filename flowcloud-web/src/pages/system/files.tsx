import { useEffect, useRef, useState } from 'react';
import type { ChangeEvent, DragEvent } from 'react';
import { Button, Card, Input, Modal, Select, Space, Table, Tag, Toast, Typography } from '@douyinfe/semi-ui';
import type { ColumnProps } from '@douyinfe/semi-ui/lib/es/table';
import {
  batchDeleteAttachments,
  deleteAttachment,
  downloadAttachmentsArchive,
  getAttachmentPage,
  uploadAttachment,
  type AttachmentManageQuery,
  type AttachmentVO,
} from '@/api/attachment';
import { PageActionGroup, PageFilterCard, PageHeader } from '@/components/page-kit';
import { usePermission } from '@/hooks/usePermission';
import { openAttachmentPreview } from '@/utils/attachmentPreview';
import { PERM } from '@/utils/permissions';

const { Text } = Typography;

const STORAGE_LABEL_MAP: Record<string, { text: string; color: 'blue' | 'green' | 'grey' }> = {
  LOCAL: { text: '本地', color: 'blue' },
  MINIO: { text: 'MinIO', color: 'green' },
};

const BIZ_TYPE_LABEL_MAP: Record<string, string> = {
  common: '通用文件',
  instance: '审批实例',
  template: '流程模板',
  user_avatar: '用户头像',
};

type UploadItemStatus = 'waiting' | 'uploading' | 'success' | 'error';

interface UploadFormState {
  bizType: string;
  bizId: string;
  fieldName: string;
}

interface UploadQueueItem {
  id: string;
  file: File;
  status: UploadItemStatus;
  progress: number;
  errorMessage?: string;
  previewUrl?: string;
}

type UploadStatusFilter = 'all' | UploadItemStatus;

const DEFAULT_UPLOAD_FORM: UploadFormState = {
  bizType: 'common',
  bizId: '',
  fieldName: '',
};

const BIZ_TYPE_OPTIONS = [
  { label: '通用文件', value: 'common' },
  { label: '审批实例', value: 'instance' },
  { label: '模板', value: 'template' },
  { label: '用户头像', value: 'user_avatar' },
] as const;

const FILE_MANAGE_MAX_UPLOAD_SIZE = 10 * 1024 * 1024;
const FILE_MANAGE_MAX_UPLOAD_SIZE_TEXT = '10MB';

function formatFileSize(size?: number) {
  if (!size) {
    return '-';
  }
  if (size >= 1024 * 1024) {
    return `${(size / 1024 / 1024).toFixed(2)} MB`;
  }
  if (size >= 1024) {
    return `${(size / 1024).toFixed(2)} KB`;
  }
  return `${size} B`;
}

function getDefaultFieldName(bizType: string) {
  return bizType === 'user_avatar' ? 'avatar' : 'file';
}

export default function FilesPage() {
  const { hasPermission } = usePermission();
  const canDelete = hasPermission(PERM.FILE_DELETE);
  const canDownload = hasPermission(PERM.FILE_DOWNLOAD);
  const uploadInputRef = useRef<HTMLInputElement | null>(null);
  const uploadQueueRef = useRef<UploadQueueItem[]>([]);
  const [list, setList] = useState<AttachmentVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [batchDeleting, setBatchDeleting] = useState(false);
  const [batchDownloading, setBatchDownloading] = useState(false);
  const [total, setTotal] = useState(0);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [recentUploadedIds, setRecentUploadedIds] = useState<number[]>([]);
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [uploadDragActive, setUploadDragActive] = useState(false);
  const [uploadStatusFilter, setUploadStatusFilter] = useState<UploadStatusFilter>('all');
  const [uploadForm, setUploadForm] = useState<UploadFormState>(DEFAULT_UPLOAD_FORM);
  const [uploadQueue, setUploadQueue] = useState<UploadQueueItem[]>([]);
  const [query, setQuery] = useState<AttachmentManageQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [keyword, setKeyword] = useState('');
  const [bizType, setBizType] = useState('');
  const [storageType, setStorageType] = useState('');

  const fetchData = async (nextQuery: AttachmentManageQuery) => {
    setLoading(true);
    try {
      const res = await getAttachmentPage(nextQuery);
      setList(res.data.records ?? []);
      setTotal(res.data.total ?? 0);
      setSelectedIds([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(query);
  }, [query]);

  useEffect(() => {
    uploadQueueRef.current = uploadQueue;
  }, [uploadQueue]);

  useEffect(() => () => {
    disposeUploadItems(uploadQueueRef.current);
  }, []);

  const handleSearch = () => {
    setQuery((current) => ({
      ...current,
      pageNum: 1,
      keyword: keyword.trim() || undefined,
      bizType: bizType || undefined,
      storageType: storageType || undefined,
    }));
  };

  const handleReset = () => {
    setKeyword('');
    setBizType('');
    setStorageType('');
    setQuery({
      pageNum: 1,
      pageSize: 10,
    });
  };

  const appendFiles = (files: File[]) => {
    if (!files.length) {
      return;
    }
    const validFiles = files.filter((file) => {
      if (file.size <= FILE_MANAGE_MAX_UPLOAD_SIZE) {
        return true;
      }
      Toast.warning(`文件「${file.name}」超过 ${FILE_MANAGE_MAX_UPLOAD_SIZE_TEXT}，已跳过`);
      return false;
    });
    if (!validFiles.length) {
      return;
    }
    const nextItems = validFiles.map((file) => ({
      id: `${Date.now()}-${file.name}-${file.size}-${file.lastModified}-${Math.random().toString(36).slice(2, 8)}`,
      file,
      status: 'waiting' as UploadItemStatus,
      progress: 0,
      previewUrl: createPreviewUrl(file),
    }));
    setUploadQueue((current) => [...current, ...nextItems]);
  };

  const updateUploadQueueItem = (id: string, patch: Partial<UploadQueueItem>) => {
    setUploadQueue((current) => current.map((item) => (item.id === id ? { ...item, ...patch } : item)));
  };

  const handleOpenUploadModal = () => {
    disposeUploadItems(uploadQueueRef.current);
    setUploadForm(DEFAULT_UPLOAD_FORM);
    setUploadQueue([]);
    setUploadDragActive(false);
    setUploadStatusFilter('all');
    setUploadModalVisible(true);
  };

  const handleCloseUploadModal = () => {
    if (uploadQueue.some((item) => item.status === 'uploading')) {
      Toast.warning('文件上传中，请稍后再关闭');
      return;
    }
    setUploadModalVisible(false);
    setUploadDragActive(false);
  };

  const triggerUploadSelect = () => {
    uploadInputRef.current?.click();
  };

  const handleSelectedFiles = (event: ChangeEvent<HTMLInputElement>) => {
    appendFiles(Array.from(event.target.files ?? []));
    event.target.value = '';
  };

  const handleDropFiles = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setUploadDragActive(false);
    appendFiles(Array.from(event.dataTransfer.files ?? []));
  };

  const getUploadMeta = () => {
    const normalizedBizType = uploadForm.bizType.trim() || 'common';
    const bizIdText = uploadForm.bizId.trim();
    const fieldName = uploadForm.fieldName.trim() || getDefaultFieldName(normalizedBizType);
    let bizId: number | undefined;

    if (bizIdText) {
      if (!/^\d+$/.test(bizIdText)) {
        Toast.warning('业务ID必须为正整数');
        return null;
      }
      bizId = Number(bizIdText);
    }

    if ((normalizedBizType === 'instance' || normalizedBizType === 'template' || normalizedBizType === 'user_avatar') && bizId == null) {
      Toast.warning('当前业务类型需要填写业务ID');
      return null;
    }

    return {
      bizType: normalizedBizType,
      bizId,
      fieldName,
    };
  };

  const refreshAfterUpload = async () => {
    if (query.pageNum === 1) {
      await fetchData(query);
      return;
    }
    setQuery((current) => ({
      ...current,
      pageNum: 1,
    }));
  };

  const uploadSingleFile = async (
    item: UploadQueueItem,
    meta: { bizType: string; bizId?: number; fieldName?: string },
  ) => {
    updateUploadQueueItem(item.id, {
      status: 'uploading',
      progress: 0,
      errorMessage: undefined,
    });

    try {
      const response = await uploadAttachment(item.file, meta.bizType, meta.bizId, meta.fieldName, {
        onUploadProgress: (event) => {
          const total = event.total || item.file.size || 1;
          const progress = Math.min(99, Math.round((event.loaded / total) * 100));
          updateUploadQueueItem(item.id, { progress });
        },
      });
      updateUploadQueueItem(item.id, {
        status: 'success',
        progress: 100,
        errorMessage: undefined,
      });
      return response.data.id;
    } catch (error) {
      const message = error instanceof Error ? error.message : '上传失败，请稍后重试';
      updateUploadQueueItem(item.id, {
        status: 'error',
        progress: 0,
        errorMessage: message,
      });
      return null;
    }
  };

  const runUploadTasks = async (itemIds: string[]) => {
    if (!itemIds.length) {
      Toast.warning('请先选择待上传文件');
      return;
    }
    const meta = getUploadMeta();
    if (!meta) {
      return;
    }

    const targets = uploadQueue.filter((item) => itemIds.includes(item.id));
    if (!targets.length) {
      Toast.warning('未找到可上传的文件');
      return;
    }

    const results = await Promise.all(targets.map((item) => uploadSingleFile(item, meta)));
    const uploadedIds = results.filter((id): id is number => typeof id === 'number');
    const successCount = uploadedIds.length;
    const failedCount = results.length - successCount;

    if (successCount > 0) {
      setRecentUploadedIds((current) => Array.from(new Set([...uploadedIds, ...current])).slice(0, 20));
      await refreshAfterUpload();
    }

    if (failedCount === 0) {
      Toast.success(`已成功上传 ${successCount} 个文件`);
    } else if (successCount > 0) {
      Toast.warning(`上传完成，成功 ${successCount} 个，失败 ${failedCount} 个`);
    } else {
      Toast.error(`上传失败，共 ${failedCount} 个文件未上传成功`);
    }
  };

  const handleUploadAll = async () => {
    await runUploadTasks(
      uploadQueue
        .filter((item) => item.status === 'waiting')
        .map((item) => item.id),
    );
  };

  const handleRetryFailed = async () => {
    await runUploadTasks(
      uploadQueue
        .filter((item) => item.status === 'error')
        .map((item) => item.id),
    );
  };

  const handleRetrySingle = async (id: string) => {
    await runUploadTasks([id]);
  };

  const handleRemoveUploadItem = (id: string) => {
    setUploadQueue((current) => {
      const retained = current.filter((item) => item.id !== id || item.status === 'uploading');
      const removed = current.filter((item) => item.id === id && item.status !== 'uploading');
      disposeUploadItems(removed);
      return retained;
    });
  };

  const handleClearUploaded = () => {
    setUploadQueue((current) => {
      const retained = current.filter((item) => item.status !== 'success');
      const removed = current.filter((item) => item.status === 'success');
      disposeUploadItems(removed);
      return retained;
    });
  };

  const handleDelete = (record: AttachmentVO) => {
    const isAvatarFile = record.bizType === 'user_avatar';
    Modal.confirm({
      title: '删除文件',
      content: isAvatarFile
        ? `确认删除头像文件「${record.originalName}」吗？删除后${record.bizName ? `用户「${record.bizName}」的` : ''}头像将恢复为默认头像。`
        : `确认删除文件「${record.originalName}」吗？`,
      onOk: async () => {
        await deleteAttachment(record.id);
        Toast.success('文件已删除');
        fetchData(query);
      },
    });
  };

  const handleBatchDelete = () => {
    if (!selectedIds.length) {
      Toast.warning('请先选择要删除的文件');
      return;
    }
    const selectedAvatarCount = list.filter(
      (item) => selectedIds.includes(item.id) && item.bizType === 'user_avatar',
    ).length;
    Modal.confirm({
      title: '批量删除文件',
      content: selectedAvatarCount > 0
        ? `确认删除选中的 ${selectedIds.length} 个文件吗？其中包含 ${selectedAvatarCount} 个头像文件，删除后对应用户头像将恢复为默认头像。`
        : `确认删除选中的 ${selectedIds.length} 个文件吗？`,
      onOk: async () => {
        setBatchDeleting(true);
        try {
          await batchDeleteAttachments({ ids: selectedIds });
          Toast.success(`已删除 ${selectedIds.length} 个文件`);
          fetchData(query);
        } finally {
          setBatchDeleting(false);
        }
      },
    });
  };

  const handleBatchDownload = async () => {
    if (!selectedIds.length) {
      Toast.warning('请先选择要下载的文件');
      return;
    }
    setBatchDownloading(true);
    try {
      const response = await downloadAttachmentsArchive({ ids: selectedIds });
      const fileName = parseDownloadFileName(response.headers['content-disposition']) || `attachments-${Date.now()}.zip`;
      const url = window.URL.createObjectURL(response.data);
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      Toast.success(`已准备 ${selectedIds.length} 个文件的下载包`);
    } finally {
      setBatchDownloading(false);
    }
  };

  const columns: ColumnProps<AttachmentVO>[] = [
    {
      title: '文件名',
      dataIndex: 'originalName',
      render: (_: unknown, record: AttachmentVO) => (
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
            <a
              href="#"
              onClick={(event) => {
                event.preventDefault();
                openAttachmentPreview(record);
              }}
            >
              {record.originalName}
            </a>
            {recentUploadedIds.includes(record.id) ? <Tag color="green">刚上传</Tag> : null}
          </div>
          {record.bizType === 'user_avatar' && (record.bizName || record.bizCode) ? (
            <div style={{ marginTop: 4 }}>
              <Text type="tertiary" size="small">
                所属用户: {record.bizName || '-'}{record.bizCode ? ` / ${record.bizCode}` : ''}
              </Text>
            </div>
          ) : null}
        </div>
      ),
    },
    {
      title: '业务类型',
      dataIndex: 'bizType',
      width: 110,
      render: (value: string) => BIZ_TYPE_LABEL_MAP[value] || value || '-',
    },
    {
      title: '字段名',
      dataIndex: 'fieldName',
      width: 140,
      render: (value: string, record: AttachmentVO) => value || getDefaultFieldName(record.bizType || 'common'),
    },
    {
      title: '存储方式',
      dataIndex: 'storageType',
      width: 110,
      render: (value: string) => {
        const meta = STORAGE_LABEL_MAP[value] || { text: value || '-', color: 'grey' as const };
        return <Tag color={meta.color}>{meta.text}</Tag>;
      },
    },
    {
      title: '大小',
      dataIndex: 'fileSize',
      width: 110,
      render: (value: number) => formatFileSize(value),
    },
    {
      title: '上传人',
      dataIndex: 'uploaderName',
      width: 120,
      render: (value: string) => value || '-',
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
      width: 180,
      render: (value: string) => value || '-',
    },
    {
      title: '操作',
      width: 150,
      render: (_: unknown, record: AttachmentVO) => (
        <PageActionGroup>
          <Button
            size="small"
            type="tertiary"
            theme="light"
            className="page-action-button page-action-button-view"
            onClick={() => openAttachmentPreview(record)}
          >
            查看
          </Button>
          <Button
            size="small"
            type="danger"
            theme="light"
            className="page-action-button page-action-button-danger"
            disabled={!canDelete}
            onClick={() => handleDelete(record)}
          >
            删除
          </Button>
        </PageActionGroup>
      ),
    },
  ];

  const waitingCount = uploadQueue.filter((item) => item.status === 'waiting').length;
  const failedCount = uploadQueue.filter((item) => item.status === 'error').length;
  const uploadingCount = uploadQueue.filter((item) => item.status === 'uploading').length;
  const filteredUploadQueue = uploadQueue.filter((item) => uploadStatusFilter === 'all' || item.status === uploadStatusFilter);

  return (
    <div className="page-container">
      <PageHeader
        title="文件管理"
        description="集中查看当前租户的审批附件与上传文件"
        actions={(
          <Space wrap spacing={12}>
            <Tag color="grey">已选 {selectedIds.length} 个文件</Tag>
            <Button type="primary" theme="solid" onClick={handleOpenUploadModal}>
              上传文件
            </Button>
            <Button
              loading={batchDownloading}
              disabled={!canDownload || selectedIds.length === 0}
              onClick={handleBatchDownload}
            >
              批量下载
            </Button>
            <Button
              type="danger"
              loading={batchDeleting}
              disabled={!canDelete || selectedIds.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除
            </Button>
          </Space>
        )}
      />
      <PageFilterCard>
        <Space wrap spacing={16} align="center" className="page-filter-space">
          <Space wrap spacing={12} className="page-filter-fields">
            <Input
              value={keyword}
              placeholder="文件名关键词"
              style={{ width: 220 }}
              onChange={setKeyword}
            />
            <Select
              value={bizType}
              optionList={[
                { label: '全部业务', value: '' },
                { label: '审批实例', value: 'instance' },
                { label: '模板', value: 'template' },
                { label: '用户头像', value: 'user_avatar' },
              ]}
              style={{ width: 140 }}
              onChange={(value) => setBizType(String(value))}
            />
            <Select
              value={storageType}
              optionList={[
                { label: '全部存储', value: '' },
                { label: '本地存储', value: 'LOCAL' },
                { label: 'MinIO', value: 'MINIO' },
              ]}
              style={{ width: 140 }}
              onChange={(value) => setStorageType(String(value))}
            />
          </Space>
          <Space spacing={8} className="page-filter-actions">
            <Button type="primary" theme="solid" onClick={handleSearch}>查询</Button>
            <Button theme="solid" onClick={handleReset}>重置</Button>
          </Space>
        </Space>
      </PageFilterCard>

      <Card>
        <Table
          rowKey="id"
          columns={columns}
          dataSource={list}
          loading={loading}
          rowClassName={(record: AttachmentVO) => (recentUploadedIds.includes(record.id) ? 'files-page-row-recent-upload' : '')}
          rowSelection={{
            selectedRowKeys: selectedIds,
            onChange: (keys) => setSelectedIds(keys as number[]),
          }}
          pagination={{
            total,
            currentPage: query.pageNum,
            pageSize: query.pageSize,
            showSizeChanger: true,
            pageSizeOpts: [10, 20, 50],
            onChange: (page, pageSize) => {
              setQuery((current) => ({
                ...current,
                pageNum: page,
                pageSize,
              }));
            },
          }}
        />
      </Card>

      <Modal
        title="上传文件"
        visible={uploadModalVisible}
        onCancel={handleCloseUploadModal}
        footer={null}
        width={760}
      >
        <input
          ref={uploadInputRef}
          type="file"
          multiple
          style={{ display: 'none' }}
          onChange={handleSelectedFiles}
        />
        <div style={{ display: 'grid', gap: 16 }}>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              gap: 12,
              flexWrap: 'wrap',
              padding: '10px 14px',
              borderRadius: 12,
              background: 'var(--semi-color-fill-0)',
            }}
          >
            <Space wrap spacing={8}>
              <Tag color="blue">文件管理单文件最大 {FILE_MANAGE_MAX_UPLOAD_SIZE_TEXT}</Tag>
              <Tag color="grey">支持批量上传</Tag>
            </Space>
            <Text type="tertiary">个人头像请在个人信息页上传，单图限制 5MB。</Text>
          </div>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: '1.1fr 1fr 1fr',
              gap: 12,
            }}
          >
            <div>
              <div style={{ marginBottom: 6, fontSize: 12, color: 'var(--semi-color-text-2)' }}>业务类型</div>
              <Select
                value={uploadForm.bizType}
                optionList={BIZ_TYPE_OPTIONS.map((item) => ({ label: item.label, value: item.value }))}
                style={{ width: '100%' }}
                onChange={(value) => setUploadForm((current) => ({ ...current, bizType: String(value) }))}
              />
            </div>
            <div>
              <div style={{ marginBottom: 6, fontSize: 12, color: 'var(--semi-color-text-2)' }}>业务ID</div>
              <Input
                value={uploadForm.bizId}
                placeholder="如 1001"
                onChange={(value) => setUploadForm((current) => ({ ...current, bizId: value }))}
              />
            </div>
            <div>
              <div style={{ marginBottom: 6, fontSize: 12, color: 'var(--semi-color-text-2)' }}>字段名</div>
              <Input
                value={uploadForm.fieldName}
                placeholder={`默认 ${getDefaultFieldName(uploadForm.bizType || 'common')}`}
                onChange={(value) => setUploadForm((current) => ({ ...current, fieldName: value }))}
              />
            </div>
          </div>

          <div
            onClick={triggerUploadSelect}
            onDragEnter={(event) => {
              event.preventDefault();
              setUploadDragActive(true);
            }}
            onDragLeave={(event) => {
              event.preventDefault();
              setUploadDragActive(false);
            }}
            onDragOver={(event) => {
              event.preventDefault();
              setUploadDragActive(true);
            }}
            onDrop={handleDropFiles}
            style={{
              border: `1px dashed ${uploadDragActive ? 'var(--semi-color-primary)' : 'var(--semi-color-border)'}`,
              borderRadius: 14,
              background: uploadDragActive ? 'rgba(34, 106, 255, 0.06)' : 'var(--semi-color-fill-0)',
              padding: '28px 24px',
              textAlign: 'center',
              cursor: 'pointer',
              transition: 'all .2s ease',
            }}
          >
            <div style={{ fontSize: 16, fontWeight: 600, color: 'var(--semi-color-text-0)' }}>拖拽文件到这里，或点击选择文件</div>
            <div style={{ marginTop: 8, fontSize: 13, color: 'var(--semi-color-text-2)' }}>
              支持批量上传，上传前可设置业务类型、业务ID与字段名，单文件不超过 {FILE_MANAGE_MAX_UPLOAD_SIZE_TEXT}
            </div>
          </div>

          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: 12,
              flexWrap: 'wrap',
              padding: '10px 14px',
              borderRadius: 12,
              background: 'var(--semi-color-fill-0)',
            }}
          >
            <Space wrap spacing={8}>
              <Tag color="grey">总计 {uploadQueue.length}</Tag>
              <Tag color="blue">待上传 {waitingCount}</Tag>
              <Tag color="green">上传中 {uploadingCount}</Tag>
              <Tag color="red">失败 {failedCount}</Tag>
            </Space>
            <Space wrap spacing={8}>
              <Button size="small" theme={uploadStatusFilter === 'all' ? 'solid' : 'light'} type={uploadStatusFilter === 'all' ? 'primary' : 'tertiary'} onClick={() => setUploadStatusFilter('all')}>
                全部
              </Button>
              <Button size="small" theme={uploadStatusFilter === 'waiting' ? 'solid' : 'light'} type={uploadStatusFilter === 'waiting' ? 'primary' : 'tertiary'} onClick={() => setUploadStatusFilter('waiting')}>
                待上传
              </Button>
              <Button size="small" theme={uploadStatusFilter === 'uploading' ? 'solid' : 'light'} type={uploadStatusFilter === 'uploading' ? 'primary' : 'tertiary'} onClick={() => setUploadStatusFilter('uploading')}>
                上传中
              </Button>
              <Button size="small" theme={uploadStatusFilter === 'success' ? 'solid' : 'light'} type={uploadStatusFilter === 'success' ? 'primary' : 'tertiary'} onClick={() => setUploadStatusFilter('success')}>
                成功
              </Button>
              <Button size="small" theme={uploadStatusFilter === 'error' ? 'solid' : 'light'} type={uploadStatusFilter === 'error' ? 'primary' : 'tertiary'} onClick={() => setUploadStatusFilter('error')}>
                失败
              </Button>
              <Button theme="light" onClick={triggerUploadSelect}>
                添加文件
              </Button>
              <Button theme="light" disabled={failedCount === 0} onClick={handleRetryFailed}>
                重试失败
              </Button>
              <Button theme="light" disabled={!uploadQueue.some((item) => item.status === 'success')} onClick={handleClearUploaded}>
                清空已完成
              </Button>
              <Button type="primary" theme="solid" disabled={waitingCount === 0 || uploadingCount > 0} onClick={handleUploadAll}>
                开始上传
              </Button>
            </Space>
          </div>

          <div
            style={{
              maxHeight: 360,
              overflowY: 'auto',
              display: 'grid',
              gap: 12,
            }}
          >
            {uploadQueue.length === 0 ? (
              <div
                style={{
                  borderRadius: 12,
                  border: '1px solid var(--semi-color-border)',
                  padding: '28px 20px',
                  textAlign: 'center',
                  color: 'var(--semi-color-text-2)',
                }}
              >
                暂无待上传文件，先拖拽文件进来或点击上方区域选择文件
              </div>
            ) : filteredUploadQueue.length === 0 ? (
              <div
                style={{
                  borderRadius: 12,
                  border: '1px solid var(--semi-color-border)',
                  padding: '24px 20px',
                  textAlign: 'center',
                  color: 'var(--semi-color-text-2)',
                }}
              >
                当前筛选结果下没有文件
              </div>
            ) : filteredUploadQueue.map((item) => (
              <div
                key={item.id}
                style={{
                  border: '1px solid var(--semi-color-border)',
                  borderRadius: 12,
                  padding: '14px 16px',
                  background: 'var(--semi-color-bg-1)',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: 16, alignItems: 'flex-start' }}>
                  <div style={{ minWidth: 0, flex: 1, display: 'flex', gap: 12 }}>
                    {item.previewUrl ? (
                      <div
                        style={{
                          width: 52,
                          height: 52,
                          borderRadius: 10,
                          overflow: 'hidden',
                          flexShrink: 0,
                          border: '1px solid var(--semi-color-border)',
                          background: 'var(--semi-color-fill-0)',
                        }}
                      >
                        <img
                          src={item.previewUrl}
                          alt={item.file.name}
                          style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
                        />
                      </div>
                    ) : null}
                    <div style={{ minWidth: 0, flex: 1 }}>
                    <div
                      style={{
                        fontWeight: 600,
                        color: 'var(--semi-color-text-0)',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                      }}
                    >
                      {item.file.name}
                    </div>
                    <div style={{ marginTop: 4, fontSize: 12, color: 'var(--semi-color-text-2)' }}>
                      {formatFileSize(item.file.size)} / {item.file.type || '未知类型'}
                    </div>
                    </div>
                  </div>
                  <Space spacing={8}>
                    <Tag color={getUploadStatusColor(item.status)}>{getUploadStatusText(item.status)}</Tag>
                    {item.status === 'error' ? (
                      <Button size="small" theme="light" onClick={() => handleRetrySingle(item.id)}>
                        重试
                      </Button>
                    ) : null}
                    <Button
                      size="small"
                      theme="borderless"
                      disabled={item.status === 'uploading'}
                      onClick={() => handleRemoveUploadItem(item.id)}
                    >
                      移除
                    </Button>
                  </Space>
                </div>

                <div style={{ marginTop: 10 }}>
                  <div
                    style={{
                      height: 8,
                      borderRadius: 999,
                      background: 'var(--semi-color-fill-1)',
                      overflow: 'hidden',
                    }}
                  >
                    <div
                      style={{
                        width: `${item.progress}%`,
                        height: '100%',
                        borderRadius: 999,
                        background: item.status === 'error' ? '#ff4d4f' : 'var(--semi-color-primary)',
                        transition: 'width .2s ease',
                      }}
                    />
                  </div>
                  <div style={{ marginTop: 8, display: 'flex', justifyContent: 'space-between', gap: 12, fontSize: 12 }}>
                    <span style={{ color: item.status === 'error' ? '#ff4d4f' : 'var(--semi-color-text-2)' }}>
                      {item.errorMessage || getUploadStatusDescription(item.status)}
                    </span>
                    <span style={{ color: 'var(--semi-color-text-2)' }}>{item.progress}%</span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
            <Button onClick={handleCloseUploadModal}>关闭</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

function parseDownloadFileName(contentDisposition?: string) {
  if (!contentDisposition) {
    return '';
  }
  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1]);
  }
  const basicMatch = contentDisposition.match(/filename="?([^";]+)"?/i);
  return basicMatch?.[1] ?? '';
}

function getUploadStatusText(status: UploadItemStatus) {
  if (status === 'uploading') {
    return '上传中';
  }
  if (status === 'success') {
    return '成功';
  }
  if (status === 'error') {
    return '失败';
  }
  return '待上传';
}

function getUploadStatusDescription(status: UploadItemStatus) {
  if (status === 'uploading') {
    return '文件正在上传，请稍候';
  }
  if (status === 'success') {
    return '文件已上传成功，列表已自动刷新';
  }
  if (status === 'error') {
    return '上传失败，请点击重试';
  }
  return '等待开始上传';
}

function getUploadStatusColor(status: UploadItemStatus): 'blue' | 'green' | 'red' | 'grey' {
  if (status === 'uploading') {
    return 'blue';
  }
  if (status === 'success') {
    return 'green';
  }
  if (status === 'error') {
    return 'red';
  }
  return 'grey';
}

function createPreviewUrl(file: File) {
  return file.type.startsWith('image/') ? URL.createObjectURL(file) : undefined;
}

function disposeUploadItems(items: UploadQueueItem[]) {
  items.forEach((item) => {
    if (item.previewUrl) {
      URL.revokeObjectURL(item.previewUrl);
    }
  });
}
