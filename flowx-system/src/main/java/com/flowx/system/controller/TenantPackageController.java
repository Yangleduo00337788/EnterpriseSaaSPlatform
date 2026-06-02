package com.flowx.system.controller;

import com.flowx.common.core.result.R;
import com.flowx.system.dto.TenantPackageDTO;
import com.flowx.system.service.TenantPackageService;
import com.flowx.system.vo.TenantPackageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Tenant package management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/tenant/package")
@RequiredArgsConstructor
public class TenantPackageController {

    private final TenantPackageService tenantPackageService;

    /**
     * Get package by ID
     *
     * @param id package ID
     * @return package VO
     */
    @GetMapping("/{id}")
    public R<TenantPackageVO> getPackageById(@PathVariable("id") Long id) {
        TenantPackageVO packageVO = tenantPackageService.getPackageById(id);
        return R.ok(packageVO);
    }

    /**
     * List all packages
     *
     * @return package list
     */
    @GetMapping("/list")
    public R<List<TenantPackageVO>> listPackages() {
        List<TenantPackageVO> packages = tenantPackageService.listPackages();
        return R.ok(packages);
    }

    /**
     * Create new package
     *
     * @param dto package creation DTO
     * @return created package ID
     */
    @PostMapping
    public R<Long> createPackage(@Valid @RequestBody TenantPackageDTO dto) {
        Long packageId = tenantPackageService.createPackage(dto);
        return R.ok(packageId);
    }

    /**
     * Update existing package (id from request body)
     *
     * @param dto package update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updatePackage(@Valid @RequestBody TenantPackageDTO dto) {
        tenantPackageService.updatePackage(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete packages by IDs (comma-separated)
     *
     * @param ids package IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deletePackages(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            tenantPackageService.deletePackage(id);
        }
        return R.ok();
    }
}