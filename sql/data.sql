-- 审流云测试数据脚本
-- 执行前请确保已运行 schema.sql 初始化表结构
-- 默认密码均为: 123456 (BCrypt加密)
-- 若中文显示为 ??????，请重新执行 schema.sql 与 data.sql，并重启后端服务

USE flowcloud;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据
TRUNCATE TABLE sys_message;
TRUNCATE TABLE approval_record;
TRUNCATE TABLE approval_task;
TRUNCATE TABLE approval_instance;
TRUNCATE TABLE approval_template;
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_dept;
TRUNCATE TABLE sys_permission;
TRUNCATE TABLE sys_tenant;

SET FOREIGN_KEY_CHECKS = 1;

-- BCrypt('123456')，由 Spring BCryptPasswordEncoder 生成
SET @pwd = '$2b$10$20Pvycfw94NEwlPjbXmogOYWjHVTCFoKonrJSoQ2gmheTtu.YqC6G';

-- ==================== 租户 ====================
INSERT INTO sys_tenant (id, tenant_code, tenant_name, contact_name, contact_phone, contact_email, logo, theme_color, status, plan_type, max_users) VALUES
(1, 'demo', '演示科技企业', '张总', '13800001001', 'admin@demo.com', NULL, '#3370FF', 1, 'pro', 100),
(2, 'acme', 'ACME贸易有限公司', '陈经理', '13800002001', 'admin@acme.com', NULL, '#00B42A', 1, 'basic', 50);

-- ==================== 部门 (租户1) ====================
INSERT INTO sys_dept (id, tenant_id, parent_id, dept_name, leader, sort, status) VALUES
(1,  1, 0, '演示科技企业', '张总',   0, 1),
(2,  1, 1, '技术部',       '李经理', 1, 1),
(3,  1, 1, '产品部',       '刘产品', 2, 1),
(4,  1, 1, '人力资源部',   '王人事', 3, 1),
(5,  1, 1, '财务部',       '赵财务', 4, 1),
(6,  1, 1, '市场部',       '孙市场', 5, 1),
(10, 2, 0, 'ACME贸易有限公司', '陈经理', 0, 1);

-- ==================== 角色 ====================
INSERT INTO sys_role (id, tenant_id, role_code, role_name, description, sort, status) VALUES
(1, 1, 'admin',    '管理员',   '企业管理员，拥有全部权限', 0, 1),
(2, 1, 'approver', '审批人',   '可处理审批任务',           1, 1),
(3, 1, 'employee', '普通员工', '可发起审批',               2, 1),
(4, 2, 'admin',    '管理员',   '企业管理员',               0, 1),
(5, 2, 'approver', '审批人',   '审批人员',                 1, 1),
(6, 2, 'employee', '普通员工', '普通员工',                 2, 1);

-- ==================== 用户 ====================
INSERT INTO sys_user (id, tenant_id, username, password, real_name, email, phone, dept_id, status, is_admin) VALUES
(1, 1, 'admin',   @pwd, '张总',   'admin@demo.com',    '13800001001', 1, 1, 1),
(2, 1, 'manager', @pwd, '李经理', 'manager@demo.com',  '13800001002', 2, 1, 0),
(3, 1, 'hr',      @pwd, '王人事', 'hr@demo.com',       '13800001003', 4, 1, 0),
(4, 1, 'finance', @pwd, '赵财务', 'finance@demo.com',  '13800001004', 5, 1, 0),
(5, 1, 'zhangsan',@pwd, '张三',   'zhangsan@demo.com', '13800001005', 2, 1, 0),
(6, 1, 'lisi',    @pwd, '李四',   'lisi@demo.com',     '13800001006', 3, 1, 0),
(7, 1, 'wangwu',  @pwd, '王五',   'wangwu@demo.com',   '13800001007', 2, 1, 0),
(8, 1, 'sunqi',   @pwd, '孙七',   'sunqi@demo.com',    '13800001008', 6, 1, 0),
(9, 2, 'admin',   @pwd, '陈经理', 'admin@acme.com',    '13800002001', 10, 1, 1);

