package com.flowx.user.convert;

import com.flowx.user.dto.DeptDTO;
import com.flowx.user.entity.SysDept;
import com.flowx.user.vo.DeptVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Department entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    /**
     * Convert DeptDTO to SysDept entity
     *
     * @param dto department DTO
     * @return department entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysDept toEntity(DeptDTO dto);

    /**
     * Convert SysDept entity to DeptVO
     *
     * @param entity department entity
     * @return department VO
     */
    @Mapping(target = "children", ignore = true)
    DeptVO toVO(SysDept entity);

    /**
     * Convert list of SysDept entities to list of DeptVOs
     *
     * @param entities department entity list
     * @return department VO list
     */
    List<DeptVO> toVOList(List<SysDept> entities);

    /**
     * Update SysDept entity from DeptDTO
     *
     * @param dto    department DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(DeptDTO dto, @MappingTarget SysDept entity);
}
