DELETE current_data
FROM sys_dict_data current_data
INNER JOIN sys_dict_data duplicated_data
    ON current_data.tenant_id = duplicated_data.tenant_id
    AND current_data.dict_type_id = duplicated_data.dict_type_id
    AND current_data.dict_value = duplicated_data.dict_value
    AND current_data.deleted = duplicated_data.deleted
    AND current_data.id > duplicated_data.id;

ALTER TABLE sys_dict_data
    ADD UNIQUE KEY uk_tenant_dict_type_value_deleted (tenant_id, dict_type_id, dict_value, deleted);