-- ==================== 用户角色关联 ====================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2), (2, 3),
(3, 2), (3, 3),
(4, 2), (4, 3),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
(9, 4);

-- ==================== 权限菜单 ====================
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort, status) VALUES
(1,  0, 'dashboard',     '工作台',     'menu', '/dashboard',          'home',      0, 1),
(2,  0, 'approval',      '审批管理',   'menu', NULL,                  'approval',  1, 1),
(3,  2, 'approval:submit','发起审批',  'menu', '/approval/submit',    NULL,        0, 1),
(4,  2, 'approval:my',   '我的申请',   'menu', '/approval/my',        NULL,        1, 1),
(5,  2, 'approval:pending','待我审批', 'menu', '/approval/pending',   NULL,        2, 1),
(6,  2, 'approval:all',  '全部审批',   'menu', '/approval/all',       NULL,        3, 1),
(7,  0, 'template',      '流程模板',   'menu', '/templates',          'template',  2, 1),
(8,  0, 'system',        '系统管理',   'menu', NULL,                  'setting',   3, 1),
(9,  8, 'system:user',   '用户管理',   'menu', '/system/users',       NULL,        0, 1),
(22, 0, 'messages',      '消息中心',   'menu', '/messages',           'bell',      4, 1);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,22),
(2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,22),
(3,1),(3,3),(3,4),(3,22);

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort, status) VALUES
(10, 8, 'system:dept', '组织架构', 'menu', '/system/depts', NULL, 1, 1),
(11, 8, 'system:tenant', '租户中心', 'menu', '/system/tenant', NULL, 2, 1),
(12, 9, 'system:user:view', '查看用户', 'button', NULL, NULL, 0, 1),
(13, 9, 'system:user:edit', '编辑用户', 'button', NULL, NULL, 1, 1),
(14, 10, 'system:dept:view', '查看组织', 'button', NULL, NULL, 0, 1),
(15, 10, 'system:dept:edit', '编辑组织', 'button', NULL, NULL, 1, 1),
(16, 11, 'system:tenant:view', '查看租户', 'button', NULL, NULL, 0, 1),
(17, 11, 'system:tenant:edit', '编辑租户', 'button', NULL, NULL, 1, 1),
(18, 7, 'approval:template:manage', '管理模板', 'button', NULL, NULL, 0, 1),
(19, 6, 'approval:instance:viewAll', '查看全部审批', 'button', NULL, NULL, 0, 1),
(20, 5, 'approval:task:handle', '处理审批任务', 'button', NULL, NULL, 0, 1),
(21, 8, 'system:role:view', '查看角色', 'button', NULL, NULL, 3, 1),
(23, 8, 'system:position', '岗位管理', 'menu', '/system/positions', NULL, 4, 1),
(24, 23, 'system:position:view', '查看岗位', 'button', NULL, NULL, 0, 1),
(25, 23, 'system:position:edit', '编辑岗位', 'button', NULL, NULL, 1, 1);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,23),(1,24),(1,25),
(2,19),(2,20),(2,12),(2,14),(2,21),(2,23),(2,24),
(3,12);

-- ==================== 审批模板 (租户1) ====================
INSERT INTO approval_template (id, tenant_id, template_code, template_name, category, description, form_schema, flow_config, status, sort, pub_version) VALUES
(1, 1, 'LEAVE', '请假申请',
 'leave', '员工请假审批，支持年假、事假、病假',
 '{"fields":[{"name":"leaveType","label":"请假类型","type":"select","options":["年假","事假","病假","调休"]},{"name":"startDate","label":"开始日期","type":"date"},{"name":"endDate","label":"结束日期","type":"date"},{"name":"days","label":"请假天数","type":"number"},{"name":"reason","label":"请假事由","type":"textarea"}]}',
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"HR审批","type":"approval","approverIds":[3]}]',
 1, 1, 1),

