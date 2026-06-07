package com.flowcloud.common.security.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.Base64;

@Service
public class ApiCryptoService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_CIPHER = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final ObjectMapper objectMapper;
    private final KeyPair keyPair;
    private final ResourceLoader resourceLoader;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiCryptoService(ObjectMapper objectMapper,
                            ApiCryptoProperties apiCryptoProperties,
                            ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        this.keyPair = loadKeyPair(apiCryptoProperties);
    }

    public boolean shouldProcess(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String servletPath = request.getServletPath();
        return servletPath != null
                && servletPath.startsWith(ApiCryptoConstants.API_PREFIX)
                && !ApiCryptoConstants.PUBLIC_KEY_PATH.equals(servletPath)
                && ApiCryptoConstants.ENCRYPTION_ENABLED.equals(request.getHeader(ApiCryptoConstants.ENCRYPTION_FLAG_HEADER));
    }

    public SecretKey resolveRequestKey(HttpServletRequest request) {
        if (request == null || !shouldProcess(request)) {
            return null;
        }
        Object cachedKey = request.getAttribute(ApiCryptoConstants.REQUEST_KEY_ATTRIBUTE);
        if (cachedKey instanceof SecretKey secretKey) {
            return secretKey;
        }
        String encryptedKey = request.getHeader(ApiCryptoConstants.ENCRYPTED_KEY_HEADER);
        if (!StringUtils.hasText(encryptedKey)) {
            return null;
        }
        validateRequestFingerprint(request);
        SecretKey secretKey = decryptRequestKey(encryptedKey);
        request.setAttribute(ApiCryptoConstants.REQUEST_KEY_ATTRIBUTE, secretKey);
        return secretKey;
    }

    public String decryptRequestBody(String rawBody, SecretKey secretKey) {
        if (!StringUtils.hasText(rawBody)) {
            return rawBody;
        }
        try {
            ApiCryptoEnvelope envelope = objectMapper.readValue(rawBody, ApiCryptoEnvelope.class);
            if (!envelope.isEncrypted() || !StringUtils.hasText(envelope.getPayload()) || !StringUtils.hasText(envelope.getIv())) {
                throw new IllegalArgumentException("请求密文结构非法");
            }
            byte[] ivBytes = Base64.getDecoder().decode(envelope.getIv());
            byte[] cipherBytes = Base64.getDecoder().decode(envelope.getPayload());
            byte[] plainBytes = decryptAes(cipherBytes, secretKey, ivBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("请求体解析失败", e);
        }
    }

    public ApiCryptoEnvelope encryptResponseBody(Object body, SecretKey secretKey) {
        try {
            byte[] ivBytes = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(ivBytes);
            String json = objectMapper.writeValueAsString(body);
            byte[] cipherBytes = encryptAes(json.getBytes(StandardCharsets.UTF_8), secretKey, ivBytes);
            return new ApiCryptoEnvelope(
                    true,
                    Base64.getEncoder().encodeToString(ivBytes),
                    Base64.getEncoder().encodeToString(cipherBytes)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("响应序列化失败", e);
        }
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String getPublicKeyFingerprint() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(keyPair.getPublic().getEncoded());
            return Base64.getEncoder().encodeToString(digest).substring(0, 16);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("接口加密公钥指纹生成失败", e);
        }
    }

    private void validateRequestFingerprint(HttpServletRequest request) {
        String requestFingerprint = request.getHeader(ApiCryptoConstants.KEY_FINGERPRINT_HEADER);
        if (StringUtils.hasText(requestFingerprint) && !requestFingerprint.equals(getPublicKeyFingerprint())) {
            throw new IllegalArgumentException("接口加密密钥已失效");
        }
    }

    private SecretKey decryptRequestKey(String encryptedKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_CIPHER);
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    keyPair.getPrivate(),
                    new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT)
            );
            byte[] keyBytes = cipher.doFinal(encryptedKeyBytes);
            return new SecretKeySpec(keyBytes, AES_ALGORITHM);
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("请求密钥解密失败", e);
        }
    }

    private byte[] encryptAes(byte[] plainBytes, SecretKey secretKey, byte[] ivBytes) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, ivBytes));
            return cipher.doFinal(plainBytes);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("响应加密失败", e);
        }
    }

    private byte[] decryptAes(byte[] cipherBytes, SecretKey secretKey, byte[] ivBytes) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, ivBytes));
            return cipher.doFinal(cipherBytes);
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("请求体解密失败", e);
        }
    }

    private KeyPair loadKeyPair(ApiCryptoProperties properties) {
        try {
            PrivateKey privateKey = loadPrivateKey(properties);
            PublicKey publicKey = loadPublicKey(properties, privateKey);
            return new KeyPair(publicKey, privateKey);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("接口加密固定密钥加载失败", e);
        }
    }

    private PrivateKey loadPrivateKey(ApiCryptoProperties properties) throws GeneralSecurityException, IOException {
        String privateKeyContent = resolveKeyContent(
                properties.getPrivateKey(),
                properties.getPrivateKeyLocation(),
                "private key"
        );
        if (!StringUtils.hasText(privateKeyContent)) {
            throw new IllegalStateException("请配置 flowcloud.api-crypto.private-key 或 private-key-location");
        }
        byte[] privateKeyBytes = Base64.getDecoder().decode(normalizeKeyContent(privateKeyContent));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    private PublicKey loadPublicKey(ApiCryptoProperties properties,
                                    PrivateKey privateKey) throws GeneralSecurityException, IOException {
        String publicKeyContent = resolveKeyContent(
                properties.getPublicKey(),
                properties.getPublicKeyLocation(),
                "public key"
        );
        PublicKey publicKey = StringUtils.hasText(publicKeyContent)
                ? parsePublicKey(publicKeyContent)
                : derivePublicKey(privateKey);
        validateKeyPair(privateKey, publicKey);
        return publicKey;
    }

    private PublicKey parsePublicKey(String publicKeyContent) throws GeneralSecurityException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(normalizeKeyContent(publicKeyContent));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    private PublicKey derivePublicKey(PrivateKey privateKey) throws GeneralSecurityException {
        if (!(privateKey instanceof RSAPrivateCrtKey rsaPrivateCrtKey)) {
            throw new IllegalStateException("接口加密私钥格式不支持自动推导公钥");
        }
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(new RSAPublicKeySpec(
                rsaPrivateCrtKey.getModulus(),
                rsaPrivateCrtKey.getPublicExponent()
        ));
    }

    private void validateKeyPair(PrivateKey privateKey, PublicKey publicKey) throws GeneralSecurityException {
        byte[] challenge = new byte[32];
        secureRandom.nextBytes(challenge);
        Cipher encryptCipher = Cipher.getInstance(RSA_CIPHER);
        encryptCipher.init(
                Cipher.ENCRYPT_MODE,
                publicKey,
                new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT)
        );
        byte[] encrypted = encryptCipher.doFinal(challenge);
        Cipher decryptCipher = Cipher.getInstance(RSA_CIPHER);
        decryptCipher.init(
                Cipher.DECRYPT_MODE,
                privateKey,
                new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT)
        );
        byte[] decrypted = decryptCipher.doFinal(encrypted);
        if (!MessageDigest.isEqual(challenge, decrypted)) {
            throw new IllegalStateException("接口加密公钥与私钥不匹配");
        }
    }

    private String resolveKeyContent(String inlineValue,
                                     String location,
                                     String keyType) throws IOException {
        if (StringUtils.hasText(inlineValue)) {
            return inlineValue;
        }
        if (!StringUtils.hasText(location)) {
            return null;
        }
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new IllegalStateException("接口加密" + keyType + "资源不存在: " + location);
        }
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String normalizeKeyContent(String keyContent) {
        return keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }
}
