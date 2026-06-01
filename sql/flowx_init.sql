-- ============================================================================
-- FlowX Enterprise SaaS Platform - Database Initialization Script
-- MySQL 8.4 | InnoDB | utf8mb4
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- SYSTEM TABLES (sys_)
-- ============================================================================

-- ----------------------------
-- 1. sys_tenant - 租户管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_tenant` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_name`   VARCHAR(100) NOT NULL                COMMENT '租户名称',
    `contact_name`  VARCHAR(50)  DEFAULT NULL            COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20)  DEFAULT NULL            COMMENT '联系人电话',
    `contact_email` VARCHAR(100) DEFAULT NULL            COMMENT '联系人邮箱',
    `domain`        VARCHAR(200) DEFAULT NULL            COMMENT '绑定域名',
    `logo_url`      VARCHAR(500) DEFAULT NULL            COMMENT '租户Logo地址',
    `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `expire_time`   DATETIME     DEFAULT NULL            COMMENT '过期时间',
    `account_limit` INT          NOT NULL DEFAULT 0      COMMENT '账号上限（0不限制）',
    `package_id`    BIGINT       DEFAULT NULL            COMMENT '租户套餐ID',
    `remark`        VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `tenant_id`     BIGINT       NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT       DEFAULT NULL            COMMENT '创建者',
    `update_by`     BIGINT       DEFAULT NULL            COMMENT '更新者',
    `deleted`       TINYINT      NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_tenant_status` (`status`),
    INDEX `idx_tenant_expire` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户管理表';

-- ----------------------------
-- 2. sys_tenant_package - 租户套餐
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_tenant_package` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `package_name` VARCHAR(100) NOT NULL                COMMENT '套餐名称',
    `menu_ids`    JSON          DEFAULT NULL            COMMENT '关联菜单ID集合',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户套餐表';

-- ----------------------------
-- 3. sys_user - 用户管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)   NOT NULL                COMMENT '用户账号',
    `password`    VARCHAR(200)  NOT NULL                COMMENT '密码',
    `nickname`    VARCHAR(50)   NOT NULL                COMMENT '用户昵称',
    `email`       VARCHAR(100)  DEFAULT NULL            COMMENT '邮箱',
    `phone`       VARCHAR(20)   DEFAULT NULL            COMMENT '手机号',
    `gender`      TINYINT       DEFAULT 0               COMMENT '性别（0未知 1男 2女）',
    `avatar`      VARCHAR(500)  DEFAULT NULL            COMMENT '头像地址',
    `dept_id`     BIGINT        DEFAULT NULL            COMMENT '部门ID',
    `position_id` BIGINT        DEFAULT NULL            COMMENT '岗位ID',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `login_ip`    VARCHAR(50)   DEFAULT NULL            COMMENT '最后登录IP',
    `login_time`  DATETIME      DEFAULT NULL            COMMENT '最后登录时间',
    `remark`      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username` (`username`, `tenant_id`, `deleted`),
    INDEX `idx_user_dept` (`dept_id`),
    INDEX `idx_user_status` (`status`),
    INDEX `idx_user_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 4. sys_role - 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name`   VARCHAR(50)   NOT NULL                COMMENT '角色名称',
    `role_key`    VARCHAR(50)   NOT NULL                COMMENT '角色权限字符串',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `data_scope`  TINYINT       NOT NULL DEFAULT 1      COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_role_key` (`role_key`),
    INDEX `idx_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 5. sys_user_role - 用户角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '用户ID',
    `role_id`     BIGINT   NOT NULL               COMMENT '角色ID',
    `tenant_id`   BIGINT   NOT NULL DEFAULT 1     COMMENT '租户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT   DEFAULT NULL           COMMENT '创建者',
    `update_by`   BIGINT   DEFAULT NULL           COMMENT '更新者',
    `deleted`     TINYINT  NOT NULL DEFAULT 0     COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_user_role` (`user_id`, `role_id`),
    INDEX `idx_ur_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 6. sys_menu - 菜单/权限表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `menu_name`   VARCHAR(50)   NOT NULL                COMMENT '菜单名称',
    `parent_id`   BIGINT        NOT NULL DEFAULT 0      COMMENT '父菜单ID（0为顶级）',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `path`        VARCHAR(200)  DEFAULT ''               COMMENT '路由地址',
    `component`   VARCHAR(200)  DEFAULT NULL            COMMENT '组件路径',
    `menu_type`   CHAR(1)       NOT NULL DEFAULT 'M'    COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `permission`  VARCHAR(100)  DEFAULT NULL            COMMENT '权限标识',
    `icon`        VARCHAR(100)  DEFAULT '#'             COMMENT '菜单图标',
    `visible`     TINYINT       NOT NULL DEFAULT 1      COMMENT '是否可见（0隐藏 1显示）',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_menu_parent` (`parent_id`),
    INDEX `idx_menu_type` (`menu_type`),
    INDEX `idx_menu_permission` (`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- ----------------------------
-- 7. sys_role_menu - 角色菜单关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`     BIGINT   NOT NULL               COMMENT '角色ID',
    `menu_id`     BIGINT   NOT NULL               COMMENT '菜单ID',
    `tenant_id`   BIGINT   NOT NULL DEFAULT 1     COMMENT '租户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT   DEFAULT NULL           COMMENT '创建者',
    `update_by`   BIGINT   DEFAULT NULL           COMMENT '更新者',
    `deleted`     TINYINT  NOT NULL DEFAULT 0     COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_role_menu` (`role_id`, `menu_id`),
    INDEX `idx_rm_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- 8. sys_dept - 部门表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dept_name`   VARCHAR(50)   NOT NULL                COMMENT '部门名称',
    `parent_id`   BIGINT        NOT NULL DEFAULT 0      COMMENT '父部门ID（0为顶级）',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `leader`      VARCHAR(50)   DEFAULT NULL            COMMENT '负责人',
    `phone`       VARCHAR(20)   DEFAULT NULL            COMMENT '联系电话',
    `email`       VARCHAR(100)  DEFAULT NULL            COMMENT '邮箱',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `order_num`   INT           NOT NULL DEFAULT 0      COMMENT '排序号',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_dept_parent` (`parent_id`),
    INDEX `idx_dept_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ----------------------------
