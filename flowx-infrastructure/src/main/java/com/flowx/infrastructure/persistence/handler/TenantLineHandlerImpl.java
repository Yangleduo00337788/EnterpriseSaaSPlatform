package com.flowx.infrastructure.persistence.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.flowx.infrastructure.persistence.TenantContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of TenantLineHandler for MyBatis-Flex multi-tenant support.
 * Automatically adds tenant_id condition to SQL queries.
 */
public class TenantLineHandlerImpl implements TenantLineHandler {

    /**
     * Tables that should be excluded from tenant filtering.
     * These tables do not have a tenant_id column.
     */
    private static final List<String> IGNORE_TABLES = Arrays.asList(
            "sys_tenant",
            "sys_tenant_package",
            "sys_config",
            "sys_dict_type",
            "sys_dict_data",
            "flowx_workflow_definition",
            "flowx_workflow_node"
    );

    @Override
    public Expression getTenantId() {
        Long tenantId = TenantContext.getTenantId();
        return new LongValue(tenantId != null ? tenantId : 0L);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return IGNORE_TABLES.contains(tableName.toLowerCase());
    }
}
