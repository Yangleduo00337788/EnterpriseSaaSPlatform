package com.flowcloud.system.service;

import java.util.Set;

public interface RoleAuthService {

    Set<String> getCurrentUserRoles();

    Set<String> getCurrentUserPermissions();

    String getCurrentDataScope();

    boolean isAdmin();

    boolean isApprover();

    boolean hasPermission(String permission);

    void requirePermission(String permission);

    void requireAnyPermission(String... permissions);

    void requireAdmin();

    void requireApprover();
}
