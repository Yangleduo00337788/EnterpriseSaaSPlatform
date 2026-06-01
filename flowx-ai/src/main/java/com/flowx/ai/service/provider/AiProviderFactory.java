package com.flowx.ai.service.provider;

import com.flowx.ai.config.AiProviderConfig;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI provider factory - resolves provider by name
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Component
public class AiProviderFactory {

    private final Map<String, AIProvider> providerMap = new HashMap<>();
    private final AiProviderConfig aiProviderConfig;

    public AiProviderFactory(List<AIProvider> providers, AiProviderConfig aiProviderConfig) {
        this.aiProviderConfig = aiProviderConfig;
        for (AIProvider provider : providers) {
            providerMap.put(provider.getProviderName(), provider);
            log.info("Registered AI provider: {}", provider.getProviderName());
        }
    }

    /**
     * Get AI provider by name
     *
     * @param name provider name (openai/deepseek/qwen/claude/gemini)
     * @return AI provider instance
     * @throws BizException if provider not found
     */
    public AIProvider getProvider(String name) {
        AIProvider provider = providerMap.get(name);
        if (provider == null) {
            throw new BizException(ResultCodeEnum.AI_SERVICE_ERROR.getCode(), "不支持的AI提供商: " + name);
        }
        return provider;
    }

    /**
     * Get the default AI provider
     *
     * @return default AI provider instance
     */
    public AIProvider getDefaultProvider() {
        return getProvider(aiProviderConfig.getDefaultProvider());
    }
}
