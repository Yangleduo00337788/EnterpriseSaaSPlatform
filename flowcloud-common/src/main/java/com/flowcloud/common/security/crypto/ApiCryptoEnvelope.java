package com.flowcloud.common.security.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCryptoEnvelope {

    private boolean encrypted;
    private String iv;
    private String payload;
}
