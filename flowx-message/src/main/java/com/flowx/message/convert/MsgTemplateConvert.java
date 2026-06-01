package com.flowx.message.convert;

import com.flowx.message.entity.MsgTemplate;
import com.flowx.message.vo.MsgTemplateVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Message template entity/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface MsgTemplateConvert {

    MsgTemplateConvert INSTANCE = Mappers.getMapper(MsgTemplateConvert.class);

    /**
     * Convert MsgTemplate entity to MsgTemplateVO
     *
     * @param entity template entity
     * @return template VO
     */
    MsgTemplateVO toVO(MsgTemplate entity);

    /**
     * Convert list of MsgTemplate entities to list of MsgTemplateVOs
     *
     * @param entities template entity list
     * @return template VO list
     */
    List<MsgTemplateVO> toVOList(List<MsgTemplate> entities);

    /**
     * Convert MsgTemplateVO to MsgTemplate entity
     *
     * @param vo template VO
     * @return template entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    MsgTemplate toEntity(MsgTemplateVO vo);

    /**
     * Update MsgTemplate entity from MsgTemplateVO
     *
     * @param vo     template VO
     * @param entity target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(MsgTemplateVO vo, @MappingTarget MsgTemplate entity);
}
