package com.flowx.common.core.constant;

/**
 * Security constant definitions
 *
 * @author FlowX
 * @since 1.0.0
 */
public final class SecurityConstant {

    private SecurityConstant() {
        throw new IllegalStateException("Constant class");
    }

    /**
     * Login URL
     */
    public static final String LOGIN_URL = "/api/auth/login";

    /**
     * Register URL
     */
    public static final String REGISTER_URL = "/api/auth/register";

    /**
     * Token expire time (hours)
     */
    public static final int TOKEN_EXPIRE_HOURS = 24;

    /**
     * Refresh token expire time (days)
     */
    public static final int REFRESH_TOKEN_EXPIRE_DAYS = 7;

    /**
     * JWT secret key placeholder (should be configured in application.yml)
     */
    public static final String SECRET_KEY = "flowx-enterprise-saas-platform-secret-key-please-change-in-production";

    /**
     * Token issuer
     */
    public static final String TOKEN_ISSUER = "flowx";

    /**
     * Token header name
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token prefix
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT claims key for user ID
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT claims key for username
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * JWT claims key for tenant ID
     */
    public static final String CLAIM_TENANT_ID = "tenantId";

    /**
     * JWT claims key for roles
     */
    public static final String CLAIM_ROLES = "roles";

    /**
     * JWT claims key for permissions
     */
    public static final String CLAIM_PERMISSIONS = "permissions";

    /**
     * JWT claims key for data scope
     */
    public static final String CLAIM_DATA_SCOPE = "dataScope";

    /**
     * Anonymous user
     */
    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * Password max retry count
     */
    public static final int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * Password lock time (minutes)
     */
    public static final int PASSWORD_LOCK_MINUTES = 30;
}
