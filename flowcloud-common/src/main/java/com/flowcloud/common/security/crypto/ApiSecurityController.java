package com.flowcloud.common.security.crypto;

import com.flowcloud.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SkipApiEncryption
public class ApiSecurityController {

    private final ApiCryptoService apiCryptoService;

    public ApiSecurityController(ApiCryptoService apiCryptoService) {
        this.apiCryptoService = apiCryptoService;
    }

    @GetMapping(ApiCryptoConstants.PUBLIC_KEY_PATH)
    public Result<Map<String, String>> publicKey() {
        return Result.ok(Map.of(
                "algorithm", "RSA-OAEP",
                "publicKey", apiCryptoService.getPublicKey(),
                "fingerprint", apiCryptoService.getPublicKeyFingerprint()
        ));
    }
}
