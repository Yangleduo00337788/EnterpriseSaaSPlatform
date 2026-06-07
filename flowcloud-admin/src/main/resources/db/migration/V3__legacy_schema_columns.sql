-- Make legacy column backfill idempotent so Flyway can run against
-- databases initialized manually by sql/schema.sql as well as older schemas.
SET @dbname = DATABASE();

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_tenant'
              AND COLUMN_NAME = 'expire_time'
        ),
        'SELECT 1',
        'ALTER TABLE sys_tenant ADD COLUMN expire_time DATETIME NULL COMMENT ''expire'''
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_tenant'
              AND COLUMN_NAME = 'package_config'
        ),
        'SELECT 1',
        'ALTER TABLE sys_tenant ADD COLUMN package_config TEXT NULL'
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_tenant'
              AND COLUMN_NAME = 'feature_config'
        ),
        'SELECT 1',
        'ALTER TABLE sys_tenant ADD COLUMN feature_config TEXT NULL'
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_dept'
              AND COLUMN_NAME = 'leader_user_id'
        ),
        'SELECT 1',
        'ALTER TABLE sys_dept ADD COLUMN leader_user_id BIGINT NULL'
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_dept'
              AND COLUMN_NAME = 'ancestors'
        ),
        'SELECT 1',
        'ALTER TABLE sys_dept ADD COLUMN ancestors VARCHAR(512) DEFAULT ''0'''
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_user'
              AND COLUMN_NAME = 'manager_id'
        ),
        'SELECT 1',
        'ALTER TABLE sys_user ADD COLUMN manager_id BIGINT NULL'
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_user'
              AND COLUMN_NAME = 'job_title'
        ),
        'SELECT 1',
        'ALTER TABLE sys_user ADD COLUMN job_title VARCHAR(64) NULL'
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_user'
              AND COLUMN_NAME = 'work_status'
        ),
        'SELECT 1',
        'ALTER TABLE sys_user ADD COLUMN work_status VARCHAR(32) DEFAULT ''active'''
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'sys_role'
              AND COLUMN_NAME = 'data_scope'
        ),
        'SELECT 1',
        'ALTER TABLE sys_role ADD COLUMN data_scope VARCHAR(32) DEFAULT ''SELF'''
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @preparedStatement = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'approval_instance'
              AND COLUMN_NAME = 'flow_config_snapshot'
        ),
        'SELECT 1',
        'ALTER TABLE approval_instance ADD COLUMN flow_config_snapshot TEXT NULL'
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE sys_tenant SET feature_config = '{"approval":true,"report":true,"message":true,"tenantSettings":true}'
WHERE feature_config IS NULL OR feature_config = '';
