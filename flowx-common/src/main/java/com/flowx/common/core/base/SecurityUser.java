package com.flowx.common.core.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * Security user DTO for Spring Security context
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String username;

    /**
     * Tenant ID
     */
    private Long tenantId;

    /**
     * Role keys
     */
    private Set<String> roles;

    /**
     * Permission strings
     */
    private Set<String> permissions;

    /**
     * Data scope
     */
    private Integer dataScope;
}
