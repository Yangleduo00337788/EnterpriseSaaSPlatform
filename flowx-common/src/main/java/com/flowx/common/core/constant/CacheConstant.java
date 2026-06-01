package com.flowx.common.core.constant;

/**
 * Cache constant definitions (Redis key prefixes)
 *
 * @author FlowX
 * @since 1.0.0
 */
public final class CacheConstant {

    private CacheConstant() {
        throw new IllegalStateException("Constant class");
    }

    /**
     * Cache key separator
     */
    public static final String SEPARATOR = ":";

    /**
     * Cache prefix for flowx
     */
    public static final String FLOWX_PREFIX = "flowx" + SEPARATOR;

    /**
     * User info cache prefix
     */
    public static final String USER_INFO = FLOWX_PREFIX + "user:info:";

    /**
     * Role info cache prefix
     */
    public static final String ROLE_INFO = FLOWX_PREFIX + "role:info:";

    /**
     * Menu permission cache prefix
     */
    public static final String MENU_PERM = FLOWX_PREFIX + "menu:perm:";

    /**
     * Tenant info cache prefix
     */
    public static final String TENANT_INFO = FLOWX_PREFIX + "tenant:info:";

    /**
     * Captcha cache prefix
     */
    public static final String CAPTCHA = FLOWX_PREFIX + "captcha:";

    /**
     * Login token cache prefix
     */
    public static final String LOGIN_TOKEN = FLOWX_PREFIX + "login:token:";

    /**
     * Dictionary data cache prefix
     */
    public static final String DICT_DATA = FLOWX_PREFIX + "dict:data:";

    /**
     * Dictionary type cache prefix
     */
    public static final String DICT_TYPE = FLOWX_PREFIX + "dict:type:";

    /**
     * Config cache prefix
     */
    public static final String CONFIG = FLOWX_PREFIX + "config:";

    /**
     * Repeat submit cache prefix
     */
    public static final String REPEAT_SUBMIT = FLOWX_PREFIX + "repeat:submit:";

    /**
     * Rate limit cache prefix
     */
    public static final String RATE_LIMIT = FLOWX_PREFIX + "rate:limit:";

    /**
     * Online user cache prefix
     */
    public static final String ONLINE_USER = FLOWX_PREFIX + "online:user:";

    /**
     * Password retry count cache prefix
     */
    public static final String PWD_RETRY_COUNT = FLOWX_PREFIX + "pwd:retry:";

    /**
     * Lock prefix
     */
    public static final String LOCK = FLOWX_PREFIX + "lock:";

    /**
     * Workflow definition cache prefix
     */
    public static final String WORKFLOW_DEF = FLOWX_PREFIX + "workflow:def:";

    /**
     * Data scope cache prefix
     */
    public static final String DATA_SCOPE = FLOWX_PREFIX + "data:scope:";

    /**
     * Default cache expire time (seconds): 24 hours
     */
    public static final int DEFAULT_EXPIRE_SECONDS = 86400;

    /**
     * Short cache expire time (seconds): 5 minutes
     */
    public static final int SHORT_EXPIRE_SECONDS = 300;

    /**
     * Long cache expire time (seconds): 7 days
     */
    public static final int LONG_EXPIRE_SECONDS = 604800;
}
