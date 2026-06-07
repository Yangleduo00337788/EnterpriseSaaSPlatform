import { useDictOptions } from '@/hooks/useDictOptions';
import { APPROVAL_CATEGORY_OPTIONS } from '@/utils/approvalDisplay';

export function useApprovalCategory() {
  return useDictOptions('approval_category', [...APPROVAL_CATEGORY_OPTIONS]);
}
