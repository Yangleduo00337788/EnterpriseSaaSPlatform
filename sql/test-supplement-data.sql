-- 审流云功能测试补数脚本
-- 用途：在执行 schema.sql + data.sql 之后，补齐功能测试所需的缺失数据
-- 覆盖：岗位、用户岗位、模板版本、附件、审计日志、字典项

USE flowcloud;

SET NAMES utf8mb4;

-- ==================== 岗位测试数据 ====================
DELETE FROM sys_user_position WHERE user_id IN (1, 2, 3, 4, 5, 6, 7, 8);
DELETE FROM sys_position WHERE id BETWEEN 1001 AND 1006;

INSERT INTO sys_position (id, tenant_id, position_code, position_name, dept_id, sort, status, remark) VALUES
(1001, 1, 'CEO',        '总经理',     1, 0, 1, '企业负责人'),
(1002, 1, 'TECH_MGR',   '技术经理',   2, 1, 1, '技术部门负责人'),
(1003, 1, 'HR_MGR',     'HR经理',     4, 2, 1, '人力资源负责人'),
(1004, 1, 'FIN_MGR',    '财务经理',   5, 3, 1, '财务负责人'),
(1005, 1, 'DEV_ENG',    '开发工程师', 2, 4, 1, '研发岗位'),
(1006, 1, 'PM',         '产品经理',   3, 5, 1, '产品岗位');

INSERT INTO sys_user_position (user_id, position_id) VALUES
(1, 1001),
(2, 1002),
(3, 1003),
(4, 1004),
(5, 1005),
(6, 1006),
(7, 1005);

-- ==================== 字典测试数据 ====================
-- approval_category / approval_status 在部分库里可能只有 dict_type，没有 dict_data
INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '请假', 'leave', 0, 1, '审批分类'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_category'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'leave'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '报销', 'expense', 1, 1, '审批分类'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_category'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'expense'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '合同', 'contract', 2, 1, '审批分类'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_category'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'contract'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '采购', 'purchase', 3, 1, '审批分类'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_category'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'purchase'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '其他', 'other', 4, 1, '审批分类'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_category'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'other'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '草稿', 'draft', 0, 1, '审批状态'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_status'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'draft'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '审批中', 'pending', 1, 1, '审批状态'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_status'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'pending'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '已通过', 'approved', 2, 1, '审批状态'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_status'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'approved'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '已驳回', 'rejected', 3, 1, '审批状态'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_status'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'rejected'
  );

INSERT INTO sys_dict_data (tenant_id, dict_type_id, dict_label, dict_value, sort, status, remark)
SELECT 1, t.id, '已撤销', 'cancelled', 4, 1, '审批状态'
FROM sys_dict_type t
WHERE t.tenant_id = 1 AND t.dict_code = 'approval_status'
  AND NOT EXISTS (
    SELECT 1 FROM sys_dict_data d
    WHERE d.dict_type_id = t.id AND d.dict_value = 'cancelled'
  );

-- ==================== 模板版本测试数据 ====================
DELETE FROM approval_template_version WHERE id BETWEEN 2001 AND 2005;

INSERT INTO approval_template_version (id, tenant_id, template_id, version, flow_config, form_schema, remark, create_time) VALUES
(2001, 1, 1, 1,
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"HR审批","type":"approval","approverIds":[3]}]',
 '{"fields":[{"name":"leaveType","label":"请假类型","type":"select","options":["年假","事假","病假","调休"]},{"name":"startDate","label":"开始日期","type":"date"},{"name":"endDate","label":"结束日期","type":"date"},{"name":"days","label":"请假天数","type":"number"},{"name":"reason","label":"请假事由","type":"textarea"}]}',
 '初始化发布版本',
 '2026-06-01 08:30:00'),
(2002, 1, 2, 1,
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"财务审批","type":"approval","approverIds":[4]}]',
 '{"fields":[{"name":"expenseType","label":"费用类型","type":"select","options":["差旅费","交通费","餐饮费","办公用品","其他"]},{"name":"amount","label":"报销金额(元)","type":"number"},{"name":"expenseDate","label":"费用发生日期","type":"date"},{"name":"description","label":"费用说明","type":"textarea"}]}',
 '初始化发布版本',
 '2026-06-02 09:00:00'),
(2003, 1, 3, 1,
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"财务审批","type":"approval","approverIds":[4]},{"index":2,"name":"总经理审批","type":"approval","approverIds":[1]}]',
 '{"fields":[{"name":"itemName","label":"采购物品","type":"text"},{"name":"quantity","label":"数量","type":"number"},{"name":"unitPrice","label":"单价(元)","type":"number"},{"name":"totalAmount","label":"总金额(元)","type":"number"},{"name":"supplier","label":"供应商","type":"text"},{"name":"reason","label":"采购原因","type":"textarea"}]}',
 '初始化发布版本',
 '2026-06-03 09:20:00'),
