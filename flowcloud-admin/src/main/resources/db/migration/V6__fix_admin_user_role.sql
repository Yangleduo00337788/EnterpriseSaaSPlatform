INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.tenant_id = u.tenant_id AND r.role_code = 'admin'
WHERE u.is_admin = 1 AND u.deleted = 0 AND r.deleted = 0;
