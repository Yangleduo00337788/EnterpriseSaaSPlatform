package com.flowcloud.common.security.crypto;

public final class ApiCryptoConstants {

    public static final String ENCRYPTION_FLAG_HEADER = "X-Api-Encrypted";
    public static final String ENCRYPTED_KEY_HEADER = "X-Api-Encrypted-Key";
    public static final String KEY_FINGERPRINT_HEADER = "X-Api-Key-Fingerprint";
    public static final String REQUEST_KEY_ATTRIBUTE = ApiCryptoConstants.class.getName() + ".REQUEST_KEY";
    public static final String SKIP_RESPONSE_ENCRYPTION_ATTRIBUTE =
            ApiCryptoConstants.class.getName() + ".SKIP_RESPONSE_ENCRYPTION";
    public static final String API_PREFIX = "/api/";
    public static final String PUBLIC_KEY_PATH = "/api/security/public-key";
    public static final String ENCRYPTION_ENABLED = "1";

    private ApiCryptoConstants() {
    }
}
