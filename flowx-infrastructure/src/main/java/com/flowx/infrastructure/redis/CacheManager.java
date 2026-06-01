package com.flowx.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Business-level cache service implementing cache-aside pattern.
 * Provides cache operations for common business entities with null value protection.
 */
@Slf4j
@Service
public class CacheManager {

    private final RedisService redisService;

    public CacheManager(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Get user info from cache or load from database.
     * Caches null values with short TTL to prevent cache penetration.
     *
     * @param userId the user ID
     * @param loader the database loader function
     * @return user info object, or null if not found
     */
    public Object getUserInfo(Long userId, Supplier<Object> loader) {
        String key = CacheConstant.CACHE_USER + userId;
        return getWithCacheAside(key, loader, CacheConstant.USER_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Get role permissions from cache or load from database.
     *
     * @param roleId the role ID
     * @param loader the database loader function
     * @return permissions object, or null if not found
     */
    public Object getRolePermissions(Long roleId, Supplier<Object> loader) {
        String key = CacheConstant.CACHE_ROLE + roleId;
        return getWithCacheAside(key, loader, CacheConstant.ROLE_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Get tenant info from cache or load from database.
     *
     * @param tenantId the tenant ID
     * @param loader the database loader function
     * @return tenant info object, or null if not found
     */
    public Object getTenantInfo(Long tenantId, Supplier<Object> loader) {
        String key = CacheConstant.CACHE_TENANT + tenantId;
        return getWithCacheAside(key, loader, CacheConstant.TENANT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Get dictionary data from cache or load from database.
     *
     * @param dictType the dictionary type
     * @param loader the database loader function
     * @return dictionary data, or null if not found
     */
    public Object getDictData(String dictType, Supplier<Object> loader) {
        String key = CacheConstant.CACHE_DICT + dictType;
        return getWithCacheAside(key, loader, CacheConstant.DICT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Remove cached user info.
     */
    public void evictUserInfo(Long userId) {
        String key = CacheConstant.CACHE_USER + userId;
        redisService.delete(key);
        log.debug("Evicted user cache: {}", userId);
    }

    /**
     * Remove cached role permissions.
     */
    public void evictRolePermissions(Long roleId) {
        String key = CacheConstant.CACHE_ROLE + roleId;
        redisService.delete(key);
        log.debug("Evicted role cache: {}", roleId);
    }

    /**
     * Remove cached tenant info.
     */
    public void evictTenantInfo(Long tenantId) {
        String key = CacheConstant.CACHE_TENANT + tenantId;
        redisService.delete(key);
        log.debug("Evicted tenant cache: {}", tenantId);
    }

    /**
     * Remove cached dictionary data.
     */
    public void evictDictData(String dictType) {
        String key = CacheConstant.CACHE_DICT + dictType;
        redisService.delete(key);
        log.debug("Evicted dict cache: {}", dictType);
    }

    /**
     * Cache-aside pattern implementation.
     * If cache hit, return cached value.
     * If cache miss, load from database and cache the result.
     * Null values are cached with short TTL to prevent cache penetration.
     */
    private Object getWithCacheAside(String key, Supplier<Object> loader,
                                     long ttl, TimeUnit unit) {
        // Try to get from cache
        Object cached = redisService.get(key);
        if (cached != null) {
            if (CacheConstant.NULL_PLACEHOLDER.equals(cached.toString())) {
                log.debug("Cache hit (null placeholder): {}", key);
                return null;
            }
            log.debug("Cache hit: {}", key);
            return cached;
        }

        // Cache miss, load from database
        Object value = loader.get();
        if (value != null) {
            redisService.set(key, value, ttl, unit);
            log.debug("Cache loaded and set: {}", key);
        } else {
            // Cache null value with short TTL to prevent penetration
            redisService.set(key, CacheConstant.NULL_PLACEHOLDER,
                    CacheConstant.NULL_VALUE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cache null value protection: {}", key);
        }
        return value;
    }
}
