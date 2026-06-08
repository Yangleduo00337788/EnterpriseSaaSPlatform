INSERT IGNORE INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, sort, status)
VALUES
    (37, 8, 'system:menu', '菜单管理', 'menu', '/system/menus', 9, 1),
    (38, 37, 'system:menu:view', '查看菜单', 'button', NULL, 1, 1),
    (39, 37, 'system:menu:edit', '编辑菜单', 'button', NULL, 2, 1);

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
VALUES
    (1, 37),
    (1, 38),
    (1, 39);
