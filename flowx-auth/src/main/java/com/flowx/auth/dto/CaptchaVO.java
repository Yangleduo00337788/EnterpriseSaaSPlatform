package com.flowx.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Captcha response VO
 *
 * @author FlowX Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {

    /**
     * Captcha key for verification
     */
    private String captchaKey;

    /**
     * Captcha image in base64 format
     */
    private String captchaImage;

    /**
     * Captcha expiration timestamp
     */
    private long expireTime;
}
