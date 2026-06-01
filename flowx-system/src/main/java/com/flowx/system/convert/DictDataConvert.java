package com.flowx.system.convert;

import com.flowx.system.dto.DictDataDTO;
import com.flowx.system.entity.SysDictData;
import com.flowx.system.vo.DictDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Dictionary data entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DictDataConvert {

    DictDataConvert INSTANCE = Mappers.getMapper(DictDataConvert.class);

    /**
     * Convert DictDataDTO to SysDictData entity
     *
     * @param dto dict data DTO
     * @return dict data entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysDictData toEntity(DictDataDTO dto);

    /**
     * Convert SysDictData entity to DictDataVO
     *
     * @param entity dict data entity
     * @return dict data VO
     */
    DictDataVO toVO(SysDictData entity);

    /**
     * Convert list of SysDictData entities to list of DictDataVOs
     *
     * @param entities dict data entity list
     * @return dict data VO list
     */
    List<DictDataVO> toVOList(List<SysDictData> entities);

    /**
     * Update SysDictData entity from DictDataDTO
     *
     * @param dto    dict data DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(DictDataDTO dto, @MappingTarget SysDictData entity);
}
