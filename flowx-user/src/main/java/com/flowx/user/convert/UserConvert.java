package com.flowx.user.convert;

import com.flowx.common.config.FlowxMapstructConfig;
import com.flowx.user.dto.UserDTO;
import com.flowx.user.entity.SysUser;
import com.flowx.user.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * User entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", config = FlowxMapstructConfig.class)
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    /**
     * Convert UserDTO to SysUser entity
     *
     * @param dto user DTO
     * @return user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "loginIp", ignore = true)
    @Mapping(target = "loginTime", ignore = true)
    SysUser toEntity(UserDTO dto);

    /**
     * Convert SysUser entity to UserVO
     *
     * @param entity user entity
     * @return user VO
     */
    @Mapping(target = "deptName", ignore = true)
    @Mapping(target = "positionName", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserVO toVO(SysUser entity);

    /**
     * Convert list of SysUser entities to list of UserVOs
     *
     * @param entities user entity list
     * @return user VO list
     */
    List<UserVO> toVOList(List<SysUser> entities);

    /**
     * Update SysUser entity from UserDTO
     *
     * @param dto    user DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "loginIp", ignore = true)
    @Mapping(target = "loginTime", ignore = true)
    void updateEntity(UserDTO dto, @MappingTarget SysUser entity);
}
