SET @dbname = DATABASE();
SET @preparedStatement = (
    SELECT IF(
        (
            SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @dbname
              AND TABLE_NAME = 'approval_template'
              AND COLUMN_NAME = 'pub_version'
        ) > 0,
        'SELECT 1',
        'ALTER TABLE approval_template ADD COLUMN pub_version INT DEFAULT 0 COMMENT ''published version'''
    )
);
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE approval_template SET pub_version = 1 WHERE status = 1 AND (pub_version IS NULL OR pub_version = 0);
