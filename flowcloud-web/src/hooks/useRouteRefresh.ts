import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

/** 路由切换或返回时自动刷新数据，避免需手动 F5 */
export function useRouteRefresh(callback: () => void) {
  const location = useLocation();

  useEffect(() => {
    callback();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location.pathname, location.key]);
}
