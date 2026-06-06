import { useState, useCallback, useEffect } from 'react';
import {
  Table, Button, Input, Select, DatePicker, Toast,
  Typography, Tag, Space,
} from '@douyinfe/semi-ui';
import type { ColumnProps } from '@douyinfe/semi-ui/lib/es/table';
import { getAuditLogs, type AuditLogVO, type AuditLogQuery } from '@/api/audit';

const { Title } = Typography;

const MODULE_OPTIONS = [
  { label: '全部', value: '' },
  { label: '登录', value: 'login' },
  { label: '审批', value: 'approval' },
  { label: '系统', value: 'system' },
];

const ACTION_COLOR: Record<string, string> = {
  LOGIN: 'green',
  LOGOUT: 'grey',
  PUBLISH_TEMPLATE: 'blue',
  DISABLE_TEMPLATE: 'orange',
  SUBMIT_APPROVAL: 'teal',
  APPROVE_TASK: 'green',
  REJECT_TASK: 'red',
};

export default function AuditLogsPage() {
  const [list, setList] = useState<AuditLogVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [query, setQuery] = useState<AuditLogQuery>({ pageNum: 1, pageSize: 20 });

  const [keyword, setKeyword] = useState('');
  const [targetType, setTargetType] = useState('');
  const [dateRange, setDateRange] = useState<[Date, Date] | null>(null);

  const fetchLogs = useCallback(async (q: AuditLogQuery) => {
    setLoading(true);
    try {
      const res = await getAuditLogs(q);
      setList(res.data.records ?? []);
      setTotal(res.data.total ?? 0);
    } catch {
      Toast.error('加载审计日志失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchLogs(query);
  }, [fetchLogs, query]);

  const handleSearch = () => {
    const next: AuditLogQuery = {
      ...query,
      pageNum: 1,
      action: keyword || undefined,
      targetType: targetType || undefined,
      startTime: dateRange ? dateRange[0].toISOString() : undefined,
      endTime: dateRange ? dateRange[1].toISOString() : undefined,
    };
    setQuery(next);
  };

  const handleReset = () => {
    setKeyword('');
    setTargetType('');
    setDateRange(null);
    setQuery({ pageNum: 1, pageSize: 20 });
  };

  const columns: ColumnProps<AuditLogVO>[] = [
    {
      title: '操作时间',
      dataIndex: 'createTime',
      width: 170,
      render: (v: string) => v?.replace('T', ' ').slice(0, 19),
    },
    {
      title: '操作人',
      dataIndex: 'userName',
      width: 100,
    },
    {
      title: '动作',
      dataIndex: 'action',
      width: 160,
      render: (v: string) => (
        <Tag color={(ACTION_COLOR[v] ?? 'light-blue') as 'green'} size="small">{v}</Tag>
      ),
    },
    {
      title: '模块',
      dataIndex: 'targetType',
      width: 90,
    },
    {
      title: '详情',
      dataIndex: 'detail',
      ellipsis: true,
    },
    {
      title: '结果',
      dataIndex: 'result',
      width: 70,
      render: (v: string) => (
        <Tag color={v === 'success' ? 'green' : 'red'} size="small">
          {v === 'success' ? '成功' : '失败'}
        </Tag>
      ),
    },
    {
      title: 'IP',
      dataIndex: 'ip',
      width: 130,
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Title heading={4} style={{ marginBottom: 16 }}>审计日志</Title>

      {/* 筛选栏 */}
      <div style={{ display: 'flex', gap: 12, marginBottom: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        <Input
          placeholder="动作关键词"
          value={keyword}
          onChange={setKeyword}
          style={{ width: 180 }}
        />
        <Select
          placeholder="模块"
          value={targetType}
          onChange={(v) => setTargetType(v as string)}
          optionList={MODULE_OPTIONS}
          style={{ width: 120 }}
        />
        <DatePicker
          type="dateRange"
          value={dateRange as [Date, Date] | undefined}
          onChange={(v) => setDateRange(v as [Date, Date] | null)}
          style={{ width: 280 }}
        />
        <Space spacing={8}>
          <Button type="primary" onClick={handleSearch}>查询</Button>
          <Button onClick={handleReset}>重置</Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={list}
        loading={loading}
        rowKey="id"
        pagination={{
          total,
          currentPage: query.pageNum,
          pageSize: query.pageSize,
          showSizeChanger: true,
          pageSizeOpts: [10, 20, 50],
          onChange: (page, size) =>
            setQuery((prev) => ({ ...prev, pageNum: page, pageSize: size })),
        }}
        size="small"
        scroll={{ x: 900 }}
      />
    </div>
  );
}