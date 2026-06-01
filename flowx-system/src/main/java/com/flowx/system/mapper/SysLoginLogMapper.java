package com.flowx.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.system.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Login log mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
