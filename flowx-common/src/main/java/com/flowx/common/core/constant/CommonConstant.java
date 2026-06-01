package com.flowx.common.core.constant;

/**
 * Common constant definitions
 *
 * @author FlowX
 * @since 1.0.0
 */
public final class CommonConstant {

    private CommonConstant() {
        throw new IllegalStateException("Constant class");
    }

    /**
     * Token prefix
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Tenant ID header name
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /**
     * User ID header name
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * Username header name
     */
    public static final String HEADER_USERNAME = "X-Username";

    /**
     * Default tenant ID
     */
    public static final Long DEFAULT_TENANT_ID = 1L;

    /**
     * Super admin user ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * Super admin role key
     */
    public static final String SUPER_ADMIN_ROLE_KEY = "super_admin";

    /**
     * Super admin role name
     */
    public static final String SUPER_ADMIN_ROLE_NAME = "超级管理员";

    /**
     * Success flag
     */
    public static final String SUCCESS = "0";

    /**
     * Fail flag
     */
    public static final String FAIL = "1";

    /**
     * Yes flag
     */
    public static final Integer YES = 1;

    /**
     * No flag
     */
    public static final Integer NO = 0;

    /**
     * Enable status
     */
    public static final Integer STATUS_ENABLE = 0;

    /**
     * Disable status
     */
    public static final Integer STATUS_DISABLE = 1;

    /**
     * Normal deleted flag
     */
    public static final Integer NOT_DELETED = 0;

    /**
     * Deleted flag
     */
    public static final Integer DELETED = 1;

    /**
     * UTF-8 charset
     */
    public static final String UTF8 = "UTF-8";

    /**
     * JSON content type
     */
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    /**
     * Date format pattern
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * DateTime format pattern
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Default page number
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * Default page size
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Max page size
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Request ID header
     */
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
}
