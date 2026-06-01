package com.flowx.user.convert;

import com.flowx.user.dto.RoleDTO;
import com.flowx.user.entity.SysRole;
import com.flowx.user.vo.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Role entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface RoleConvert {

    RoleConvert INSTANCE = Mappers.getMapper(RoleConvert.class);

    /**
     * Convert RoleDTO to SysRole entity
     *
     * @param dto role DTO
     * @return role entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysRole toEntity(RoleDTO dto);

    /**
     * Convert SysRole entity to RoleVO
     *
     * @param entity role entity
     * @return role VO
     */
    @Mapping(target = "menuIds", ignore = true)
    RoleVO toVO(SysRole entity);

    /**
     * Convert list of SysRole entities to list of RoleVOs
     *
     * @param entities role entity list
     * @return role VO list
     */
    List<RoleVO> toVOList(List<SysRole> entities);

    /**
     * Update SysRole entity from RoleDTO
     *
     * @param dto    role DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(RoleDTO dto, @MappingTarget SysRole entity);
}
