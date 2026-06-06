-- 补齐 sql/data.sql 中缺失的权限及角色授权（messages、系统子权限等）
INSERT IGNORE INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort, status) VALUES
(22,  0,  'messages',                   '消息中心',     'menu',   '/messages',           'bell',    4, 1),
(10,  8,  'system:dept',                '组织架构',     'menu',   '/system/depts',       NULL,      1, 1),
(11,  8,  'system:tenant',              '租户中心',     'menu',   '/system/tenant',      NULL,      2, 1),
(12,  9,  'system:user:view',           '查看用户',     'button', NULL,                  NULL,      0, 1),
(13,  9,  'system:user:edit',           '编辑用户',     'button', NULL,                  NULL,      1, 1),
(14,  10, 'system:dept:view',           '查看组织',     'button', NULL,                  NULL,      0, 1),
(15,  10, 'system:dept:edit',           '编辑组织',     'button', NULL,                  NULL,      1, 1),
(16,  11, 'system:tenant:view',         '查看租户',     'button', NULL,                  NULL,      0, 1),
(17,  11, 'system:tenant:edit',         '编辑租户',     'button', NULL,                  NULL,      1, 1),
(18,  7,  'approval:template:manage',   '管理模板',     'button', NULL,                  NULL,      0, 1),
(19,  6,  'approval:instance:viewAll',  '查看全部审批', 'button', NULL,                  NULL,      0, 1),
(20,  5,  'approval:task:handle',       '处理审批任务', 'button', NULL,                  NULL,      0, 1),
(21,  8,  'system:role:view',           '查看角色',     'button', NULL,                  NULL,      3, 1),
(23,  8,  'system:position',            '岗位管理',     'menu',   '/system/positions',   NULL,      4, 1),
(24,  23, 'system:position:view',        '查看岗位',     'button', NULL,                  NULL,      0, 1),
(25,  23, 'system:position:edit',        '编辑岗位',     'button', NULL,                  NULL,      1, 1);

-- admin 角色：补齐全部权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id) VALUES
(1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17),
(1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25);

-- approver 角色：审批 + 消息 + 基础查看
INSERT IGNORE INTO sys_role_permission (role_id, permission_id) VALUES
(2, 12), (2, 14), (2, 19), (2, 20), (2, 21), (2, 22), (2, 23), (2, 24);

-- employee 角色：消息 + 用户查看
INSERT IGNORE INTO sys_role_permission (role_id, permission_id) VALUES
(3, 12), (3, 22);
