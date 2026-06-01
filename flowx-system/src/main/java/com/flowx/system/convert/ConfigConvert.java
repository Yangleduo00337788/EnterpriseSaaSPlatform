package com.flowx.system.convert;

import com.flowx.system.dto.ConfigDTO;
import com.flowx.system.entity.SysConfig;
import com.flowx.system.vo.ConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * System config entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ConfigConvert {

    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    /**
     * Convert ConfigDTO to SysConfig entity
     *
     * @param dto config DTO
     * @return config entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysConfig toEntity(ConfigDTO dto);

    /**
     * Convert SysConfig entity to ConfigVO
     *
     * @param entity config entity
     * @return config VO
     */
    ConfigVO toVO(SysConfig entity);

    /**
     * Convert list of SysConfig entities to list of ConfigVOs
     *
     * @param entities config entity list
     * @return config VO list
     */
    List<ConfigVO> toVOList(List<SysConfig> entities);

    /**
     * Update SysConfig entity from ConfigDTO
     *
     * @param dto    config DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(ConfigDTO dto, @MappingTarget SysConfig entity);
}
