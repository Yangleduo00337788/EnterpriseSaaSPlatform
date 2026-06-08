package com.flowcloud.system.service.impl;

import com.flowcloud.system.entity.ManagedFileRecord;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.ManagedFileRecordMapper;
import com.flowcloud.system.service.StorageService;
import com.flowcloud.system.service.UserAvatarFileService;
import com.flowcloud.system.support.StorageType;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAvatarFileServiceImpl implements UserAvatarFileService {

    public static final String AVATAR_BIZ_TYPE = "user_avatar";
    public static final String AVATAR_FIELD_NAME = "avatar";

    private final ManagedFileRecordMapper managedFileRecordMapper;
    private final StorageService storageService;

    @Override
    public void recordUploadedAvatarFile(SysUser user, MultipartFile file, StorageService.StoredFileResult storedFileResult) {
        ManagedFileRecord record = new ManagedFileRecord();
        record.setTenantId(user.getTenantId());
        record.setBizType(AVATAR_BIZ_TYPE);
        record.setBizId(user.getId());
        record.setFieldName(AVATAR_FIELD_NAME);
        record.setOriginalName(defaultFileName(file.getOriginalFilename()));
        record.setFileKey(storedFileResult.fileKey());
        record.setFileUrl(storedFileResult.fileUrl());
        record.setStorageType(storedFileResult.storageType().name());
        record.setFileSize(file.getSize());
        record.setMimeType(file.getContentType());
        record.setUploaderId(user.getId());
        record.setUploaderName(user.getRealName());
        record.setCreateTime(LocalDateTime.now());
        managedFileRecordMapper.insert(record);
    }

    @Override
    public void retainSelectedAvatarFile(Long tenantId, Long userId, String selectedAvatarUrl) {
        if (tenantId == null || userId == null) {
            return;
        }

        List<ManagedFileRecord> avatarFiles = managedFileRecordMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ManagedFileRecord::getTenantId).eq(tenantId)
                        .and(ManagedFileRecord::getBizType).eq(AVATAR_BIZ_TYPE)
                        .and(ManagedFileRecord::getBizId).eq(userId)
                        .and(ManagedFileRecord::getFieldName).eq(AVATAR_FIELD_NAME)
                        .orderBy(ManagedFileRecord::getCreateTime, false)
                        .orderBy(ManagedFileRecord::getId, false)
        );

        boolean retained = false;
        for (ManagedFileRecord avatarFile : avatarFiles) {
            boolean matchesSelected = StringUtils.hasText(selectedAvatarUrl)
                    && selectedAvatarUrl.equals(avatarFile.getFileUrl());
            if (matchesSelected && !retained) {
                retained = true;
                continue;
            }
            deleteAvatarFile(avatarFile);
        }
    }

    private void deleteAvatarFile(ManagedFileRecord avatarFile) {
        storageService.delete(StorageType.fromValue(avatarFile.getStorageType()), avatarFile.getFileKey());
        managedFileRecordMapper.deleteById(avatarFile.getId());
    }

    private String defaultFileName(String originalFilename) {
        return StringUtils.hasText(originalFilename) ? originalFilename.trim() : "avatar";
    }
}
