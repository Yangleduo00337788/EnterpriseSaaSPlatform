package com.flowx.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties
 *
 * @author FlowX Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "flowx.jwt")
public class JwtConfig {

    /**
     * JWT signing secret
     */
    private String secret;

    /**
     * Access token expiration time in hours
     */
    private int accessTokenExpireHours = 24;

    /**
     * Refresh token expiration time in days
     */
    private int refreshTokenExpireDays = 7;

    /**
     * Token issuer
     */
    private String issuer = "flowx";
}
