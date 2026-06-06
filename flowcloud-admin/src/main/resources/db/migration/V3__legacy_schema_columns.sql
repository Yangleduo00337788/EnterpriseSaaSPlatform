-- MySQL 8.0 compatible (no IF NOT EXISTS on ADD COLUMN)
ALTER TABLE sys_tenant ADD COLUMN expire_time DATETIME NULL COMMENT 'expire';
ALTER TABLE sys_tenant ADD COLUMN package_config TEXT NULL;
ALTER TABLE sys_tenant ADD COLUMN feature_config TEXT NULL;
ALTER TABLE sys_dept ADD COLUMN leader_user_id BIGINT NULL;
ALTER TABLE sys_dept ADD COLUMN ancestors VARCHAR(512) DEFAULT '0';
ALTER TABLE sys_user ADD COLUMN manager_id BIGINT NULL;
ALTER TABLE sys_user ADD COLUMN job_title VARCHAR(64) NULL;
ALTER TABLE sys_user ADD COLUMN work_status VARCHAR(32) DEFAULT 'active';
ALTER TABLE sys_role ADD COLUMN data_scope VARCHAR(32) DEFAULT 'SELF';
ALTER TABLE approval_instance ADD COLUMN flow_config_snapshot TEXT NULL;

UPDATE sys_tenant SET feature_config = '{"approval":true,"report":true,"message":true,"tenantSettings":true}'
WHERE feature_config IS NULL OR feature_config = '';
