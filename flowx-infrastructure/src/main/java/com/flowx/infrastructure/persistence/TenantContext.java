package com.flowx.infrastructure.persistence;

/**
 * ThreadLocal holder for tenant ID.
 * Used for multi-tenant data isolation in request processing.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private TenantContext() {
        // utility class
    }

    /**
     * Set the current tenant ID for this thread.
     *
     * @param tenantId the tenant ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * Get the current tenant ID for this thread.
     *
     * @return the tenant ID, or null if not set
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * Clear the tenant context for this thread.
     * Must be called in a finally block to prevent memory leaks.
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
