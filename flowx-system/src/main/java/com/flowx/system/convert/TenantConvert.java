package com.flowx.system.convert;

import com.flowx.system.dto.TenantDTO;
import com.flowx.system.entity.SysTenant;
import com.flowx.system.vo.TenantVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Tenant entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    /**
     * Convert TenantDTO to SysTenant entity
     *
     * @param dto tenant DTO
     * @return tenant entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysTenant toEntity(TenantDTO dto);

    /**
     * Convert SysTenant entity to TenantVO
     *
     * @param entity tenant entity
     * @return tenant VO
     */
    @Mapping(target = "packageName", ignore = true)
    TenantVO toVO(SysTenant entity);

    /**
     * Convert list of SysTenant entities to list of TenantVOs
     *
     * @param entities tenant entity list
     * @return tenant VO list
     */
    List<TenantVO> toVOList(List<SysTenant> entities);

    /**
     * Update SysTenant entity from TenantDTO
     *
     * @param dto    tenant DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(TenantDTO dto, @MappingTarget SysTenant entity);
}
