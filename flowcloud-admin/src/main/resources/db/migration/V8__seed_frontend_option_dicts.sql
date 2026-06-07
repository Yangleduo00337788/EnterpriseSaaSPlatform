INSERT IGNORE INTO sys_dict_type (tenant_id, dict_code, dict_name, status, remark) VALUES
(1, 'message_event_type', '消息事件类型', 1, '消息模板事件类型选项'),
(1, 'role_data_scope', '角色数据范围', 1, '角色数据范围选项'),
(1, 'audit_module', '审计模块', 1, '审计日志模块筛选选项');

INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '任务分配', 'TASK_ASSIGNED', 0, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'message_event_type';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '审批通过', 'APPROVED', 1, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'message_event_type';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '审批驳回', 'REJECTED', 2, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'message_event_type';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '审批撤销', 'CANCELLED', 3, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'message_event_type';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '审批催办', 'REMIND', 4, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'message_event_type';

INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '全部数据', 'ALL', 0, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'role_data_scope';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '本部门', 'DEPT', 1, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'role_data_scope';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '仅本人', 'SELF', 2, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'role_data_scope';

INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '登录', 'login', 0, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'audit_module';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '审批', 'approval', 1, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'audit_module';
INSERT IGNORE INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status)
SELECT 1, id, '系统', 'system', 2, 1 FROM sys_dict_type WHERE tenant_id = 1 AND dict_code = 'audit_module';
