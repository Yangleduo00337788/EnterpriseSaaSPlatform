INSERT IGNORE INTO sys_permission (perm_code, perm_name, perm_type, path, icon, parent_id, sort, status)
VALUES ('insight', '消息与分析', 'menu', NULL, 'report', 0, 2, 1);

UPDATE sys_permission
SET parent_id = 2,
    sort = 4
WHERE perm_code = 'template';

UPDATE sys_permission
SET parent_id = (SELECT id FROM (SELECT id FROM sys_permission WHERE perm_code = 'insight' LIMIT 1) AS t),
    sort = 0
WHERE perm_code = 'messages';

UPDATE sys_permission
SET parent_id = (SELECT id FROM (SELECT id FROM sys_permission WHERE perm_code = 'insight' LIMIT 1) AS t),
    sort = 1
WHERE perm_code = 'report';

UPDATE sys_permission
SET parent_id = 8,
    sort = 11
WHERE perm_code = 'system:audit';
