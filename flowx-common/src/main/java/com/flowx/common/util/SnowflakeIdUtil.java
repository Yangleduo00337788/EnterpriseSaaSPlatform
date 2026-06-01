package com.flowx.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * Snowflake ID utility class
 *
 * @author FlowX
 * @since 1.0.0
 */
public final class SnowflakeIdUtil {

    private SnowflakeIdUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Default worker ID
     */
    private static final long DEFAULT_WORKER_ID = 1;

    /**
     * Default datacenter ID
     */
    private static final long DEFAULT_DATACENTER_ID = 1;

    /**
     * Snowflake instance
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(DEFAULT_WORKER_ID, DEFAULT_DATACENTER_ID);

    /**
     * Generate next snowflake ID as Long
     *
     * @return snowflake ID
     */
    public static long nextId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * Generate next snowflake ID as String
     *
     * @return snowflake ID string
     */
    public static String nextIdStr() {
        return SNOWFLAKE.nextIdStr();
    }

    /**
     * Generate snowflake ID with custom worker and datacenter
     *
     * @param workerId     worker ID
     * @param datacenterId datacenter ID
     * @return snowflake ID
     */
    public static long nextId(long workerId, long datacenterId) {
        Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        return snowflake.nextId();
    }

    /**
     * Generate snowflake ID string with custom worker and datacenter
     *
     * @param workerId     worker ID
     * @param datacenterId datacenter ID
     * @return snowflake ID string
     */
    public static String nextIdStr(long workerId, long datacenterId) {
        Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        return snowflake.nextIdStr();
    }
}
