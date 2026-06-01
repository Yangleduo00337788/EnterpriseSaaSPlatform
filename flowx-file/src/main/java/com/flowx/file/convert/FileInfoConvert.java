package com.flowx.file.convert;

import com.flowx.file.entity.FileInfo;
import com.flowx.file.vo.FileInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * File info entity/VO MapStruct converter
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface FileInfoConvert {

    FileInfoConvert INSTANCE = Mappers.getMapper(FileInfoConvert.class);

    /**
     * Convert FileInfo entity to FileInfoVO
     *
     * @param entity file entity
     * @return file VO
     */
    FileInfoVO toVO(FileInfo entity);

    /**
     * Convert list of FileInfo entities to list of FileInfoVOs
     *
     * @param entities file entity list
     * @return file VO list
     */
    List<FileInfoVO> toVOList(List<FileInfo> entities);
}
