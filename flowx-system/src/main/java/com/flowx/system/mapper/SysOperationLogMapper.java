package com.flowx.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Operation log mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {
}
