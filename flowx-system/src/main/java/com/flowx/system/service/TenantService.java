package com.flowx.system.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.system.dto.TenantDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.vo.TenantStatsVO;
import com.flowx.system.vo.TenantVO;

/**
 * Tenant service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface TenantService {

    /**
     * Get tenant by ID
     *
     * @param tenantId tenant ID
     * @return tenant VO
     */
    TenantVO getTenantById(Long tenantId);

    /**
     * Create new tenant
     *
     * @param dto tenant creation DTO
     * @return created tenant ID
     */
    Long createTenant(TenantDTO dto);

    /**
     * Update existing tenant
     *
     * @param tenantId tenant ID
     * @param dto      tenant update DTO
     */
    void updateTenant(Long tenantId, TenantDTO dto);

    /**
     * Delete tenant (soft delete)
     *
     * @param tenantId tenant ID
     */
    void deleteTenant(Long tenantId);

    /**
     * List tenants with pagination and filters
     *
     * @param queryDTO query parameters
     * @return paginated tenant list
     */
    PageResult<TenantVO> listTenants(TenantQueryDTO queryDTO);

    /**
     * Assign package to tenant
     *
     * @param tenantId  tenant ID
     * @param packageId package ID
     */
    void assignPackage(Long tenantId, Long packageId);

    /**
     * Get tenant statistics
     *
     * @param tenantId tenant ID
     * @return tenant statistics
     */
    TenantStatsVO getTenantStats(Long tenantId);
}
