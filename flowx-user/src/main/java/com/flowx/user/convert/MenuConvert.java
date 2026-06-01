package com.flowx.user.convert;

import com.flowx.user.dto.MenuDTO;
import com.flowx.user.entity.SysMenu;
import com.flowx.user.vo.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Menu entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface MenuConvert {

    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    /**
     * Convert MenuDTO to SysMenu entity
     *
     * @param dto menu DTO
     * @return menu entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysMenu toEntity(MenuDTO dto);

    /**
     * Convert SysMenu entity to MenuVO
     *
     * @param entity menu entity
     * @return menu VO
     */
    @Mapping(target = "children", ignore = true)
    MenuVO toVO(SysMenu entity);

    /**
     * Convert list of SysMenu entities to list of MenuVOs
     *
     * @param entities menu entity list
     * @return menu VO list
     */
    List<MenuVO> toVOList(List<SysMenu> entities);

    /**
     * Update SysMenu entity from MenuDTO
     *
     * @param dto    menu DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(MenuDTO dto, @MappingTarget SysMenu entity);
}
