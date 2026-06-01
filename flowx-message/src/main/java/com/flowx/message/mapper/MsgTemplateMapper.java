package com.flowx.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.message.entity.MsgTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Message template mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface MsgTemplateMapper extends BaseMapper<MsgTemplate> {

    /**
     * Select template by template code
     *
     * @param templateCode template code
     * @return template entity or null
     */
    MsgTemplate selectByTemplateCode(@Param("templateCode") String templateCode);
}
