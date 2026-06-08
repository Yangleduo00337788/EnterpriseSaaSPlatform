package com.flowcloud.system.service;

import com.flowcloud.system.dto.StorageSettingsDTO;
import com.flowcloud.system.support.StorageType;
import com.flowcloud.system.vo.StorageSettingsVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    record StoredFileResult(StorageType storageType, String fileKey, String fileUrl) {
    }

    StorageSettingsVO getCurrentSettings();

    void updateCurrentSettings(StorageSettingsDTO dto);

    String testConnection(StorageSettingsDTO dto);

    StoredFileResult store(MultipartFile file, String directory) throws IOException;

    InputStream openStream(Long tenantId, StorageType storageType, String fileKey);

    void delete(StorageType storageType, String fileKey);
}
