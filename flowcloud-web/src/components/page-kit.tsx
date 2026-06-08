import type { ReactNode } from 'react';
import { Card, Space, Typography } from '@douyinfe/semi-ui';

const { Title, Text } = Typography;

interface PageHeaderProps {
  title: ReactNode;
  description?: ReactNode;
  actions?: ReactNode;
  className?: string;
}

interface PageFormActionsProps {
  children: ReactNode;
}

interface PageFilterCardProps {
  children: ReactNode;
}

interface PageActionGroupProps {
  children: ReactNode;
}

export function PageHeader({ title, description, actions, className }: PageHeaderProps) {
  const isPlainTitle = typeof title === 'string' || typeof title === 'number';

  return (
    <div className={['page-header', 'page-header-semi', className].filter(Boolean).join(' ')}>
      <div className="page-header-main">
        {isPlainTitle ? (
          <Title heading={3} className="page-header-title">{title}</Title>
        ) : (
          <div className="page-header-title page-header-title-custom">{title}</div>
        )}
        {description ? <Text type="tertiary" className="page-header-description">{description}</Text> : null}
      </div>
      {actions ? (
        <Space wrap spacing={12} className="page-header-actions">
          {actions}
        </Space>
      ) : null}
    </div>
  );
}

export function PageFilterCard({ children }: PageFilterCardProps) {
  return <Card className="page-filter-card">{children}</Card>;
}

export function PageFormActions({ children }: PageFormActionsProps) {
  return (
    <div className="page-form-actions page-form-actions-end">
      <Space spacing={12}>{children}</Space>
    </div>
  );
}

export function PageActionGroup({ children }: PageActionGroupProps) {
  return (
    <Space wrap spacing={8} className="page-inline-actions">
      {children}
    </Space>
  );
}
