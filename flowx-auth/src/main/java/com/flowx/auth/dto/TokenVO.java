package com.flowx.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token response VO
 *
 * @author FlowX Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {

    /**
     * Access token
     */
    private String accessToken;

    /**
     * Refresh token
     */
    private String refreshToken;

    /**
     * Token expiration time in seconds
     */
    private long expiresIn;

    /**
     * Token type
     */
    @Builder.Default
    private String tokenType = "Bearer";
}
