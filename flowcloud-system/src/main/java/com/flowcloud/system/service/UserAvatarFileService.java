package com.flowcloud.system.service;

import com.flowcloud.system.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

public interface UserAvatarFileService {

    void recordUploadedAvatarFile(SysUser user, MultipartFile file, StorageService.StoredFileResult storedFileResult);

    void retainSelectedAvatarFile(Long tenantId, Long userId, String selectedAvatarUrl);
}
