import type { MenuVO, UserInfo } from '@/types';
import { canAccessPath } from '@/utils/permissions';

function normalizeChildren(menu: MenuVO): MenuVO[] {
  return menu.children ?? [];
}

export function filterAccessibleMenus(user: UserInfo | null, menus: MenuVO[]): MenuVO[] {
  return menus.flatMap((menu) => {
    const children = filterAccessibleMenus(user, normalizeChildren(menu));
    const hasVisibleChildren = children.length > 0;
    const hasPath = Boolean(menu.path?.trim());
    const canAccessSelf = !hasPath || canAccessPath(user, menu.path!.trim());
    if (!canAccessSelf && !hasVisibleChildren) {
      return [];
    }
    return [{ ...menu, children }];
  });
}

export function findFirstMenuPath(menus: MenuVO[]): string | null {
  for (const menu of menus) {
    const path = menu.path?.trim();
    if (path) {
      return path;
    }
    const childPath = findFirstMenuPath(normalizeChildren(menu));
    if (childPath) {
      return childPath;
    }
  }
  return null;
}

export function findMenuTrailByPath(menus: MenuVO[], pathname: string): MenuVO[] {
  for (const menu of menus) {
    if (menu.path === pathname) {
      return [menu];
    }
    const childTrail = findMenuTrailByPath(normalizeChildren(menu), pathname);
    if (childTrail.length > 0) {
      return [menu, ...childTrail];
    }
  }
  return [];
}
