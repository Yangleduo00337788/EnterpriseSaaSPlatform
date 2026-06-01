package com.flowx.common.util;

import com.flowx.common.core.base.SecurityUser;
import com.flowx.common.core.constant.CommonConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security utility class for getting current user information
 *
 * @author FlowX
 * @since 1.0.0
 */
public final class SecurityUtil {

    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * ThreadLocal for SecurityUser
     */
    private static final ThreadLocal<SecurityUser> SECURITY_USER_HOLDER = new ThreadLocal<>();

    /**
     * Set security user to ThreadLocal
     *
     * @param securityUser security user
     */
    public static void setSecurityUser(SecurityUser securityUser) {
        SECURITY_USER_HOLDER.set(securityUser);
    }

    /**
     * Get security user from ThreadLocal
     *
     * @return security user or null
     */
    public static SecurityUser getSecurityUser() {
        SecurityUser securityUser = SECURITY_USER_HOLDER.get();
        if (securityUser == null) {
            securityUser = getUserFromSecurityContext();
        }
        return securityUser;
    }

    /**
     * Remove security user from ThreadLocal
     */
    public static void removeSecurityUser() {
        SECURITY_USER_HOLDER.remove();
    }

    /**
     * Get current user ID
     *
     * @return user ID or null
     */
    public static Long getUserId() {
        SecurityUser securityUser = getSecurityUser();
        return securityUser != null ? securityUser.getUserId() : null;
    }

    /**
     * Get current username
     *
     * @return username or null
     */
    public static String getUsername() {
        SecurityUser securityUser = getSecurityUser();
        if (securityUser != null) {
            return securityUser.getUsername();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * Get current tenant ID
     *
     * @return tenant ID or default tenant ID
     */
    public static Long getTenantId() {
        SecurityUser securityUser = getSecurityUser();
        return securityUser != null ? securityUser.getTenantId() : CommonConstant.DEFAULT_TENANT_ID;
    }

    /**
     * Get current user data scope
     *
     * @return data scope or null
     */
    public static Integer getDataScope() {
        SecurityUser securityUser = getSecurityUser();
        return securityUser != null ? securityUser.getDataScope() : null;
    }

    /**
     * Check if current user is super admin
     *
     * @return true if super admin
     */
    public static boolean isSuperAdmin() {
        Long userId = getUserId();
        return CommonConstant.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * Get authentication from SecurityContext
     *
     * @return authentication or null
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get SecurityUser from SecurityContext
     *
     * @return security user or null
     */
    private static SecurityUser getUserFromSecurityContext() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        }
        return null;
    }
}