(2, 1, 'EXPENSE', '报销申请',
 'expense', '日常费用报销审批',
 '{"fields":[{"name":"expenseType","label":"费用类型","type":"select","options":["差旅费","交通费","餐饮费","办公用品","其他"]},{"name":"amount","label":"报销金额(元)","type":"number"},{"name":"expenseDate","label":"费用发生日期","type":"date"},{"name":"description","label":"费用说明","type":"textarea"}]}',
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"财务审批","type":"approval","approverIds":[4]}]',
 1, 2, 1),

(3, 1, 'PURCHASE', '采购申请',
 'purchase', '办公用品及设备采购审批',
 '{"fields":[{"name":"itemName","label":"采购物品","type":"text"},{"name":"quantity","label":"数量","type":"number"},{"name":"unitPrice","label":"单价(元)","type":"number"},{"name":"totalAmount","label":"总金额(元)","type":"number"},{"name":"supplier","label":"供应商","type":"text"},{"name":"reason","label":"采购原因","type":"textarea"}]}',
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"财务审批","type":"approval","approverIds":[4]},{"index":2,"name":"总经理审批","type":"approval","approverIds":[1]}]',
 1, 3, 1),

(4, 1, 'CONTRACT', '合同审批',
 'contract', '商务合同签订审批',
 '{"fields":[{"name":"contractName","label":"合同名称","type":"text"},{"name":"partyB","label":"合作方","type":"text"},{"name":"amount","label":"合同金额(元)","type":"number"},{"name":"startDate","label":"合同开始日期","type":"date"},{"name":"endDate","label":"合同结束日期","type":"date"},{"name":"summary","label":"合同摘要","type":"textarea"}]}',
 '[{"index":0,"name":"部门主管审批","type":"approval","approverIds":[2]},{"index":1,"name":"总经理审批","type":"approval","approverIds":[1]}]',
 1, 4, 1),

(5, 1, 'SELF_TEST', '自审测试',
 'other', '发起后由申请人自行审批，用于流程联调测试',
 '{"fields":[{"name":"reason","label":"申请事由","type":"textarea"}]}',
 '[{"index":0,"name":"发起人自审","type":"self","approverIds":[]}]',
 1, 5, 1);

-- ==================== 审批实例 ====================
INSERT INTO approval_instance (id, tenant_id, instance_no, template_id, template_name, category, title, applicant_id, applicant_name, dept_id, form_data, status, current_node, current_approvers, submit_time, finish_time) VALUES
-- 已通过：张三年假请假
(1, 1, 'FC20260601001', 1, '请假申请', 'leave',
 '张三的年假请假申请', 5, '张三', 2,
 '{"leaveType":"年假","startDate":"2026-06-10","endDate":"2026-06-12","days":3,"reason":"回老家探亲"}',
 'approved', 2, NULL, '2026-06-01 09:30:00', '2026-06-01 16:20:00'),

-- 审批中：李四报销，待李经理审批
(2, 1, 'FC20260602001', 2, '报销申请', 'expense',
 '李四6月差旅费报销', 6, '李四', 3,
 '{"expenseType":"差旅费","amount":2350.00,"expenseDate":"2026-05-28","description":"北京出差交通及住宿费用"}',
 'pending', 0, '李经理', '2026-06-02 10:15:00', NULL),

-- 审批中：王五采购，已过主管，待财务审批
(3, 1, 'FC20260603001', 3, '采购申请', 'purchase',
 '王五申请采购开发笔记本', 7, '王五', 2,
 '{"itemName":"MacBook Pro 14","quantity":2,"unitPrice":12999,"totalAmount":25998,"supplier":"苹果授权经销商","reason":"新员工入职配备开发设备"}',
 'pending', 1, '赵财务', '2026-06-03 14:00:00', NULL),

-- 已驳回：张三餐饮报销
(4, 1, 'FC20260604001', 2, '报销申请', 'expense',
 '张三客户招待餐费报销', 5, '张三', 2,
 '{"expenseType":"餐饮费","amount":880.00,"expenseDate":"2026-05-30","description":"客户商务宴请"}',
 'rejected', 0, NULL, '2026-06-04 11:00:00', '2026-06-04 15:30:00'),

