-- 审流云数据库初始化脚本
CREATE DATABASE IF NOT EXISTS flowcloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE flowcloud;

-- 租户表
CREATE TABLE IF NOT EXISTS sys_tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_code VARCHAR(64) NOT NULL UNIQUE COMMENT '租户编码',
    tenant_name VARCHAR(128) NOT NULL COMMENT '租户名称',
    contact_name VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(128) COMMENT '联系邮箱',
    logo VARCHAR(512) COMMENT 'Logo URL',
    theme_color VARCHAR(16) DEFAULT '#3370FF' COMMENT '主题色',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    plan_type VARCHAR(32) DEFAULT 'basic' COMMENT '套餐类型',
    max_users INT DEFAULT 50 COMMENT '最大用户数',
    expire_time DATETIME NULL COMMENT '套餐到期时间',
    package_config TEXT NULL COMMENT '套餐配置JSON',
    feature_config TEXT NULL COMMENT '功能开关JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(128) NOT NULL COMMENT '部门名称',
    leader VARCHAR(64) COMMENT '负责人',
    leader_user_id BIGINT NULL COMMENT '负责人用户ID',
    ancestors VARCHAR(512) DEFAULT '0' COMMENT '祖级链路',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) COMMENT '真实姓名',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(512) COMMENT '头像',
    dept_id BIGINT COMMENT '部门ID',
    manager_id BIGINT NULL COMMENT '直属上级ID',
    job_title VARCHAR(64) NULL COMMENT '岗位',
    work_status VARCHAR(32) DEFAULT 'active' COMMENT '在岗状态',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    is_admin TINYINT DEFAULT 0 COMMENT '是否管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_tenant_username (tenant_id, username),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(256) COMMENT '描述',
    data_scope VARCHAR(32) DEFAULT 'SELF' COMMENT '数据范围',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    perm_code VARCHAR(64) NOT NULL,
    perm_name VARCHAR(64) NOT NULL,
    perm_type VARCHAR(16) COMMENT 'menu/button',
    path VARCHAR(256),
    icon VARCHAR(64),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联';

-- 审批模板表
CREATE TABLE IF NOT EXISTS approval_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    template_code VARCHAR(64) NOT NULL,
    template_name VARCHAR(128) NOT NULL,
    category VARCHAR(32) NOT NULL COMMENT 'leave/expense/contract/purchase',
    description VARCHAR(512),
    form_schema TEXT COMMENT '表单JSON Schema',
    flow_config TEXT COMMENT '流程节点配置JSON',
    status TINYINT DEFAULT 1,
    sort INT DEFAULT 0,
    pub_version INT DEFAULT 0 COMMENT 'published version',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批模板';

-- 审批实例表
CREATE TABLE IF NOT EXISTS approval_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    instance_no VARCHAR(32) NOT NULL UNIQUE,
    template_id BIGINT NOT NULL,
    template_name VARCHAR(128),
    category VARCHAR(32),
    title VARCHAR(256) NOT NULL,
    applicant_id BIGINT NOT NULL,
    applicant_name VARCHAR(64),
    dept_id BIGINT,
    form_data TEXT,
    status VARCHAR(16) DEFAULT 'pending',
    current_node INT DEFAULT 0,
    current_approvers VARCHAR(512),
    flow_config_snapshot TEXT NULL COMMENT '提交时流程配置快照',
    submit_time DATETIME,
    finish_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_tenant_status (tenant_id, status),
    INDEX idx_applicant (applicant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批实例';

-- 审批任务表
CREATE TABLE IF NOT EXISTS approval_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    instance_id BIGINT NOT NULL,
    instance_no VARCHAR(32),
    title VARCHAR(256),
    node_index INT NOT NULL,
    node_name VARCHAR(64),
    approver_id BIGINT NOT NULL,
    approver_name VARCHAR(64),
    status VARCHAR(16) DEFAULT 'pending',
    comment VARCHAR(512),
    handle_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_approver_status (approver_id, status),
    INDEX idx_instance (instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批任务';

-- 审批记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    instance_id BIGINT NOT NULL,
    node_index INT,
    node_name VARCHAR(64),
    operator_id BIGINT,
    operator_name VARCHAR(64),
    action VARCHAR(16),
    comment VARCHAR(512),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_instance (instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录';

-- 消息通知表
CREATE TABLE IF NOT EXISTS sys_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT,
    type VARCHAR(16) DEFAULT 'system',
    biz_type VARCHAR(32),
    biz_id BIGINT,
    is_read TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知';

-- 岗位表
CREATE TABLE IF NOT EXISTS sys_position (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    position_code VARCHAR(64) NOT NULL COMMENT '岗位编码',
    position_name VARCHAR(64) NOT NULL COMMENT '岗位名称',
    dept_id BIGINT COMMENT '所属部门',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    remark VARCHAR(256),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS sys_user_position (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    position_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_position (user_id, position_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户岗位关联';

-- 审批模板版本快照表
CREATE TABLE IF NOT EXISTS approval_template_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    version INT NOT NULL COMMENT '版本号',
    flow_config TEXT COMMENT '流程配置快照',
    form_schema TEXT COMMENT '表单Schema快照',
    remark VARCHAR(256) COMMENT '发布备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_template_version (template_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批模板版本快照';

-- 附件文件表
CREATE TABLE IF NOT EXISTS attachment_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    biz_type VARCHAR(32) COMMENT '业务类型 instance/template',
    biz_id BIGINT COMMENT '业务ID',
    field_name VARCHAR(64) COMMENT '表单字段名',
    original_name VARCHAR(256) NOT NULL COMMENT '原始文件名',
    file_key VARCHAR(512) NOT NULL COMMENT '存储路径key',
    file_url VARCHAR(512) NOT NULL COMMENT '访问URL',
    file_size BIGINT COMMENT '文件大小(bytes)',
    mime_type VARCHAR(128) COMMENT 'MIME类型',
    uploader_id BIGINT COMMENT '上传人ID',
    uploader_name VARCHAR(64) COMMENT '上传人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件文件';

-- 审计日志表
CREATE TABLE IF NOT EXISTS sys_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT COMMENT '租户ID（平台级操作可为NULL）',
    user_id BIGINT COMMENT '操作人ID',
    user_name VARCHAR(64) COMMENT '操作人姓名',
    action VARCHAR(64) NOT NULL COMMENT '操作动作',
    target_type VARCHAR(32) COMMENT '目标类型 user/instance/template/tenant',
    target_id VARCHAR(64) COMMENT '目标ID',
    target_name VARCHAR(128) COMMENT '目标名称',
    result VARCHAR(16) DEFAULT 'success' COMMENT 'success/fail',
    detail TEXT COMMENT '附加详情JSON',
    ip VARCHAR(64) COMMENT '客户端IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_time (tenant_id, create_time),
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志';

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

-- 已有库字符集修复（可选，解决历史乱码数据）
ALTER DATABASE flowcloud CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
