package com.flowcloud.system.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.StorageSettingsDTO;
import com.flowcloud.system.entity.SysTenant;
import com.flowcloud.system.mapper.SysTenantMapper;
import com.flowcloud.system.service.StorageService;
import com.flowcloud.system.support.StorageType;
import com.flowcloud.system.vo.StorageSettingsVO;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.GetObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String STORAGE_SETTINGS_KEY = "storageSettings";

    private final SysTenantMapper tenantMapper;

    @Value("${flowcloud.upload.path:${user.home}/flowcloud-uploads}")
    private String defaultLocalPath;

    @Value("${flowcloud.upload.base-url:http://localhost:8080/uploads}")
    private String defaultLocalBaseUrl;

    @Value("${flowcloud.minio.endpoint:http://localhost:9000}")
    private String defaultMinioEndpoint;

    @Value("${flowcloud.minio.console-url:http://localhost:9001}")
    private String defaultMinioConsoleUrl;

    @Value("${flowcloud.minio.access-key:minioadmin}")
    private String defaultMinioAccessKey;

    @Value("${flowcloud.minio.secret-key:minioadmin123}")
    private String defaultMinioSecretKey;

    @Value("${flowcloud.minio.bucket:flowcloud}")
    private String defaultMinioBucket;

    @Override
    public StorageSettingsVO getCurrentSettings() {
        return toVO(resolveStorageSettings(getCurrentTenant()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentSettings(StorageSettingsDTO dto) {
        SysTenant tenant = getCurrentTenant();
        StorageSettings updated = buildSettings(dto, resolveStorageSettings(tenant));

        JSONObject packageConfig = readPackageConfig(tenant.getPackageConfig());
        JSONObject storageConfig = new JSONObject();
        storageConfig.set("type", updated.storageType.name());
        storageConfig.set("localPath", updated.localPath);
        storageConfig.set("localBaseUrl", updated.localBaseUrl);
        storageConfig.set("minioEndpoint", updated.minioEndpoint);
        storageConfig.set("minioAccessKey", updated.minioAccessKey);
        storageConfig.set("minioSecretKey", updated.minioSecretKey);
        storageConfig.set("minioBucket", updated.minioBucket);
        storageConfig.set("minioBaseUrl", updated.minioBaseUrl);
        storageConfig.set("minioConsoleUrl", updated.minioConsoleUrl);
        packageConfig.set(STORAGE_SETTINGS_KEY, storageConfig);
        tenant.setPackageConfig(packageConfig.toString());
        tenantMapper.update(tenant);
    }

    @Override
    public String testConnection(StorageSettingsDTO dto) {
        StorageSettings settings = buildSettings(dto, resolveStorageSettings(getCurrentTenant()));
        if (settings.storageType == StorageType.LOCAL) {
            File directory = new File(settings.localPath);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new BusinessException("本地存储目录不可创建");
            }
            if (!directory.isDirectory()) {
                throw new BusinessException("本地存储路径不是有效目录");
            }
            if (!directory.canWrite()) {
                throw new BusinessException("本地存储目录不可写");
            }
            return "本地存储目录可访问且具备写入权限";
        }

        try {
            MinioClient client = buildMinioClient(settings);
            boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(settings.minioBucket).build());
            if (!bucketExists) {
                throw new BusinessException("MinIO 连接成功，但 Bucket 不存在: " + settings.minioBucket);
            }
            return "MinIO 连接成功，Bucket 可访问";
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("MinIO 连通性测试失败: " + ex.getMessage());
        }
    }

    @Override
    public StoredFileResult store(MultipartFile file, String directory) throws IOException {
        StorageSettings settings = resolveStorageSettings(getCurrentTenant());
        String safeDirectory = sanitizeDirectory(directory);
        String ext = getExtension(file.getOriginalFilename());
        String fileKey = safeDirectory + "/" + LocalDateTime.now().format(DATE_PATH_FORMATTER)
                + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

        if (settings.storageType == StorageType.MINIO) {
            return storeToMinio(file, fileKey, settings);
        }
        return storeToLocal(file, fileKey, settings);
    }

    @Override
    public InputStream openStream(Long tenantId, StorageType storageType, String fileKey) {
        if (storageType == null || !StringUtils.hasText(fileKey)) {
            throw new BusinessException("文件不存在或存储标识无效");
        }
        StorageSettings settings = resolveStorageSettings(getTenant(tenantId));
        try {
            if (storageType == StorageType.MINIO) {
                MinioClient client = buildMinioClient(settings);
                return client.getObject(GetObjectArgs.builder()
                        .bucket(settings.minioBucket)
                        .object(fileKey)
                        .build());
            }
            File file = new File(settings.localPath, fileKey);
            if (!file.exists() || !file.isFile()) {
                throw new BusinessException("文件不存在: " + fileKey);
            }
            return new FileInputStream(file);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("读取存储文件失败: " + ex.getMessage());
        }
    }

    @Override
    public void delete(StorageType storageType, String fileKey) {
        if (storageType == null || !StringUtils.hasText(fileKey)) {
            return;
        }
        StorageSettings settings = resolveStorageSettings(getCurrentTenant());
        try {
            if (storageType == StorageType.MINIO) {
                MinioClient client = buildMinioClient(settings);
                try {
                    boolean bucketExists = client.bucketExists(BucketExistsArgs.builder()
                            .bucket(settings.minioBucket)
                            .build());
                    if (!bucketExists) {
                        return;
                    }
                } catch (Exception ex) {
                    if (isIgnorableMinioDeleteException(ex)) {
                        return;
                    }
                    throw ex;
                }
                try {
                    client.removeObject(RemoveObjectArgs.builder()
                            .bucket(settings.minioBucket)
                            .object(fileKey)
                            .build());
                } catch (Exception ex) {
                    if (isIgnorableMinioDeleteException(ex)) {
                        return;
                    }
                    throw ex;
                }
                return;
            }
            File file = new File(settings.localPath, fileKey);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {
            throw new BusinessException("删除存储文件失败: " + ex.getMessage());
        }
    }

    private boolean isIgnorableMinioDeleteException(Exception ex) {
        String message = ex.getMessage();
        if (!StringUtils.hasText(message)) {
            return false;
        }
        String normalized = message.trim().toLowerCase();
        return normalized.contains("specified bucket does not exist")
                || normalized.contains("bucket does not exist")
                || normalized.contains("no such bucket")
                || normalized.contains("object does not exist")
                || normalized.contains("no such key");
    }

    private StoredFileResult storeToLocal(MultipartFile file, String fileKey, StorageSettings settings) throws IOException {
        File targetFile = new File(settings.localPath, fileKey);
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new BusinessException("本地存储目录创建失败");
        }
        file.transferTo(targetFile);
        return new StoredFileResult(StorageType.LOCAL, fileKey, joinUrl(settings.localBaseUrl, fileKey));
    }

    private StoredFileResult storeToMinio(MultipartFile file, String fileKey, StorageSettings settings) {
        try (InputStream inputStream = file.getInputStream()) {
            MinioClient client = buildMinioClient(settings);
            ensureBucketExists(client, settings.minioBucket);
            client.putObject(PutObjectArgs.builder()
                    .bucket(settings.minioBucket)
                    .object(fileKey)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(defaultIfBlank(file.getContentType(), "application/octet-stream"))
                    .build());
            return new StoredFileResult(
                    StorageType.MINIO,
                    fileKey,
                    joinUrl(settings.minioBaseUrl, settings.minioBucket, fileKey)
            );
        } catch (Exception ex) {
            throw new BusinessException("上传 MinIO 失败: " + ex.getMessage());
        }
    }

    private void ensureBucketExists(MinioClient client, String bucket) throws Exception {
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private MinioClient buildMinioClient(StorageSettings settings) {
        return MinioClient.builder()
                .endpoint(settings.minioEndpoint)
                .credentials(settings.minioAccessKey, settings.minioSecretKey)
                .build();
    }

    private StorageSettings resolveStorageSettings(SysTenant tenant) {
        JSONObject packageConfig = readPackageConfig(tenant.getPackageConfig());
        JSONObject storageConfig = packageConfig.getJSONObject(STORAGE_SETTINGS_KEY);

        StorageSettings settings = new StorageSettings();
        settings.storageType = StorageType.fromValue(readConfigValue(storageConfig, "type", StorageType.LOCAL.name()));
        settings.localPath = readConfigValue(storageConfig, "localPath", defaultLocalPath);
        settings.localBaseUrl = readConfigValue(storageConfig, "localBaseUrl", defaultLocalBaseUrl);
        settings.minioEndpoint = readConfigValue(storageConfig, "minioEndpoint", defaultMinioEndpoint);
        settings.minioAccessKey = readConfigValue(storageConfig, "minioAccessKey", defaultMinioAccessKey);
        settings.minioSecretKey = readConfigValue(storageConfig, "minioSecretKey", defaultMinioSecretKey);
        settings.minioBucket = readConfigValue(storageConfig, "minioBucket", defaultMinioBucket);
        settings.minioBaseUrl = readConfigValue(storageConfig, "minioBaseUrl", defaultMinioEndpoint);
        settings.minioConsoleUrl = readConfigValue(storageConfig, "minioConsoleUrl", defaultMinioConsoleUrl);
        return settings;
    }

    private StorageSettings buildSettings(StorageSettingsDTO dto, StorageSettings current) {
        StorageSettings updated = new StorageSettings();
        updated.storageType = StorageType.fromValue(dto.getStorageType());
        updated.localPath = defaultIfBlank(dto.getLocalPath(), current.localPath);
        updated.localBaseUrl = defaultIfBlank(dto.getLocalBaseUrl(), current.localBaseUrl);
        updated.minioEndpoint = defaultIfBlank(dto.getMinioEndpoint(), current.minioEndpoint);
        updated.minioAccessKey = defaultIfBlank(dto.getMinioAccessKey(), current.minioAccessKey);
        updated.minioSecretKey = defaultIfBlank(dto.getMinioSecretKey(), current.minioSecretKey);
        updated.minioBucket = defaultIfBlank(dto.getMinioBucket(), current.minioBucket);
        updated.minioBaseUrl = defaultIfBlank(dto.getMinioBaseUrl(), current.minioBaseUrl);
        updated.minioConsoleUrl = defaultIfBlank(dto.getMinioConsoleUrl(), current.minioConsoleUrl);

        if (updated.storageType == StorageType.LOCAL && !StringUtils.hasText(updated.localPath)) {
            throw new BusinessException("本地存储路径不能为空");
        }
        if (updated.storageType == StorageType.MINIO) {
            requireText(updated.minioEndpoint, "MinIO Endpoint 不能为空");
            requireText(updated.minioAccessKey, "MinIO AccessKey 不能为空");
            requireText(updated.minioSecretKey, "MinIO SecretKey 不能为空");
            requireText(updated.minioBucket, "MinIO Bucket 不能为空");
        }
        return updated;
    }

    private StorageSettingsVO toVO(StorageSettings settings) {
        StorageSettingsVO vo = new StorageSettingsVO();
        vo.setStorageType(settings.storageType.name());
        vo.setLocalPath(settings.localPath);
        vo.setLocalBaseUrl(settings.localBaseUrl);
        vo.setMinioEndpoint(settings.minioEndpoint);
        vo.setMinioAccessKey(settings.minioAccessKey);
        vo.setMinioSecretKey(settings.minioSecretKey);
        vo.setMinioBucket(settings.minioBucket);
        vo.setMinioBaseUrl(settings.minioBaseUrl);
        vo.setMinioConsoleUrl(settings.minioConsoleUrl);
        return vo;
    }

    private SysTenant getCurrentTenant() {
        return getTenant(TenantContext.getTenantId());
    }

    private SysTenant getTenant(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException("当前租户信息不存在");
        }
        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        return tenant;
    }

    private JSONObject readPackageConfig(String packageConfig) {
        if (!StringUtils.hasText(packageConfig)) {
            return new JSONObject();
        }
        try {
            return JSONUtil.parseObj(packageConfig);
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    private String readConfigValue(JSONObject storageConfig, String key, String defaultValue) {
        if (storageConfig == null) {
            return defaultValue;
        }
        Object value = storageConfig.get(key);
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? defaultValue : text;
    }

    private String sanitizeDirectory(String directory) {
        String normalized = defaultIfBlank(directory, "common");
        normalized = normalized.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.isEmpty() ? "common" : normalized;
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value.trim() : defaultValue;
    }

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }

    private String joinUrl(String base, String... parts) {
        StringBuilder builder = new StringBuilder();
        builder.append(trimTrailingSlash(defaultIfBlank(base, "")));
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            String cleaned = part.replace('\\', '/');
            while (cleaned.startsWith("/")) {
                cleaned = cleaned.substring(1);
            }
            if (!builder.isEmpty()) {
                builder.append('/');
            }
            builder.append(cleaned);
        }
        return builder.toString();
    }

    private String trimTrailingSlash(String value) {
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static class StorageSettings {
        private StorageType storageType;
        private String localPath;
        private String localBaseUrl;
        private String minioEndpoint;
        private String minioAccessKey;
        private String minioSecretKey;
        private String minioBucket;
        private String minioBaseUrl;
        private String minioConsoleUrl;
    }
}
