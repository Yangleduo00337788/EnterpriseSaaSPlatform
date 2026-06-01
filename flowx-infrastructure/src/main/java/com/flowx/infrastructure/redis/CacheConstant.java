package com.flowx.infrastructure.redis;

/**
 * Cache key constants for consistent cache key generation.
 */
public final class CacheConstant {

    private CacheConstant() {
        // utility class
    }

    /** Cache key prefix for user info */
    public static final String CACHE_USER = "flowx:user:";

    /** Cache key prefix for role permissions */
    public static final String CACHE_ROLE = "flowx:role:";

    /** Cache key prefix for tenant info */
    public static final String CACHE_TENANT = "flowx:tenant:";

    /** Cache key prefix for dictionary data */
    public static final String CACHE_DICT = "flowx:dict:";

    /** Cache key prefix for user permissions */
    public static final String CACHE_PERMISSION = "flowx:permission:";

    /** Cache key prefix for user roles */
    public static final String CACHE_USER_ROLES = "flowx:user:roles:";

    /** Null value placeholder to prevent cache penetration */
    public static final String NULL_PLACEHOLDER = "NULL";

    /** Default TTL for null value protection (5 minutes) */
    public static final long NULL_VALUE_TTL_MINUTES = 5;

    /** Default TTL for user info cache (30 minutes) */
    public static final long USER_CACHE_TTL_MINUTES = 30;

    /** Default TTL for role permissions cache (30 minutes) */
    public static final long ROLE_CACHE_TTL_MINUTES = 30;

    /** Default TTL for tenant info cache (60 minutes) */
    public static final long TENANT_CACHE_TTL_MINUTES = 60;

    /** Default TTL for dictionary data cache (60 minutes) */
    public static final long DICT_CACHE_TTL_MINUTES = 60;
}
