package com.flowx.system.convert;

import com.flowx.system.dto.TenantPackageDTO;
import com.flowx.system.entity.SysTenantPackage;
import com.flowx.system.vo.TenantPackageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Tenant package entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface TenantPackageConvert {

    TenantPackageConvert INSTANCE = Mappers.getMapper(TenantPackageConvert.class);

    /**
     * Convert TenantPackageDTO to SysTenantPackage entity
     *
     * @param dto package DTO
     * @return package entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysTenantPackage toEntity(TenantPackageDTO dto);

    /**
     * Convert SysTenantPackage entity to TenantPackageVO
     *
     * @param entity package entity
     * @return package VO
     */
    TenantPackageVO toVO(SysTenantPackage entity);

    /**
     * Convert list of SysTenantPackage entities to list of TenantPackageVOs
     *
     * @param entities package entity list
     * @return package VO list
     */
    List<TenantPackageVO> toVOList(List<SysTenantPackage> entities);

    /**
     * Update SysTenantPackage entity from TenantPackageDTO
     *
     * @param dto    package DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(TenantPackageDTO dto, @MappingTarget SysTenantPackage entity);
}
