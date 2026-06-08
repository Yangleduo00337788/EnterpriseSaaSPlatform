INSERT IGNORE INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, sort, status)
VALUES
    (33, 8, 'system:file', '文件管理', 'menu', '/system/files', 8, 1),
    (34, 33, 'system:file:view', '查看文件', 'button', NULL, 1, 1),
    (35, 33, 'system:file:download', '下载文件', 'button', NULL, 2, 1),
    (36, 33, 'system:file:delete', '删除文件', 'button', NULL, 3, 1);

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
VALUES
    (1, 33),
    (1, 34),
    (1, 35),
    (1, 36);
