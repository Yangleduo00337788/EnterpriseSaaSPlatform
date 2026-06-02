package com.flowx.file.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.file.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * File information mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FileInfoMapper extends FlexBaseMapper<FileInfo> {
}
