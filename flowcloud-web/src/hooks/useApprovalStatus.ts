import { useMemo } from 'react';
import { useDictOptions } from '@/hooks/useDictOptions';
import { APPROVAL_STATUS_META, APPROVAL_STATUS_OPTIONS } from '@/utils/approvalDisplay';

export function useApprovalStatus() {
  const { options, labelMap } = useDictOptions('approval_status', APPROVAL_STATUS_OPTIONS);

  const getStatusMeta = useMemo(
    () => (status: string) => ({
      text: labelMap[status] || APPROVAL_STATUS_META[status]?.text || status,
      color: APPROVAL_STATUS_META[status]?.color || 'grey',
    }),
    [labelMap],
  );

  return { options, labelMap, getStatusMeta };
}
