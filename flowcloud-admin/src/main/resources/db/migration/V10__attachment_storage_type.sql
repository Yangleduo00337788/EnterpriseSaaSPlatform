SET @storage_type_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attachment_file'
      AND COLUMN_NAME = 'storage_type'
);

SET @attachment_storage_type_ddl = IF(
    @storage_type_column_exists = 0,
    'ALTER TABLE attachment_file ADD COLUMN storage_type VARCHAR(16) NULL COMMENT ''存储类型''',
    'SELECT 1'
);

PREPARE attachment_storage_type_stmt FROM @attachment_storage_type_ddl;
EXECUTE attachment_storage_type_stmt;
DEALLOCATE PREPARE attachment_storage_type_stmt;

UPDATE attachment_file
SET storage_type = CASE
    WHEN file_url LIKE 'http://localhost:9000/%'
        OR file_url LIKE 'https://localhost:9000/%'
        OR file_url LIKE '%/minio/%'
        OR file_url LIKE '%/flowcloud/%'
        THEN 'MINIO'
    ELSE 'LOCAL'
END
WHERE storage_type IS NULL OR storage_type = '';
