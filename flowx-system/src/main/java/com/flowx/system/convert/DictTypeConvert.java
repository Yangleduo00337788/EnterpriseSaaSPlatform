package com.flowx.system.convert;

import com.flowx.system.dto.DictTypeDTO;
import com.flowx.system.entity.SysDictType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Dictionary type entity/DTO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DictTypeConvert {

    DictTypeConvert INSTANCE = Mappers.getMapper(DictTypeConvert.class);

    /**
     * Convert DictTypeDTO to SysDictType entity
     *
     * @param dto dict type DTO
     * @return dict type entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysDictType toEntity(DictTypeDTO dto);

    /**
     * Convert SysDictType entity to DictTypeDTO
     *
     * @param entity dict type entity
     * @return dict type DTO
     */
    DictTypeDTO toDTO(SysDictType entity);

    /**
     * Convert list of SysDictType entities to list of DictTypeDTOs
     *
     * @param entities dict type entity list
     * @return dict type DTO list
     */
    List<DictTypeDTO> toDTOList(List<SysDictType> entities);

    /**
     * Update SysDictType entity from DictTypeDTO
     *
     * @param dto    dict type DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(DictTypeDTO dto, @MappingTarget SysDictType entity);
}