-- 审批中：李四事假
(5, 1, 'FC20260605001', 1, '请假申请', 'leave',
 '李四事假申请', 6, '李四', 3,
 '{"leaveType":"事假","startDate":"2026-06-09","endDate":"2026-06-09","days":1,"reason":"办理个人事务"}',
 'pending', 0, '李经理', '2026-06-05 08:45:00', NULL),

-- 已通过：王五合同审批
(6, 1, 'FC20260605002', 4, '合同审批', 'contract',
 '王五-SaaS服务采购合同', 7, '王五', 2,
 '{"contractName":"云服务年度采购合同","partyB":"阿里云","amount":48000,"startDate":"2026-07-01","endDate":"2027-06-30","summary":"采购云服务器及数据库服务一年"}',
 'approved', 2, NULL, '2026-06-05 09:30:00', '2026-06-05 17:00:00'),

-- 已撤销：孙七报销草稿撤销
(7, 1, 'FC20260606001', 2, '报销申请', 'expense',
 '孙七办公用品报销', 8, '孙七', 6,
 '{"expenseType":"办公用品","amount":320.00,"expenseDate":"2026-06-01","description":"采购打印纸和文具"}',
 'cancelled', 0, NULL, '2026-06-06 10:00:00', '2026-06-06 10:30:00'),

-- 审批中：管理员自审测试（demo/admin 登录后可测试通过/驳回）
(8, 1, 'FC20260607001', 5, '自审测试', 'other',
 '管理员自审测试申请', 1, '张总', 1,
 '{"reason":"测试自审审批流程"}',
 'pending', 0, '张总', '2026-06-07 10:00:00', NULL);

-- ==================== 审批任务 ====================
INSERT INTO approval_task (id, tenant_id, instance_id, instance_no, title, node_index, node_name, approver_id, approver_name, status, comment, handle_time) VALUES
-- 实例1 请假(已通过) - 2个节点均完成
(1, 1, 1, 'FC20260601001', '张三的年假请假申请', 0, '部门主管审批', 2, '李经理', 'approved', '同意，注意工作交接', '2026-06-01 11:00:00'),
(2, 1, 1, 'FC20260601001', '张三的年假请假申请', 1, 'HR审批',       3, '王人事', 'approved', '已备案',             '2026-06-01 16:20:00'),

-- 实例2 报销(审批中) - 待李经理
(3, 1, 2, 'FC20260602001', '李四6月差旅费报销', 0, '部门主管审批', 2, '李经理', 'pending', NULL, NULL),

-- 实例3 采购(审批中) - 主管已通过，财务待审
(4, 1, 3, 'FC20260603001', '王五申请采购开发笔记本', 0, '部门主管审批', 2, '李经理', 'approved', '设备确实需要，同意采购', '2026-06-03 16:00:00'),
(5, 1, 3, 'FC20260603001', '王五申请采购开发笔记本', 1, '财务审批',     4, '赵财务', 'pending',  NULL, NULL),

-- 实例4 报销(已驳回)
(6, 1, 4, 'FC20260604001', '张三客户招待餐费报销', 0, '部门主管审批', 2, '李经理', 'rejected', '超标，请按公司标准重新申请', '2026-06-04 15:30:00'),

-- 实例5 请假(审批中) - 待李经理
(7, 1, 5, 'FC20260605001', '李四事假申请', 0, '部门主管审批', 2, '李经理', 'pending', NULL, NULL),

-- 实例6 合同(已通过)
(8, 1, 6, 'FC20260605002', '王五-SaaS服务采购合同', 0, '部门主管审批', 2, '李经理', 'approved', '云服务必要支出，同意', '2026-06-05 11:00:00'),
(9, 1, 6, 'FC20260605002', '王五-SaaS服务采购合同', 1, '总经理审批',   1, '张总',   'approved', '批准签署',           '2026-06-05 17:00:00'),

-- 实例8 自审测试(审批中) - 待管理员自审
(10, 1, 8, 'FC20260607001', '管理员自审测试申请', 0, '发起人自审', 1, '张总', 'pending', NULL, NULL);

