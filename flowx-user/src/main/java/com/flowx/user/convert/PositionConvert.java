package com.flowx.user.convert;

import com.flowx.user.dto.PositionDTO;
import com.flowx.user.entity.SysPosition;
import com.flowx.user.vo.PositionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Position entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface PositionConvert {

    PositionConvert INSTANCE = Mappers.getMapper(PositionConvert.class);

    /**
     * Convert PositionDTO to SysPosition entity
     *
     * @param dto position DTO
     * @return position entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysPosition toEntity(PositionDTO dto);

    /**
     * Convert SysPosition entity to PositionVO
     *
     * @param entity position entity
     * @return position VO
     */
    PositionVO toVO(SysPosition entity);

    /**
     * Convert list of SysPosition entities to list of PositionVOs
     *
     * @param entities position entity list
     * @return position VO list
     */
    List<PositionVO> toVOList(List<SysPosition> entities);

    /**
     * Update SysPosition entity from PositionDTO
     *
     * @param dto    position DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(PositionDTO dto, @MappingTarget SysPosition entity);
}
