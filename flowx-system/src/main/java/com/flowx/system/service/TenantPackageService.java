package com.flowx.system.service;

import com.flowx.system.dto.TenantPackageDTO;
import com.flowx.system.vo.TenantPackageVO;

import java.util.List;

/**
 * Tenant package service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface TenantPackageService {

    /**
     * Get package by ID
     *
     * @param packageId package ID
     * @return package VO
     */
    TenantPackageVO getPackageById(Long packageId);

    /**
     * Create new package
     *
     * @param dto package creation DTO
     * @return created package ID
     */
    Long createPackage(TenantPackageDTO dto);

    /**
     * Update existing package
     *
     * @param packageId package ID
     * @param dto       package update DTO
     */
    void updatePackage(Long packageId, TenantPackageDTO dto);

    /**
     * Delete package (soft delete)
     *
     * @param packageId package ID
     */
    void deletePackage(Long packageId);

    /**
     * List all packages
     *
     * @return package list
     */
    List<TenantPackageVO> listPackages();
}