-- ==================== 审批记录 ====================
INSERT INTO approval_record (tenant_id, instance_id, node_index, node_name, operator_id, operator_name, action, comment) VALUES
(1, 1, NULL, NULL,           5, '张三',   'submit',   '提交审批'),
(1, 1, 0,    '部门主管审批', 2, '李经理', 'approve',  '同意，注意工作交接'),
(1, 1, 1,    'HR审批',       3, '王人事', 'approve',  '已备案'),
(1, 2, NULL, NULL,           6, '李四',   'submit',   '提交审批'),
(1, 3, NULL, NULL,           7, '王五',   'submit',   '提交审批'),
(1, 3, 0,    '部门主管审批', 2, '李经理', 'approve',  '设备确实需要，同意采购'),
(1, 4, NULL, NULL,           5, '张三',   'submit',   '提交审批'),
(1, 4, 0,    '部门主管审批', 2, '李经理', 'reject',   '超标，请按公司标准重新申请'),
(1, 5, NULL, NULL,           6, '李四',   'submit',   '提交审批'),
(1, 6, NULL, NULL,           7, '王五',   'submit',   '提交审批'),
(1, 6, 0,    '部门主管审批', 2, '李经理', 'approve',  '云服务必要支出，同意'),
(1, 6, 1,    '总经理审批',   1, '张总',   'approve',  '批准签署'),
(1, 7, NULL, NULL,           8, '孙七',   'submit',   '提交审批'),
(1, 7, NULL, NULL,           8, '孙七',   'cancel',   '金额有误，撤销重新填写'),
(1, 8, NULL, NULL,           1, '张总',   'submit',   '提交审批');

-- ==================== 消息通知 ====================
INSERT INTO sys_message (tenant_id, user_id, title, content, type, biz_type, biz_id, is_read) VALUES
(1, 2, '新的审批待处理', '李四提交了「李四6月差旅费报销」，请及时审批。',       'approval', 'expense',  2, 0),
(1, 2, '新的审批待处理', '李四提交了「李四事假申请」，请及时审批。',           'approval', 'leave',    5, 0),
(1, 4, '新的审批待处理', '王五提交了「王五申请采购开发笔记本」，请及时审批。', 'approval', 'purchase', 3, 0),
(1, 5, '审批已通过',     '您的「张三的年假请假申请」已通过全部审批。',         'approval', 'leave',    1, 1),
(1, 5, '审批已驳回',     '您的「张三客户招待餐费报销」已被驳回：超标，请按公司标准重新申请。', 'approval', 'expense', 4, 0),
(1, 7, '审批已通过',     '您的「王五-SaaS服务采购合同」已通过全部审批。',     'approval', 'contract', 6, 1),
(1, 1, '系统通知',       '欢迎使用审流云企业审批平台！',                     'system',   NULL,       NULL, 0),
(1, 1, '新的审批待处理', '您提交了「管理员自审测试申请」，请及时审批。',       'approval', 'other',    8,    0);

-- 重置自增ID
UPDATE sys_tenant SET expire_time = '2027-12-31 23:59:59', package_config = '{"storageGb":20,"workflowVersioning":true}', feature_config = '{"approval":true,"report":true,"message":true,"tenantSettings":true}';
UPDATE sys_dept SET leader_user_id = CASE id
    WHEN 1 THEN 1
    WHEN 2 THEN 2
    WHEN 3 THEN 6
    WHEN 4 THEN 3
    WHEN 5 THEN 4
    WHEN 6 THEN 8
    WHEN 10 THEN 9
END,
ancestors = CASE id
    WHEN 1 THEN '0'
    WHEN 2 THEN '0,1'
    WHEN 3 THEN '0,1'
    WHEN 4 THEN '0,1'
    WHEN 5 THEN '0,1'
    WHEN 6 THEN '0,1'
    WHEN 10 THEN '0'
END;
UPDATE sys_role SET data_scope = CASE role_code
    WHEN 'admin' THEN 'ALL'
    WHEN 'approver' THEN 'DEPT'
    ELSE 'SELF'
