import { useAppSelector } from '@/hooks/useAppDispatch';
import { canAccessPath, hasPermission } from '@/utils/permissions';

export function usePermission() {
  const user = useAppSelector((s) => s.auth.user);
  return {
    user,
    hasPermission: (perm: string) => hasPermission(user, perm),
    canAccessPath: (path: string) => canAccessPath(user, path),
  };
}