-- 9. sys_position - 岗位表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_position` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `position_name`  VARCHAR(50)   NOT NULL                COMMENT '岗位名称',
    `position_code`  VARCHAR(50)   NOT NULL                COMMENT '岗位编码',
    `sort`           INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `status`         TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `remark`         VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`      BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`      BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`        TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_position_code` (`position_code`, `tenant_id`, `deleted`),
    INDEX `idx_position_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- ----------------------------
-- 10. sys_dict_type - 字典类型表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_type` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_name`   VARCHAR(100)  NOT NULL                COMMENT '字典名称',
    `dict_type`   VARCHAR(100)  NOT NULL                COMMENT '字典类型',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_dict_type` (`dict_type`, `tenant_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ----------------------------
-- 11. sys_dict_data - 字典数据表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type`   VARCHAR(100)  NOT NULL                COMMENT '字典类型',
    `dict_label`  VARCHAR(100)  NOT NULL                COMMENT '字典标签',
    `dict_value`  VARCHAR(100)  NOT NULL                COMMENT '字典键值',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`   BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`   BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_dict_data_type` (`dict_type`),
    INDEX `idx_dict_data_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- ----------------------------
-- 12. sys_config - 系统配置表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_name`  VARCHAR(100)  NOT NULL                COMMENT '配置名称',
    `config_key`   VARCHAR(100)  NOT NULL                COMMENT '配置键',
    `config_value` VARCHAR(2000) NOT NULL DEFAULT ''     COMMENT '配置值',
    `config_type`  CHAR(1)       DEFAULT 'N'            COMMENT '配置类型（Y系统内置 N非内置）',
    `remark`       VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `tenant_id`    BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`    BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`    BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`      TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_config_key` (`config_key`, `tenant_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- 13. sys_operation_log - 操作日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`           VARCHAR(50)   DEFAULT NULL            COMMENT '操作模块',
    `business_type`   INT           DEFAULT 0               COMMENT '业务类型（0其他 1新增 2修改 3删除 4授权 5导出 6导入）',
    `method`          VARCHAR(200)  DEFAULT NULL            COMMENT '请求方法',
    `request_method`  VARCHAR(10)   DEFAULT NULL            COMMENT '请求方式（GET/POST等）',
    `request_url`     VARCHAR(500)  DEFAULT NULL            COMMENT '请求URL',
    `request_param`   TEXT          DEFAULT NULL            COMMENT '请求参数',
    `response_result` TEXT          DEFAULT NULL            COMMENT '返回结果',
    `operator_type`   INT           DEFAULT 0               COMMENT '操作类别（0其他 1后台用户 2手机端用户）',
    `oper_user_id`    BIGINT        DEFAULT NULL            COMMENT '操作人员ID',
    `oper_user_name`  VARCHAR(50)   DEFAULT NULL            COMMENT '操作人员名称',
    `oper_ip`         VARCHAR(50)   DEFAULT NULL            COMMENT '操作IP',
    `oper_location`   VARCHAR(255)  DEFAULT NULL            COMMENT '操作地点',
    `status`          TINYINT       NOT NULL DEFAULT 0      COMMENT '操作状态（0正常 1异常）',
    `error_msg`       TEXT          DEFAULT NULL            COMMENT '错误消息',
    `oper_time`       DATETIME      DEFAULT NULL            COMMENT '操作时间',
    `cost_time`       BIGINT        DEFAULT 0               COMMENT '耗时（毫秒）',
    `tenant_id`       BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`       BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`         TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_oper_log_business` (`business_type`),
    INDEX `idx_oper_log_status` (`status`),
    INDEX `idx_oper_log_time` (`oper_time`),
    INDEX `idx_oper_log_user` (`oper_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- 14. sys_login_log - 登录日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_login_log` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`        VARCHAR(50)   NOT NULL                COMMENT '用户账号',
    `login_ip`        VARCHAR(50)   DEFAULT NULL            COMMENT '登录IP',
    `login_location`  VARCHAR(255)  DEFAULT NULL            COMMENT '登录地点',
    `browser`         VARCHAR(50)   DEFAULT NULL            COMMENT '浏览器类型',
    `os`              VARCHAR(50)   DEFAULT NULL            COMMENT '操作系统',
    `status`          TINYINT       NOT NULL DEFAULT 0      COMMENT '登录状态（0成功 1失败）',
    `msg`             VARCHAR(255)  DEFAULT NULL            COMMENT '提示消息',
    `login_time`      DATETIME      DEFAULT NULL            COMMENT '登录时间',
    `tenant_id`       BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`       BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`         TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_login_log_status` (`status`),
    INDEX `idx_login_log_time` (`login_time`),
    INDEX `idx_login_log_ip` (`login_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ============================================================================
-- WORKFLOW TABLES (flow_)
-- ============================================================================

-- ----------------------------
-- 15. flow_category - 流程分类表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_category` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_name` VARCHAR(50)   NOT NULL                COMMENT '分类名称',
    `category_code` VARCHAR(50)   NOT NULL                COMMENT '分类编码',
    `sort`          INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `icon`          VARCHAR(100)  DEFAULT NULL            COMMENT '图标',
    `status`        TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `tenant_id`     BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`     BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`       TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_category_code` (`category_code`, `tenant_id`, `deleted`),
    INDEX `idx_category_sort` (`sort`),
    INDEX `idx_category_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程分类表';

-- ----------------------------
-- 16. flow_definition - 流程定义表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_definition` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `definition_key` VARCHAR(100)  NOT NULL                COMMENT '流程定义标识',
    `definition_name` VARCHAR(100) NOT NULL                COMMENT '流程定义名称',
    `category_id`    BIGINT        DEFAULT NULL            COMMENT '分类ID',
    `version`        INT           NOT NULL DEFAULT 1      COMMENT '版本号',
    `description`    VARCHAR(500)  DEFAULT NULL            COMMENT '描述',
    `bpmn_xml`       MEDIUMTEXT    DEFAULT NULL            COMMENT 'BPMN XML定义',
    `form_json`      JSON          DEFAULT NULL            COMMENT '表单JSON定义',
    `status`         TINYINT       NOT NULL DEFAULT 0      COMMENT '状态（0未发布 1已发布 2已停用）',
    `deploy_time`    DATETIME      DEFAULT NULL            COMMENT '发布时间',
    `tenant_id`      BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`      BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`        TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_def_key_version` (`definition_key`, `version`, `tenant_id`, `deleted`),
    INDEX `idx_def_category` (`category_id`),
    INDEX `idx_def_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程定义表';

-- ----------------------------
-- 17. flow_instance - 流程实例表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_instance` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `definition_id`  BIGINT        NOT NULL                COMMENT '流程定义ID',
    `business_key`   VARCHAR(200)  DEFAULT NULL            COMMENT '业务标识',
    `business_type`  VARCHAR(50)   DEFAULT NULL            COMMENT '业务类型',
    `title`          VARCHAR(200)  DEFAULT NULL            COMMENT '流程标题',
    `initiator_id`   BIGINT        DEFAULT NULL            COMMENT '发起人ID',
    `start_time`     DATETIME      DEFAULT NULL            COMMENT '开始时间',
    `end_time`       DATETIME      DEFAULT NULL            COMMENT '结束时间',
    `status`         TINYINT       NOT NULL DEFAULT 0      COMMENT '状态（0进行中 1已完成 2已终止 3已取消）',
    `variables`      JSON          DEFAULT NULL            COMMENT '流程变量',
    `tenant_id`      BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`      BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`        TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_instance_definition` (`definition_id`),
    INDEX `idx_instance_business` (`business_key`),
    INDEX `idx_instance_initiator` (`initiator_id`),
    INDEX `idx_instance_status` (`status`),
    INDEX `idx_instance_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例表';

-- ----------------------------
-- 18. flow_task - 流程任务表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_task` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `instance_id`     BIGINT        NOT NULL                COMMENT '流程实例ID',
    `task_name`       VARCHAR(100)  NOT NULL                COMMENT '任务名称',
    `task_key`        VARCHAR(100)  DEFAULT NULL            COMMENT '任务标识',
    `assignee_id`     BIGINT        DEFAULT NULL            COMMENT '处理人ID',
    `candidate_users` VARCHAR(500)  DEFAULT NULL            COMMENT '候选人ID（逗号分隔）',
    `candidate_groups` VARCHAR(500) DEFAULT NULL            COMMENT '候选组（逗号分隔）',
    `claim_time`      DATETIME      DEFAULT NULL            COMMENT '签收时间',
    `complete_time`   DATETIME      DEFAULT NULL            COMMENT '完成时间',
    `status`          TINYINT       NOT NULL DEFAULT 0      COMMENT '状态（0待处理 1已完成 2已终止 3已转办 4已委派）',
    `comment`         VARCHAR(500)  DEFAULT NULL            COMMENT '审批意见',
    `tenant_id`       BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`       BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`         TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_task_instance` (`instance_id`),
    INDEX `idx_task_assignee` (`assignee_id`),
    INDEX `idx_task_status` (`status`),
    INDEX `idx_task_key` (`task_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程任务表';

-- ----------------------------
-- 19. flow_task_log - 任务操作日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_task_log` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `instance_id`    BIGINT        NOT NULL                COMMENT '流程实例ID',
    `task_id`        BIGINT        DEFAULT NULL            COMMENT '任务ID',
    `operator_id`    BIGINT        DEFAULT NULL            COMMENT '操作人ID',
    `operation_type` VARCHAR(50)   NOT NULL                COMMENT '操作类型（complete/approve/reject/transfer/delegate/cancel/withdraw）',
    `comment`        VARCHAR(500)  DEFAULT NULL            COMMENT '操作意见',
    `operate_time`   DATETIME      DEFAULT NULL            COMMENT '操作时间',
    `tenant_id`      BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`      BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`        TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_task_log_instance` (`instance_id`),
    INDEX `idx_task_log_task` (`task_id`),
    INDEX `idx_task_log_operator` (`operator_id`),
    INDEX `idx_task_log_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务操作日志表';

-- ============================================================================
-- APPROVAL TABLES (approval_)
-- ============================================================================

-- ----------------------------
-- 20. approval_type - 审批类型表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `approval_type` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_name`    VARCHAR(50)   NOT NULL                COMMENT '类型名称',
    `type_code`    VARCHAR(50)   NOT NULL                COMMENT '类型编码',
    `icon`         VARCHAR(100)  DEFAULT NULL            COMMENT '图标',
    `flow_key`     VARCHAR(100)  DEFAULT NULL            COMMENT '关联流程标识',
    `sort`         INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `status`       TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `form_schema`  JSON          DEFAULT NULL            COMMENT '表单Schema',
    `description`  VARCHAR(500)  DEFAULT NULL            COMMENT '描述',
    `tenant_id`    BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`    BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`    BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`      TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_type_code` (`type_code`, `tenant_id`, `deleted`),
    INDEX `idx_approval_type_sort` (`sort`),
    INDEX `idx_approval_type_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批类型表';

-- ----------------------------
-- 21. approval_instance - 审批实例表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `approval_instance` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_id`         BIGINT        NOT NULL                COMMENT '审批类型ID',
    `title`           VARCHAR(200)  NOT NULL                COMMENT '审批标题',
    `business_id`     VARCHAR(200)  DEFAULT NULL            COMMENT '关联业务ID',
    `initiator_id`    BIGINT        NOT NULL                COMMENT '发起人ID',
    `flow_instance_id` BIGINT       DEFAULT NULL            COMMENT '流程实例ID',
    `form_data`       JSON          DEFAULT NULL            COMMENT '表单数据',
    `status`          TINYINT       NOT NULL DEFAULT 0      COMMENT '状态（0审批中 1已通过 2已驳回 3已撤回 4已取消）',
    `urgency_level`   TINYINT       NOT NULL DEFAULT 0      COMMENT '紧急程度（0普通 1紧急 2加急）',
    `submit_time`     DATETIME      DEFAULT NULL            COMMENT '提交时间',
    `complete_time`   DATETIME      DEFAULT NULL            COMMENT '完成时间',
    `tenant_id`       BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`       BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`         TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_approval_ins_type` (`type_id`),
    INDEX `idx_approval_ins_initiator` (`initiator_id`),
    INDEX `idx_approval_ins_flow` (`flow_instance_id`),
    INDEX `idx_approval_ins_status` (`status`),
    INDEX `idx_approval_ins_submit_time` (`submit_time`),
    INDEX `idx_approval_ins_business` (`business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批实例表';

-- ============================================================================
-- MESSAGE TABLES (msg_)
-- ============================================================================

-- ----------------------------
-- 22. msg_template - 消息模板表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `msg_template` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_name`    VARCHAR(100)  NOT NULL                COMMENT '模板名称',
    `template_code`    VARCHAR(100)  NOT NULL                COMMENT '模板编码',
    `template_type`    TINYINT       NOT NULL DEFAULT 1      COMMENT '模板类型（1站内信 2邮件 3短信 4微信）',
    `title_template`   VARCHAR(200)  DEFAULT NULL            COMMENT '标题模板',
    `content_template` TEXT          NOT NULL                COMMENT '内容模板',
    `status`           TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `params_desc`      VARCHAR(500)  DEFAULT NULL            COMMENT '参数说明',
    `tenant_id`        BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`        BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`        BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`          TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_template_code` (`template_code`, `tenant_id`, `deleted`),
    INDEX `idx_template_type` (`template_type`),
    INDEX `idx_template_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息模板表';

-- ----------------------------
-- 23. msg_notification - 站内通知表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `msg_notification` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT        NOT NULL                COMMENT '接收用户ID',
    `title`         VARCHAR(200)  NOT NULL                COMMENT '通知标题',
    `content`       TEXT          DEFAULT NULL            COMMENT '通知内容',
    `msg_type`      TINYINT       NOT NULL DEFAULT 1      COMMENT '消息类型（1通知 2公告 3待办）',
    `read_status`   TINYINT       NOT NULL DEFAULT 0      COMMENT '阅读状态（0未读 1已读）',
    `read_time`     DATETIME      DEFAULT NULL            COMMENT '阅读时间',
    `business_type` VARCHAR(50)   DEFAULT NULL            COMMENT '业务类型',
    `business_id`   VARCHAR(200)  DEFAULT NULL            COMMENT '业务ID',
    `tenant_id`     BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`     BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`       TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_notification_user` (`user_id`),
    INDEX `idx_notification_read` (`read_status`),
    INDEX `idx_notification_type` (`msg_type`),
    INDEX `idx_notification_business` (`business_type`, `business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内通知表';

-- ----------------------------
-- 24. msg_record - 外部消息记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `msg_record` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_code` VARCHAR(100)  DEFAULT NULL            COMMENT '模板编码',
    `receiver`      VARCHAR(200)  NOT NULL                COMMENT '接收方（邮箱/手机号/openId）',
    `receiver_type` TINYINT       NOT NULL DEFAULT 1      COMMENT '接收方类型（1用户 2邮箱 3手机）',
    `channel`       TINYINT       NOT NULL DEFAULT 1      COMMENT '发送渠道（1站内信 2邮件 3短信 4微信）',
    `content`       TEXT          NOT NULL                COMMENT '发送内容',
    `send_status`   TINYINT       NOT NULL DEFAULT 0      COMMENT '发送状态（0待发送 1发送成功 2发送失败）',
    `send_time`     DATETIME      DEFAULT NULL            COMMENT '发送时间',
    `error_msg`     VARCHAR(500)  DEFAULT NULL            COMMENT '错误信息',
    `retry_count`   INT           NOT NULL DEFAULT 0      COMMENT '重试次数',
    `tenant_id`     BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`     BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`     BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`       TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_record_status` (`send_status`),
    INDEX `idx_record_channel` (`channel`),
    INDEX `idx_record_receiver` (`receiver`),
    INDEX `idx_record_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='外部消息记录表';

-- ============================================================================
-- FILE TABLES (file_)
-- ============================================================================

-- ----------------------------
-- 25. file_info - 文件信息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `file_info` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name`       VARCHAR(200)  NOT NULL                COMMENT '存储文件名',
    `original_name`   VARCHAR(200)  NOT NULL                COMMENT '原始文件名',
    `file_path`       VARCHAR(500)  NOT NULL                COMMENT '文件路径',
    `file_url`        VARCHAR(500)  DEFAULT NULL            COMMENT '文件访问URL',
    `file_size`       BIGINT        NOT NULL DEFAULT 0      COMMENT '文件大小（字节）',
    `file_type`       VARCHAR(50)   DEFAULT NULL            COMMENT '文件MIME类型',
    `file_extension`  VARCHAR(20)   DEFAULT NULL            COMMENT '文件扩展名',
    `storage_type`    TINYINT       NOT NULL DEFAULT 1      COMMENT '存储类型（1本地 2MinIO 3OSS 4COS 5S3）',
    `md5_hash`        VARCHAR(64)   DEFAULT NULL            COMMENT '文件MD5',
    `upload_user_id`  BIGINT        DEFAULT NULL            COMMENT '上传用户ID',
    `download_count`  INT           NOT NULL DEFAULT 0      COMMENT '下载次数',
    `status`          TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0禁用 1正常）',
    `thumbnail_url`   VARCHAR(500)  DEFAULT NULL            COMMENT '缩略图URL',
    `tenant_id`       BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`       BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`         TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_file_md5` (`md5_hash`),
    INDEX `idx_file_user` (`upload_user_id`),
    INDEX `idx_file_type` (`file_type`),
    INDEX `idx_file_extension` (`file_extension`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- ============================================================================
-- AI TABLES (ai_)
-- ============================================================================

-- ----------------------------
-- 26. ai_conversation - AI对话表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ai_conversation` (
    `id`                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`            BIGINT        NOT NULL                COMMENT '用户ID',
    `conversation_title` VARCHAR(200)  DEFAULT NULL            COMMENT '对话标题',
    `conversation_type`  TINYINT       NOT NULL DEFAULT 1      COMMENT '对话类型（1通用对话 2文档分析 3代码助手 4知识问答）',
    `context_messages`   JSON          DEFAULT NULL            COMMENT '上下文消息',
    `status`             TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0已归档 1进行中）',
    `last_message_time`  DATETIME      DEFAULT NULL            COMMENT '最后消息时间',
    `tenant_id`          BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`          BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`          BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`            TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    INDEX `idx_ai_conv_user` (`user_id`),
    INDEX `idx_ai_conv_status` (`status`),
    INDEX `idx_ai_conv_type` (`conversation_type`),
    INDEX `idx_ai_conv_last_msg` (`last_message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话表';

-- ----------------------------
-- 27. ai_prompt_template - AI提示词模板表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ai_prompt_template` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `template_name`  VARCHAR(100)  NOT NULL                COMMENT '模板名称',
    `template_code`  VARCHAR(100)  NOT NULL                COMMENT '模板编码',
    `template_type`  TINYINT       NOT NULL DEFAULT 1      COMMENT '模板类型（1系统预设 2用户自定义）',
    `prompt_content` TEXT          NOT NULL                COMMENT '提示词内容',
    `variables`      JSON          DEFAULT NULL            COMMENT '变量定义',
    `status`         TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `usage_count`    INT           NOT NULL DEFAULT 0      COMMENT '使用次数',
    `tenant_id`      BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`      BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`      BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`        TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_prompt_code` (`template_code`, `tenant_id`, `deleted`),
    INDEX `idx_prompt_type` (`template_type`),
    INDEX `idx_prompt_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI提示词模板表';

-- ============================================================================
-- REPORT TABLES (rpt_)
-- ============================================================================

-- ----------------------------
-- 28. rpt_report_config - 报表配置表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `rpt_report_config` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `report_name`  VARCHAR(100)  NOT NULL                COMMENT '报表名称',
    `report_code`  VARCHAR(100)  NOT NULL                COMMENT '报表编码',
    `report_type`  TINYINT       NOT NULL DEFAULT 1      COMMENT '报表类型（1图表 2表格 3大屏）',
    `data_source`  VARCHAR(200)  DEFAULT NULL            COMMENT '数据源',
    `chart_type`   VARCHAR(50)   DEFAULT NULL            COMMENT '图表类型（bar/line/pie/table/gauge/map）',
    `config_json`  JSON          DEFAULT NULL            COMMENT '配置JSON',
    `status`       TINYINT       NOT NULL DEFAULT 1      COMMENT '状态（0停用 1正常）',
    `sort`         INT           NOT NULL DEFAULT 0      COMMENT '显示顺序',
    `tenant_id`    BIGINT        NOT NULL DEFAULT 1      COMMENT '租户ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`    BIGINT        DEFAULT NULL            COMMENT '创建者',
    `update_by`    BIGINT        DEFAULT NULL            COMMENT '更新者',
    `deleted`      TINYINT       NOT NULL DEFAULT 0      COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_report_code` (`report_code`, `tenant_id`, `deleted`),
    INDEX `idx_report_type` (`report_type`),
    INDEX `idx_report_status` (`status`),
    INDEX `idx_report_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表配置表';


-- ============================================================================
-- SEED DATA
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 1. Default Tenant
-- ----------------------------
INSERT INTO `sys_tenant` (`id`, `tenant_name`, `contact_name`, `contact_phone`, `contact_email`, `status`, `account_limit`, `remark`)
VALUES (1, '默认租户', '系统管理员', '13800000000', 'admin@flowx.com', 1, 0, '系统初始化默认租户')
ON DUPLICATE KEY UPDATE `tenant_name` = VALUES(`tenant_name`);

-- ----------------------------
-- 2. Default Tenant Package
-- ----------------------------
INSERT INTO `sys_tenant_package` (`id`, `package_name`, `menu_ids`, `status`, `remark`)
VALUES (1, '企业版', '[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75]', 1, '企业版套餐，包含全部功能')
ON DUPLICATE KEY UPDATE `package_name` = VALUES(`package_name`);

-- ----------------------------
-- 3. Default Departments
-- ----------------------------
INSERT INTO `sys_dept` (`id`, `dept_name`, `parent_id`, `sort`, `leader`, `status`, `order_num`) VALUES
(1, '总公司',    0, 0, '管理员', 1, 0),
(2, '技术部',    1, 1, NULL,    1, 1),
(3, '产品部',    1, 2, NULL,    1, 2),
(4, '市场部',    1, 3, NULL,    1, 3),
(5, '财务部',    1, 4, NULL,    1, 4),
(6, '人力资源部', 1, 5, NULL,    1, 5)
ON DUPLICATE KEY UPDATE `dept_name` = VALUES(`dept_name`);

-- ----------------------------
-- 4. Default Positions
-- ----------------------------
INSERT INTO `sys_position` (`id`, `position_name`, `position_code`, `sort`, `status`, `remark`) VALUES
(1, 'CEO',       'ceo',       1, 1, '首席执行官'),
(2, '部门经理',  'manager',   2, 1, '部门经理'),
(3, '普通员工',  'employee',  3, 1, '普通员工')
ON DUPLICATE KEY UPDATE `position_name` = VALUES(`position_name`);

-- ----------------------------
-- 5. Default Roles
-- ----------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `remark`) VALUES
(1, '超级管理员', 'super_admin',  1, 1, 1, '超级管理员角色，拥有所有权限'),
(2, '租户管理员', 'tenant_admin', 2, 2, 1, '租户管理员角色'),
(3, '普通用户',   'user',         3, 5, 1, '普通用户角色')
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

-- ----------------------------
-- 6. Super Admin User (password: admin123 BCrypt)
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `gender`, `dept_id`, `position_id`, `status`, `remark`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@flowx.com', '13800000000', 1, 1, 1, 1, '系统超级管理员')
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

-- ----------------------------
-- 7. Admin User-Role Mapping
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1)
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- ----------------------------
-- 8. Default Menus - System Management Tree
-- ----------------------------
-- Level 1: System Management
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(1, '系统管理', 0, 1, 'system', NULL, 'M', NULL, 'setting', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: System Management Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(2,  '用户管理', 1, 1,  'user',     'system/user/index',     'C', 'system:user:list',     'user',        1, 1),
(3,  '角色管理', 1, 2,  'role',     'system/role/index',     'C', 'system:role:list',     'peoples',     1, 1),
(4,  '菜单管理', 1, 3,  'menu',     'system/menu/index',     'C', 'system:menu:list',     'tree-table',  1, 1),
(5,  '部门管理', 1, 4,  'dept',     'system/dept/index',     'C', 'system:dept:list',     'tree',        1, 1),
(6,  '岗位管理', 1, 5,  'position', 'system/position/index', 'C', 'system:position:list', 'post',        1, 1),
(7,  '字典管理', 1, 6,  'dict',     'system/dict/index',     'C', 'system:dict:list',     'dict',        1, 1),
(8,  '系统配置', 1, 7,  'config',   'system/config/index',   'C', 'system:config:list',   'edit',        1, 1),
(9,  '操作日志', 1, 8,  'operlog',  'system/operlog/index',  'C', 'system:operlog:list',  'form',        1, 1),
(10, '登录日志', 1, 9,  'loginlog', 'system/loginlog/index', 'C', 'system:loginlog:list', 'logininfor',  1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: User Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(11, '用户查询',  2, 1, '', NULL, 'F', 'system:user:query',  '#', 1, 1),
(12, '用户新增',  2, 2, '', NULL, 'F', 'system:user:add',    '#', 1, 1),
(13, '用户修改',  2, 3, '', NULL, 'F', 'system:user:edit',   '#', 1, 1),
(14, '用户删除',  2, 4, '', NULL, 'F', 'system:user:remove', '#', 1, 1),
(15, '重置密码',  2, 5, '', NULL, 'F', 'system:user:resetPwd', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Role Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(16, '角色查询',  3, 1, '', NULL, 'F', 'system:role:query',  '#', 1, 1),
(17, '角色新增',  3, 2, '', NULL, 'F', 'system:role:add',    '#', 1, 1),
(18, '角色修改',  3, 3, '', NULL, 'F', 'system:role:edit',   '#', 1, 1),
(19, '角色删除',  3, 4, '', NULL, 'F', 'system:role:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Menu Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(20, '菜单查询',  4, 1, '', NULL, 'F', 'system:menu:query',  '#', 1, 1),
(21, '菜单新增',  4, 2, '', NULL, 'F', 'system:menu:add',    '#', 1, 1),
(22, '菜单修改',  4, 3, '', NULL, 'F', 'system:menu:edit',   '#', 1, 1),
(23, '菜单删除',  4, 4, '', NULL, 'F', 'system:menu:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Dept Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(24, '部门查询',  5, 1, '', NULL, 'F', 'system:dept:query',  '#', 1, 1),
(25, '部门新增',  5, 2, '', NULL, 'F', 'system:dept:add',    '#', 1, 1),
(26, '部门修改',  5, 3, '', NULL, 'F', 'system:dept:edit',   '#', 1, 1),
(27, '部门删除',  5, 4, '', NULL, 'F', 'system:dept:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Position Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(28, '岗位查询',  6, 1, '', NULL, 'F', 'system:position:query',  '#', 1, 1),
(29, '岗位新增',  6, 2, '', NULL, 'F', 'system:position:add',    '#', 1, 1),
(30, '岗位修改',  6, 3, '', NULL, 'F', 'system:position:edit',   '#', 1, 1),
(31, '岗位删除',  6, 4, '', NULL, 'F', 'system:position:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Dict Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(32, '字典查询',  7, 1, '', NULL, 'F', 'system:dict:query',  '#', 1, 1),
(33, '字典新增',  7, 2, '', NULL, 'F', 'system:dict:add',    '#', 1, 1),
(34, '字典修改',  7, 3, '', NULL, 'F', 'system:dict:edit',   '#', 1, 1),
(35, '字典删除',  7, 4, '', NULL, 'F', 'system:dict:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Config Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(36, '配置查询',  8, 1, '', NULL, 'F', 'system:config:query',  '#', 1, 1),
(37, '配置新增',  8, 2, '', NULL, 'F', 'system:config:add',    '#', 1, 1),
(38, '配置修改',  8, 3, '', NULL, 'F', 'system:config:edit',   '#', 1, 1),
(39, '配置删除',  8, 4, '', NULL, 'F', 'system:config:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: Tenant Management
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(40, '租户管理', 0, 2, 'tenant', NULL, 'M', NULL, 'guide', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: Tenant Management Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(41, '租户列表',   40, 1, 'list',    'tenant/list/index',    'C', 'tenant:list',    'list',    1, 1),
(42, '套餐管理',   40, 2, 'package', 'tenant/package/index', 'C', 'tenant:package', 'component', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 3: Tenant Management Buttons
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(43, '租户查询',  41, 1, '', NULL, 'F', 'tenant:list:query',  '#', 1, 1),
(44, '租户新增',  41, 2, '', NULL, 'F', 'tenant:list:add',    '#', 1, 1),
(45, '租户修改',  41, 3, '', NULL, 'F', 'tenant:list:edit',   '#', 1, 1),
(46, '租户删除',  41, 4, '', NULL, 'F', 'tenant:list:remove', '#', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: Approval Management
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(47, '审批管理', 0, 3, 'approval', NULL, 'M', NULL, 'edit', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: Approval Management Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(48, '审批类型',   47, 1, 'type',     'approval/type/index',     'C', 'approval:type:list',     'form',       1, 1),
(49, '发起审批',   47, 2, 'launch',   'approval/launch/index',   'C', 'approval:launch',        'guide',      1, 1),
(50, '我的审批',   47, 3, 'my',       'approval/my/index',       'C', 'approval:my:list',       'clipboard',  1, 1),
(51, '审批记录',   47, 4, 'history',  'approval/history/index',  'C', 'approval:history:list',  'documentation', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: Workflow Management
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(52, '流程管理', 0, 4, 'flow', NULL, 'M', NULL, 'skill', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: Workflow Management Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(53, '流程分类',   52, 1, 'category',   'flow/category/index',   'C', 'flow:category:list',   'tree',         1, 1),
(54, '流程设计',   52, 2, 'design',     'flow/design/index',     'C', 'flow:design:list',     'design',       1, 1),
(55, '流程实例',   52, 3, 'instance',   'flow/instance/index',   'C', 'flow:instance:list',   'monitor',      1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: Message Management
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(56, '消息管理', 0, 5, 'message', NULL, 'M', NULL, 'message', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: Message Management Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(57, '消息模板', 56, 1, 'template',   'message/template/index',   'C', 'message:template:list',   'documentation', 1, 1),
(58, '通知公告', 56, 2, 'notice',     'message/notice/index',     'C', 'message:notice:list',     'education',     1, 1),
(59, '消息记录', 56, 3, 'record',     'message/record/index',     'C', 'message:record:list',     'log',           1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: File Management
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(60, '文件管理', 0, 6, 'file', NULL, 'M', NULL, 'upload', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: File Management Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(61, '文件列表', 60, 1, 'list', 'file/list/index', 'C', 'file:list:list', 'list', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: AI Assistant
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(62, 'AI助手', 0, 7, 'ai', NULL, 'M', NULL, 'robot', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: AI Assistant Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(63, '智能对话',     62, 1, 'chat',      'ai/chat/index',      'C', 'ai:chat:list',      'peoples',       1, 1),
(64, '提示词模板',   62, 2, 'template',  'ai/template/index',  'C', 'ai:template:list',  'documentation', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 1: Report Center
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(65, '报表中心', 0, 8, 'report', NULL, 'M', NULL, 'chart', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- Level 2: Report Center Sub-menus
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `permission`, `icon`, `visible`, `status`) VALUES
(66, '报表配置', 65, 1, 'config', 'report/config/index', 'C', 'report:config:list', 'edit', 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);

-- ----------------------------
-- 9. Super Admin has all menus
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1),  (1, 2),  (1, 3),  (1, 4),  (1, 5),  (1, 6),  (1, 7),  (1, 8),  (1, 9),  (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30),
(1, 31), (1, 32), (1, 33), (1, 34), (1, 35), (1, 36), (1, 37), (1, 38), (1, 39), (1, 40),
(1, 41), (1, 42), (1, 43), (1, 44), (1, 45), (1, 46), (1, 47), (1, 48), (1, 49), (1, 50),
(1, 51), (1, 52), (1, 53), (1, 54), (1, 55), (1, 56), (1, 57), (1, 58), (1, 59), (1, 60),
(1, 61), (1, 62), (1, 63), (1, 64), (1, 65), (1, 66)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- ----------------------------
-- 10. Default Dict Types
-- ----------------------------
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `remark`) VALUES
(1,  '用户性别',     'sys_user_gender',    1, '用户性别列表'),
(2,  '系统开关',     'sys_normal_disable', 1, '系统开关列表'),
(3,  '审批状态',     'approval_status',    1, '审批状态列表'),
(4,  '数据范围',     'sys_data_scope',     1, '数据范围列表'),
(5,  '操作类型',     'sys_oper_type',      1, '操作类型列表'),
(6,  '系统状态',     'sys_common_status',  1, '登录状态列表'),
(7,  '文件类型',     'file_type',          1, '文件类型列表'),
(8,  '消息类型',     'msg_type',           1, '消息类型列表'),
(9,  '流程状态',     'flow_status',        1, '流程状态列表'),
(10, '通知状态',     'notification_read',  1, '通知阅读状态')
ON DUPLICATE KEY UPDATE `dict_name` = VALUES(`dict_name`);

-- ----------------------------
-- 11. Default Dict Data
-- ----------------------------
-- sys_user_gender
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('sys_user_gender', '未知', '0', 1, 1),
('sys_user_gender', '男',   '1', 2, 1),
('sys_user_gender', '女',   '2', 3, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- sys_normal_disable
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('sys_normal_disable', '停用', '0', 1, 1),
('sys_normal_disable', '正常', '1', 2, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- approval_status
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('approval_status', '审批中', '0', 1, 1),
('approval_status', '已通过', '1', 2, 1),
('approval_status', '已驳回', '2', 3, 1),
('approval_status', '已撤回', '3', 4, 1),
('approval_status', '已取消', '4', 5, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- sys_data_scope
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('sys_data_scope', '全部数据',       '1', 1, 1),
('sys_data_scope', '自定义数据',     '2', 2, 1),
('sys_data_scope', '本部门数据',     '3', 3, 1),
('sys_data_scope', '本部门及以下',   '4', 4, 1),
('sys_data_scope', '仅本人数据',     '5', 5, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- sys_oper_type
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('sys_oper_type', '其他', '0', 1, 1),
('sys_oper_type', '新增', '1', 2, 1),
('sys_oper_type', '修改', '2', 3, 1),
('sys_oper_type', '删除', '3', 4, 1),
('sys_oper_type', '授权', '4', 5, 1),
('sys_oper_type', '导出', '5', 6, 1),
('sys_oper_type', '导入', '6', 7, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- sys_common_status
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('sys_common_status', '成功', '0', 1, 1),
('sys_common_status', '失败', '1', 2, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- flow_status
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('flow_status', '进行中', '0', 1, 1),
('flow_status', '已完成', '1', 2, 1),
('flow_status', '已终止', '2', 3, 1),
('flow_status', '已取消', '3', 4, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- notification_read
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('notification_read', '未读', '0', 1, 1),
('notification_read', '已读', '1', 2, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- msg_type
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort`, `status`) VALUES
('msg_type', '站内信', '1', 1, 1),
('msg_type', '邮件',   '2', 2, 1),
('msg_type', '短信',   '3', 3, 1),
('msg_type', '微信',   '4', 4, 1)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`);

-- ----------------------------
-- 12. Default Flow Categories
-- ----------------------------
INSERT INTO `flow_category` (`id`, `category_name`, `category_code`, `sort`, `icon`, `status`) VALUES
(1, '日常办公', 'daily',      1, 'calendar',   1),
(2, '财务管理', 'finance',    2, 'money',      1),
(3, '采购管理', 'procurement',3, 'shopping',   1),
(4, '合同管理', 'contract',   4, 'documentation', 1)
ON DUPLICATE KEY UPDATE `category_name` = VALUES(`category_name`);

-- ----------------------------
-- 13. Default Approval Types
-- ----------------------------
INSERT INTO `approval_type` (`id`, `type_name`, `type_code`, `icon`, `flow_key`, `sort`, `status`, `description`) VALUES
(1, '请假申请',     'leave',         'date',      'leave_flow',         1, 1, '员工请假申请审批'),
(2, '报销申请',     'reimbursement', 'money',     'reimbursement_flow', 2, 1, '费用报销审批'),
(3, '采购申请',     'procurement',   'shopping',  'procurement_flow',   3, 1, '物资采购审批'),
(4, '合同审批',     'contract',      'documentation', 'contract_flow', 4, 1, '合同签订审批'),
(5, '通用审批',     'general',       'edit',      'general_flow',       5, 1, '通用审批流程')
ON DUPLICATE KEY UPDATE `type_name` = VALUES(`type_name`);

-- ----------------------------
-- 14. Default System Configs
-- ----------------------------
INSERT INTO `sys_config` (`id`, `config_name`, `config_key`, `config_value`, `config_type`, `remark`) VALUES
(1, '主框架页-默认皮肤',   'sys.index.skinName',    'skin-blue',     'Y', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2, '用户管理-账号初始密码', 'sys.user.initPassword',  'admin123',     'Y', '初始化密码 123456'),
(3, '主框架页-侧边栏主题',  'sys.index.sideTheme',    'theme-dark',    'Y', '深色主题theme-dark，浅色主题theme-light'),
(4, '账号自助-验证码开关',   'sys.account.captchaEnabled', 'true',     'Y', '是否开启验证码功能（true开启，false关闭）'),
(5, '用户登录-最大错误次数', 'sys.login.maxRetry',     '5',            'Y', '允许的最大登录错误次数'),
(6, '用户登录-锁定时间',    'sys.login.lockTime',     '10',           'Y', '登录错误锁定时间（分钟）')
ON DUPLICATE KEY UPDATE `config_name` = VALUES(`config_name`);

-- ----------------------------
-- 15. Default AI Prompt Templates
-- ----------------------------
INSERT INTO `ai_prompt_template` (`id`, `template_name`, `template_code`, `template_type`, `prompt_content`, `variables`, `status`, `usage_count`) VALUES
(1, '通用助手',   'general_assistant',  1, '你是一个专业的AI助手，请根据用户的问题提供准确、有帮助的回答。', NULL, 1, 0),
(2, '代码助手',   'code_assistant',     1, '你是一个专业的编程助手，擅长各种编程语言和框架。请帮助用户解决代码问题，提供最佳实践建议。', NULL, 1, 0),
(3, '文档分析',   'doc_analyzer',       1, '你是一个文档分析专家，请帮助用户分析和总结文档内容，提取关键信息。', '{"document": "待分析的文档内容"}', 1, 0),
(4, '翻译助手',   'translator',         1, '你是一个专业的翻译助手，精通多种语言。请将用户提供的文本翻译成目标语言，保持原意和风格。', '{"source_lang": "源语言", "target_lang": "目标语言", "text": "待翻译文本"}', 1, 0)
ON DUPLICATE KEY UPDATE `template_name` = VALUES(`template_name`);

-- ----------------------------
-- 16. Default Message Templates
-- ----------------------------
INSERT INTO `msg_template` (`id`, `template_name`, `template_code`, `template_type`, `title_template`, `content_template`, `status`, `params_desc`) VALUES
(1, '审批通知',   'approval_notify',    1, '您有一条新的审批待处理', '您收到一条来自 ${initiator} 的 ${approvalType} 审批申请，标题：${title}，请及时处理。', 1, 'initiator:发起人, approvalType:审批类型, title:标题'),
(2, '审批结果通知', 'approval_result',  1, '您的审批申请已处理',     '您的 ${approvalType} 审批申请（${title}）已${result}，处理人：${handler}。', 1, 'approvalType:审批类型, title:标题, result:结果, handler:处理人'),
(3, '系统公告',   'system_notice',      1, '${title}',             '${content}', 1, 'title:公告标题, content:公告内容')
ON DUPLICATE KEY UPDATE `template_name` = VALUES(`template_name`);

-- ----------------------------
-- 17. Assign tenant package to tenant
-- ----------------------------
UPDATE `sys_tenant` SET `package_id` = 1 WHERE `id` = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- END OF SCRIPT
-- ============================================================================
