package com.flowx.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI provider configuration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "flowx.ai")
public class AiProviderConfig {

    /**
     * Default AI provider: openai/deepseek/qwen/claude/gemini
     */
    private String defaultProvider = "deepseek";

    /**
     * OpenAI configuration
     */
    private ProviderDetail openai = new ProviderDetail();

    /**
     * DeepSeek configuration
     */
    private ProviderDetail deepseek = new ProviderDetail();

    /**
     * Qwen (Aliyun DashScope) configuration
     */
    private ProviderDetail qwen = new ProviderDetail();

    /**
     * Claude (Anthropic) configuration
     */
    private ProviderDetail claude = new ProviderDetail();

    /**
     * Gemini (Google AI) configuration
     */
    private ProviderDetail gemini = new ProviderDetail();

    /**
     * Provider detail configuration
     */
    @Data
    public static class ProviderDetail {

        /**
         * API key
         */
        private String apiKey;

        /**
         * API base URL
         */
        private String baseUrl;

        /**
         * Model name
         */
        private String model;
    }
}