(2004, 1, 4, 1,
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"总经理审批","type":"approval","approverIds":[1]}]',
 '{"fields":[{"name":"contractName","label":"合同名称","type":"text"},{"name":"partyB","label":"合作方","type":"text"},{"name":"amount","label":"合同金额(元)","type":"number"},{"name":"startDate","label":"合同开始日期","type":"date"},{"name":"endDate","label":"合同结束日期","type":"date"},{"name":"summary","label":"合同摘要","type":"textarea"}]}',
 '初始化发布版本',
 '2026-06-04 10:00:00'),
(2005, 1, 5, 1,
 '[{"index":0,"name":"发起人自审","type":"self","approverIds":[]}]',
 '{"fields":[{"name":"reason","label":"申请事由","type":"textarea"}]}',
 '联调用自审模板版本',
 '2026-06-07 09:40:00');

-- ==================== 附件测试数据 ====================
DELETE FROM attachment_file WHERE id BETWEEN 3001 AND 3004;

INSERT INTO attachment_file (id, tenant_id, biz_type, biz_id, field_name, original_name, file_key, file_url, file_size, mime_type, uploader_id, uploader_name, create_time) VALUES
(3001, 1, 'instance', 2, 'invoice', '差旅报销凭证.pdf', 'attachments/instance/2/travel-expense.pdf', 'http://localhost:9000/flowcloud/attachments/instance/2/travel-expense.pdf', 245760, 'application/pdf', 6, '李四', '2026-06-02 10:10:00'),
(3002, 1, 'instance', 3, 'quotation', '采购报价单.xlsx', 'attachments/instance/3/purchase-quotation.xlsx', 'http://localhost:9000/flowcloud/attachments/instance/3/purchase-quotation.xlsx', 98304, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 7, '王五', '2026-06-03 13:50:00'),
(3003, 1, 'instance', 8, 'attachment', '自审测试附件.txt', 'attachments/instance/8/self-test.txt', 'http://localhost:9000/flowcloud/attachments/instance/8/self-test.txt', 2048, 'text/plain', 1, '张总', '2026-06-07 09:58:00'),
(3004, 1, 'template', 4, 'contractTemplate', '合同审批模板说明.docx', 'attachments/template/4/contract-template.docx', 'http://localhost:9000/flowcloud/attachments/template/4/contract-template.docx', 65536, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, '张总', '2026-06-04 09:30:00');

-- ==================== 审计日志测试数据 ====================
DELETE FROM sys_audit_log WHERE id BETWEEN 4001 AND 4010;

INSERT INTO sys_audit_log (id, tenant_id, user_id, user_name, action, target_type, target_id, target_name, result, detail, ip, create_time) VALUES
(4001, 1, 1, '张总',   'LOGIN',            'login',    '1', '管理员登录',         'success', '{"browser":"Chrome"}',                              '127.0.0.1', '2026-06-07 08:00:00'),
(4002, 1, 1, '张总',   'PUBLISH_TEMPLATE', 'template', '5', '自审测试',           'success', '{"remark":"联调发布"}',                             '127.0.0.1', '2026-06-07 09:45:00'),
(4003, 1, 1, '张总',   'SUBMIT_APPROVAL',  'instance', '8', '管理员自审测试申请', 'success', '{"category":"other"}',                             '127.0.0.1', '2026-06-07 10:00:00'),
(4004, 1, 2, '李经理', 'APPROVE_TASK',     'instance', '1', '张三的年假请假申请', 'success', '{"comment":"同意，注意工作交接"}',                 '127.0.0.1', '2026-06-01 11:00:00'),
(4005, 1, 3, '王人事', 'APPROVE_TASK',     'instance', '1', '张三的年假请假申请', 'success', '{"comment":"已备案"}',                             '127.0.0.1', '2026-06-01 16:20:00'),
(4006, 1, 2, '李经理', 'REJECT_TASK',      'instance', '4', '张三客户招待餐费报销', 'success', '{"comment":"超标，请按公司标准重新申请"}',        '127.0.0.1', '2026-06-04 15:30:00'),
(4007, 1, 8, '孙七',   'SUBMIT_APPROVAL',  'instance', '7', '孙七办公用品报销',   'success', '{"category":"expense"}',                           '127.0.0.1', '2026-06-06 10:00:00'),
(4008, 1, 8, '孙七',   'LOGOUT',           'system',   '7', '孙七办公用品报销',   'success', '{"reason":"用户主动退出后重新发起"}',              '127.0.0.1', '2026-06-06 10:31:00'),
(4009, 1, 1, '张总',   'DISABLE_TEMPLATE', 'template', '4', '合同审批',           'fail',    '{"message":"模板正在被引用，禁止停用演示失败样例"}', '127.0.0.1', '2026-06-07 11:00:00'),
(4010, 1, 1, '张总',   'LOGIN',            'login',    '1', '管理员登录',         'fail',    '{"message":"验证码错误示例"}',                     '127.0.0.1', '2026-06-07 11:10:00');

-- ==================== 自增修正 ====================
ALTER TABLE sys_position AUTO_INCREMENT = 1007;
ALTER TABLE approval_template_version AUTO_INCREMENT = 2006;
ALTER TABLE attachment_file AUTO_INCREMENT = 3005;
ALTER TABLE sys_audit_log AUTO_INCREMENT = 4011;
