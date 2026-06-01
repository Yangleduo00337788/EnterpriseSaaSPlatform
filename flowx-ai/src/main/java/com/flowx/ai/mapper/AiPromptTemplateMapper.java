package com.flowx.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.ai.entity.AiPromptTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI prompt template mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface AiPromptTemplateMapper extends BaseMapper<AiPromptTemplate> {
}
