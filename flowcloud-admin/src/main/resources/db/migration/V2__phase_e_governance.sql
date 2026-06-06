-- 系统字典
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    dict_code VARCHAR(64) NOT NULL,
    dict_name VARCHAR(128) NOT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(256),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_tenant_dict_code (tenant_id, dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型';

CREATE TABLE IF NOT EXISTS sys_dict_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    dict_type_id BIGINT NOT NULL,
    dict_label VARCHAR(128) NOT NULL,
    dict_value VARCHAR(128) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    remark VARCHAR(256),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_dict_type (dict_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据';

-- 消息模板
CREATE TABLE IF NOT EXISTS sys_message_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    template_code VARCHAR(64) NOT NULL,
    template_name VARCHAR(128) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    title_template VARCHAR(256) NOT NULL,
    content_template TEXT NOT NULL,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_tenant_event (tenant_id, event_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息模板';

-- 新增权限
INSERT IGNORE INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, sort, status) VALUES
(26, 8, 'system:role', '角色管理', 'menu', '/system/roles', 5, 1),
(27, 26, 'system:role:edit', '编辑角色', 'button', NULL, 1, 1),
(28, 0, 'report', '报表分析', 'menu', '/report', 5, 1),
(29, 8, 'system:dict', '系统字典', 'menu', '/system/dicts', 6, 1),
(30, 29, 'system:dict:edit', '编辑字典', 'button', NULL, 1, 1),
(31, 8, 'system:message-template', '消息模板', 'menu', '/system/message-templates', 7, 1),
(32, 31, 'system:message-template:edit', '编辑消息模板', 'button', NULL, 1, 1);

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE perm_code IN (
    'system:role', 'system:role:edit', 'report', 'system:dict', 'system:dict:edit',
    'system:message-template', 'system:message-template:edit'
);

-- 默认字典
INSERT IGNORE INTO sys_dict_type (tenant_id, dict_code, dict_name, status, remark) VALUES
(1, 'approval_category', '审批分类', 1, '审批模板分类'),
(1, 'approval_status', '审批状态', 1, '审批实例状态');

INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '请假', 'leave', 0, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_category';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '报销', 'expense', 1, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_category';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '合同', 'contract', 2, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_category';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '采购', 'purchase', 3, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_category';

INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '待审批', 'pending', 0, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_status';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '已通过', 'approved', 1, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_status';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '已驳回', 'rejected', 2, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'approval_status';

-- 默认消息模板
INSERT IGNORE INTO sys_message_template (tenant_id, template_code, template_name, event_type, title_template, content_template, status) VALUES
(1, 'TASK_ASSIGNED', '任务分配', 'TASK_ASSIGNED', '新的审批待处理', '「{operator}」提交了「{title}」，请及时审批。', 1),
(1, 'APPROVED', '审批通过', 'APPROVED', '审批已通过', '您的「{title}」已通过全部审批。', 1),
(1, 'REJECTED', '审批驳回', 'REJECTED', '审批已驳回', '您的「{title}」已被驳回{comment}。', 1),
(1, 'CANCELLED', '审批撤销', 'CANCELLED', '审批已撤销', '「{title}」已被撤销。', 1),
(1, 'REMIND', '审批催办', 'REMIND', '审批催办提醒', '「{operator}」催办了「{title}」，请尽快处理。', 1);
