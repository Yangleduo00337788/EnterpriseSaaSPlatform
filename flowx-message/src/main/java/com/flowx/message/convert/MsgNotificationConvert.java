package com.flowx.message.convert;

import com.flowx.message.dto.SendNotificationDTO;
import com.flowx.message.entity.MsgNotification;
import com.flowx.message.vo.MsgNotificationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Notification entity/DTO/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface MsgNotificationConvert {

    MsgNotificationConvert INSTANCE = Mappers.getMapper(MsgNotificationConvert.class);

    /**
     * Convert SendNotificationDTO to MsgNotification entity
     *
     * @param dto send notification DTO
     * @return notification entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "readStatus", ignore = true)
    @Mapping(target = "readTime", ignore = true)
    MsgNotification toEntity(SendNotificationDTO dto);

    /**
     * Convert MsgNotification entity to MsgNotificationVO
     *
     * @param entity notification entity
     * @return notification VO
     */
    MsgNotificationVO toVO(MsgNotification entity);

    /**
     * Convert list of MsgNotification entities to list of MsgNotificationVOs
     *
     * @param entities notification entity list
     * @return notification VO list
     */
    List<MsgNotificationVO> toVOList(List<MsgNotification> entities);

    /**
     * Update MsgNotification entity from SendNotificationDTO
     *
     * @param dto    send notification DTO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "readStatus", ignore = true)
    @Mapping(target = "readTime", ignore = true)
    void updateEntity(SendNotificationDTO dto, @MappingTarget MsgNotification entity);
}
