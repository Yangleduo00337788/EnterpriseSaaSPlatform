package com.flowx.system.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.system.dto.TenantDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.service.TenantService;
import com.flowx.system.vo.TenantStatsVO;
import com.flowx.system.vo.TenantVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Tenant management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * Get tenant by ID
     *
     * @param id tenant ID
     * @return tenant VO
     */
    @GetMapping("/{id}")
    public R<TenantVO> getTenantById(@PathVariable("id") Long id) {
        TenantVO tenantVO = tenantService.getTenantById(id);
        return R.ok(tenantVO);
    }

    /**
     * List tenants with pagination
     *
     * @param queryDTO query parameters
     * @return paginated tenant list
     */
    @GetMapping("/list")
    public R<PageResult<TenantVO>> listTenants(TenantQueryDTO queryDTO) {
        PageResult<TenantVO> result = tenantService.listTenants(queryDTO);
        return R.ok(result);
    }

    /**
     * Create new tenant
     *
     * @param dto tenant creation DTO
     * @return created tenant ID
     */
    @PostMapping
    public R<Long> createTenant(@Valid @RequestBody TenantDTO dto) {
        Long tenantId = tenantService.createTenant(dto);
        return R.ok(tenantId);
    }

    /**
     * Update existing tenant
     *
     * @param id  tenant ID
     * @param dto tenant update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateTenant(@PathVariable("id") Long id, @Valid @RequestBody TenantDTO dto) {
        tenantService.updateTenant(id, dto);
        return R.ok();
    }

    /**
     * Delete tenant
     *
     * @param id tenant ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteTenant(@PathVariable("id") Long id) {
        tenantService.deleteTenant(id);
        return R.ok();
    }

    /**
     * Get tenant statistics
     *
     * @param id tenant ID
     * @return tenant statistics
     */
    @GetMapping("/{id}/stats")
    public R<TenantStatsVO> getTenantStats(@PathVariable("id") Long id) {
        TenantStatsVO stats = tenantService.getTenantStats(id);
        return R.ok(stats);
    }
}