END;
UPDATE sys_user SET manager_id = CASE id
    WHEN 1 THEN NULL
    WHEN 2 THEN 1
    WHEN 3 THEN 1
    WHEN 4 THEN 1
    WHEN 5 THEN 2
    WHEN 6 THEN 2
    WHEN 7 THEN 2
    WHEN 8 THEN 1
    WHEN 9 THEN NULL
END,
job_title = CASE id
    WHEN 1 THEN '总经理'
    WHEN 2 THEN '技术经理'
    WHEN 3 THEN 'HR经理'
    WHEN 4 THEN '财务经理'
    WHEN 5 THEN '开发工程师'
    WHEN 6 THEN '产品经理'
    WHEN 7 THEN '开发工程师'
    WHEN 8 THEN '市场专员'
    WHEN 9 THEN '企业管理员'
END,
work_status = 'active';
UPDATE sys_permission SET path = '/templates' WHERE perm_code = 'template';
ALTER TABLE sys_tenant AUTO_INCREMENT = 3;
ALTER TABLE sys_dept AUTO_INCREMENT = 11;
ALTER TABLE sys_role AUTO_INCREMENT = 7;
ALTER TABLE sys_user AUTO_INCREMENT = 10;
ALTER TABLE sys_permission AUTO_INCREMENT = 22;
ALTER TABLE approval_template AUTO_INCREMENT = 6;
ALTER TABLE approval_instance AUTO_INCREMENT = 9;
ALTER TABLE approval_task AUTO_INCREMENT = 11;

-- 审计日志权限
INSERT IGNORE INTO sys_permission (perm_code, perm_name, perm_type, path, parent_id, sort, create_time)
VALUES
  ('system:audit',      '审计日志',    'menu',   '/system/audit-logs', 0, 90, NOW()),
  ('system:audit:view', '查看审计日志', 'button', NULL,                 0, 91, NOW());

-- 将审计权限绑定到管理员角色（role_id=1 对应 admin）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE perm_code IN ('system:audit', 'system:audit:view');

-- phase-d/e 新增权限与种子数据
INSERT IGNORE INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, sort, status) VALUES
(26, 8, 'system:role', '角色管理', 'menu', '/system/roles', 5, 1),
(27, 26, 'system:role:edit', '编辑角色', 'button', NULL, 1, 1),
(28, 0, 'report', '报表分析', 'menu', '/report', 5, 1),
(29, 8, 'system:dict', '系统字典', 'menu', '/system/dicts', 6, 1),
(30, 29, 'system:dict:edit', '编辑字典', 'button', NULL, 1, 1),
(31, 8, 'system:message-template', '消息模板', 'menu', '/system/message-templates', 7, 1),
(32, 31, 'system:message-template:edit', '编辑消息模板', 'button', NULL, 1, 1);

INSERT IGNORE INTO sys_role_permission (role_id, permission_id) VALUES
(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32);

INSERT IGNORE INTO sys_dict_type (tenant_id, dict_code, dict_name, status, remark) VALUES
(1, 'approval_category', '审批分类', 1, '审批模板分类'),
(1, 'approval_status', '审批状态', 1, '审批实例状态');

INSERT IGNORE INTO sys_message_template (tenant_id, template_code, template_name, event_type, title_template, content_template, status) VALUES
(1, 'TASK_ASSIGNED', '任务分配', 'TASK_ASSIGNED', '新的审批待处理', '「{operator}」提交了「{title}」，请及时审批。', 1),
(1, 'APPROVED', '审批通过', 'APPROVED', '审批已通过', '您的「{title}」已通过全部审批。', 1),
(1, 'REJECTED', '审批驳回', 'REJECTED', '审批已驳回', '您的「{title}」已被驳回{comment}。', 1),
(1, 'CANCELLED', '审批撤销', 'CANCELLED', '审批已撤销', '「{title}」已被撤销。', 1),
(1, 'REMIND', '审批催办', 'REMIND', '审批催办提醒', '「{operator}」催办了「{title}」，请尽快处理。', 1);
