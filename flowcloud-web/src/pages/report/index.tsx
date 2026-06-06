import { useEffect, useRef, useState } from 'react';
import { Card, Col, Row, Tag } from '@douyinfe/semi-ui';
import * as echarts from 'echarts';
import { getReportAnalytics } from '@/api/report';
import type { ReportAnalyticsVO } from '@/types';

export default function ReportPage() {
  const [data, setData] = useState<ReportAnalyticsVO | null>(null);
  const trendRef = useRef<HTMLDivElement>(null);
  const deptRef = useRef<HTMLDivElement>(null);
  const loadRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    getReportAnalytics().then((res) => setData(res.data));
  }, []);

  useEffect(() => {
    if (!data || !trendRef.current) return;
    const chart = echarts.init(trendRef.current);
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['提交量', '通过', '驳回'] },
      xAxis: { type: 'category', data: data.trend.map((i) => i.period) },
      yAxis: { type: 'value' },
      series: [
        { name: '提交量', type: 'line', data: data.trend.map((i) => i.total) },
        { name: '通过', type: 'bar', data: data.trend.map((i) => i.approved) },
        { name: '驳回', type: 'bar', data: data.trend.map((i) => i.rejected) },
      ],
    });
    return () => chart.dispose();
  }, [data]);

  useEffect(() => {
    if (!data || !deptRef.current) return;
    const chart = echarts.init(deptRef.current);
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.deptEfficiency.map((i) => i.deptName) },
      yAxis: [{ type: 'value', name: '审批量' }, { type: 'value', name: '平均耗时(h)' }],
      series: [
        { name: '审批量', type: 'bar', data: data.deptEfficiency.map((i) => i.total) },
        { name: '平均耗时', type: 'line', yAxisIndex: 1, data: data.deptEfficiency.map((i) => i.avgHours) },
      ],
    });
    return () => chart.dispose();
  }, [data]);

  useEffect(() => {
    if (!data || !loadRef.current) return;
    const chart = echarts.init(loadRef.current);
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['待办', '已办'] },
      xAxis: { type: 'category', data: data.approverLoad.slice(0, 10).map((i) => i.approverName) },
      yAxis: { type: 'value' },
      series: [
        { name: '待办', type: 'bar', stack: 'load', data: data.approverLoad.slice(0, 10).map((i) => i.pendingCount) },
        { name: '已办', type: 'bar', stack: 'load', data: data.approverLoad.slice(0, 10).map((i) => i.handledCount) },
      ],
    });
    return () => chart.dispose();
  }, [data]);

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>报表分析</h2>
        <p>审批量趋势、驳回率、部门效率与审批人负载</p>
      </div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <div className="stat-card">
              <div className="label">驳回率</div>
              <div className="value">{(data?.rejectionRate ?? 0).toFixed(2)}%</div>
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <div className="stat-card">
              <div className="label">部门数</div>
              <div className="value">{data?.deptEfficiency.length ?? 0}</div>
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <div className="stat-card">
              <div className="label">审批人数</div>
              <div className="value">{data?.approverLoad.length ?? 0}</div>
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <div style={{ color: '#86909c', fontSize: 12 }}>统计周期</div>
            <div style={{ marginTop: 8 }}><Tag color="blue">近 6 个月</Tag></div>
          </Card>
        </Col>
      </Row>
      <Row gutter={16}>
        <Col span={12}><Card title="审批量趋势"><div ref={trendRef} style={{ height: 320 }} /></Card></Col>
        <Col span={12}><Card title="部门效率"><div ref={deptRef} style={{ height: 320 }} /></Card></Col>
      </Row>
      <Card title="审批人负载 TOP10" style={{ marginTop: 16 }}>
        <div ref={loadRef} style={{ height: 360 }} />
      </Card>
    </div>
  );
}
