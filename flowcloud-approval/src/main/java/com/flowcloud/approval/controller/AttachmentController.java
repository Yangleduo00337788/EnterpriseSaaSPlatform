package com.flowcloud.approval.controller;

import com.flowcloud.approval.dto.AttachmentBatchOperationDTO;
import com.flowcloud.approval.entity.AttachmentFile;
import com.flowcloud.approval.mapper.AttachmentFileMapper;
import com.flowcloud.approval.vo.AttachmentVO;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.security.crypto.SkipApiEncryption;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.common.result.Result;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.service.StorageService;
import com.flowcloud.system.support.PermissionCodes;
import com.flowcloud.system.support.StorageType;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.SysUserMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private static final String USER_AVATAR_BIZ_TYPE = "user_avatar";
    private static final String DEFAULT_ATTACHMENT_FIELD_NAME = "file";

    private final AttachmentFileMapper attachmentMapper;
    private final SysUserMapper userMapper;
    private final StorageService storageService;
    private final RoleAuthService roleAuthService;

    @PostMapping("/upload")
    public Result<AttachmentVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId,
            @RequestParam(required = false) String fieldName) throws IOException {

        if (file.isEmpty()) throw new BusinessException("请选择要上传的文件");
        String normalizedFieldName = normalizeFieldName(fieldName, bizType);

        StorageService.StoredFileResult storedFile = storageService.store(
                file,
                buildAttachmentDirectory(bizType, bizId)
        );

        SysUser uploader = userMapper.selectOneById(TenantContext.getUserId());

        AttachmentFile att = new AttachmentFile();
        att.setTenantId(TenantContext.getTenantId());
        att.setBizType(bizType);
        att.setBizId(bizId);
        att.setFieldName(normalizedFieldName);
        att.setOriginalName(file.getOriginalFilename());
        att.setFileKey(storedFile.fileKey());
        att.setFileUrl(storedFile.fileUrl());
        att.setStorageType(storedFile.storageType().name());
        att.setFileSize(file.getSize());
        att.setMimeType(file.getContentType());
        att.setUploaderId(TenantContext.getUserId());
        att.setUploaderName(uploader != null ? uploader.getRealName() : null);
        att.setCreateTime(LocalDateTime.now());
        attachmentMapper.insert(att);

        return Result.ok(toVO(att));
    }

    @SkipApiEncryption
    @GetMapping("/{id}/content")
    public ResponseEntity<StreamingResponseBody> preview(@PathVariable Long id) {
        AttachmentFile attachment = getTenantAttachmentById(id);
        String encodedFileName = URLEncoder.encode(
                sanitizeFileName(attachment.getOriginalName()), StandardCharsets.UTF_8
        ).replace("+", "%20");
        Long tenantId = attachment.getTenantId();
        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = storageService.openStream(
                    tenantId, StorageType.fromValue(attachment.getStorageType()), attachment.getFileKey())) {
                inputStream.transferTo(outputStream);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFileName)
                .contentType(resolveContentType(attachment.getMimeType()))
                .body(stream);
    }

    @GetMapping
    public Result<List<AttachmentVO>> list(
            @RequestParam String bizType, @RequestParam Long bizId,
            @RequestParam(required = false) String fieldName) {
        QueryWrapper query = QueryWrapper.create()
                .where(AttachmentFile::getTenantId).eq(TenantContext.getTenantId())
                .and(AttachmentFile::getBizType).eq(bizType)
                .and(AttachmentFile::getBizId).eq(bizId);
        if (fieldName != null) {
            query.and(AttachmentFile::getFieldName).eq(fieldName);
        }
        query.orderBy(AttachmentFile::getCreateTime, true);
        List<AttachmentVO> vos = attachmentMapper.selectListByQuery(query).stream().map(this::toVO).toList();
        return Result.ok(vos);
    }

    @GetMapping("/manage")
    public Result<PageResult<AttachmentVO>> pageManage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String storageType,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requireAnyPermission(PermissionCodes.SYSTEM_FILE, PermissionCodes.SYSTEM_FILE_VIEW);

        QueryWrapper query = QueryWrapper.create()
                .where(AttachmentFile::getTenantId).eq(TenantContext.getTenantId());
        if (StringUtils.hasText(keyword)) {
            query.and(AttachmentFile::getOriginalName).like(keyword.trim());
        }
        if (StringUtils.hasText(bizType)) {
            query.and(AttachmentFile::getBizType).eq(bizType.trim());
        }
        if (StringUtils.hasText(storageType)) {
            query.and(AttachmentFile::getStorageType).eq(StorageType.fromValue(storageType).name());
        }
        query.orderBy(AttachmentFile::getCreateTime, false);

        Page<AttachmentFile> page = attachmentMapper.paginate(pageNum, pageSize, query);
        List<AttachmentVO> records = page.getRecords().stream().map(this::toVO).toList();
        return Result.ok(PageResult.of(records, page.getTotalRow(), pageNum, pageSize));
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        roleAuthService.requirePermission(PermissionCodes.SYSTEM_FILE_DELETE);
        AttachmentFile att = attachmentMapper.selectOneById(id);
        if (att == null) return Result.ok();
        if (!TenantContext.getTenantId().equals(att.getTenantId())) {
            throw new BusinessException("无权删除其他租户附件");
        }
        clearUserAvatarReferenceIfNecessary(att);
        storageService.delete(StorageType.fromValue(att.getStorageType()), att.getFileKey());
        attachmentMapper.deleteById(id);
        return Result.ok();
    }

    @PostMapping("/batch-delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(@Valid @RequestBody AttachmentBatchOperationDTO dto) {
        roleAuthService.requirePermission(PermissionCodes.SYSTEM_FILE_DELETE);
        List<AttachmentFile> attachments = listAttachmentsByIds(dto.getIds());
        for (AttachmentFile attachment : attachments) {
            clearUserAvatarReferenceIfNecessary(attachment);
            storageService.delete(StorageType.fromValue(attachment.getStorageType()), attachment.getFileKey());
            attachmentMapper.deleteById(attachment.getId());
        }
        return Result.ok();
    }

    @SkipApiEncryption
    @PostMapping("/download")
    public ResponseEntity<StreamingResponseBody> batchDownload(@Valid @RequestBody AttachmentBatchOperationDTO dto) {
        roleAuthService.requirePermission(PermissionCodes.SYSTEM_FILE_DOWNLOAD);
        List<AttachmentFile> attachments = listAttachmentsByIds(dto.getIds());
        String archiveName = "attachments-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".zip";
        String encodedArchiveName = URLEncoder.encode(archiveName, StandardCharsets.UTF_8).replace("+", "%20");
        StreamingResponseBody stream = outputStream -> {
            Set<String> usedNames = new HashSet<>();
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
                for (AttachmentFile attachment : attachments) {
                    String entryName = buildUniqueEntryName(attachment.getOriginalName(), attachment.getId(), usedNames);
                    try (InputStream inputStream = storageService.openStream(
                            attachment.getTenantId(), StorageType.fromValue(attachment.getStorageType()), attachment.getFileKey())) {
                        zipOutputStream.putNextEntry(new ZipEntry(entryName));
                        inputStream.transferTo(zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
                zipOutputStream.finish();
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedArchiveName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }

    private AttachmentVO toVO(AttachmentFile att) {
        AttachmentVO vo = new AttachmentVO();
        vo.setId(att.getId());
        vo.setBizType(att.getBizType());
        vo.setBizId(att.getBizId());
        fillBizDisplayInfo(att, vo);
        vo.setFieldName(normalizeFieldName(att.getFieldName(), att.getBizType()));
        vo.setOriginalName(att.getOriginalName());
        vo.setFileUrl(buildAttachmentAccessUrl(att));
        vo.setStorageType(att.getStorageType());
        vo.setFileSize(att.getFileSize());
        vo.setMimeType(att.getMimeType());
        vo.setUploaderName(att.getUploaderName());
        vo.setCreateTime(att.getCreateTime());
        return vo;
    }

    private String buildAttachmentAccessUrl(AttachmentFile attachment) {
        if (attachment.getId() == null) {
            return attachment.getFileUrl();
        }
        return "/api/attachments/" + attachment.getId() + "/content";
    }

    private String buildAttachmentDirectory(String bizType, Long bizId) {
        String typePart = normalizePathPart(bizType, "common");
        String bizIdPart = bizId == null ? "common" : String.valueOf(bizId);
        return "attachments/" + typePart + "/" + bizIdPart;
    }

    private List<AttachmentFile> listAttachmentsByIds(List<Long> ids) {
        List<AttachmentFile> attachments = attachmentMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(AttachmentFile::getTenantId).eq(TenantContext.getTenantId())
                        .and(AttachmentFile::getId).in(ids)
        );
        if (attachments.isEmpty()) {
            throw new BusinessException("未找到可操作的文件");
        }
        return attachments;
    }

    private AttachmentFile getTenantAttachmentById(Long id) {
        AttachmentFile attachment = attachmentMapper.selectOneById(id);
        if (attachment == null || !TenantContext.getTenantId().equals(attachment.getTenantId())) {
            throw new BusinessException("文件不存在或无访问权限");
        }
        return attachment;
    }

    private String normalizeFieldName(String fieldName, String bizType) {
        if (StringUtils.hasText(fieldName)) {
            return fieldName.trim();
        }
        return USER_AVATAR_BIZ_TYPE.equals(bizType) ? "avatar" : DEFAULT_ATTACHMENT_FIELD_NAME;
    }

    private String normalizePathPart(String value, String defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return value.trim().replaceAll("[^a-zA-Z0-9_-]", "-");
    }

    private String buildUniqueEntryName(String originalName, Long attachmentId, Set<String> usedNames) {
        String fileName = sanitizeFileName(originalName);
        if (usedNames.add(fileName)) {
            return fileName;
        }

        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex >= 0 ? fileName.substring(0, dotIndex) : fileName;
        String extension = dotIndex >= 0 ? fileName.substring(dotIndex) : "";
        String candidate = baseName + "-" + attachmentId + extension;
        usedNames.add(candidate);
        return candidate;
    }

    private String sanitizeFileName(String fileName) {
        String normalized = StringUtils.hasText(fileName) ? fileName.trim() : "attachment";
        normalized = normalized.replaceAll("[\\\\/:*?\"<>|]", "_");
        return normalized.isEmpty() ? "attachment" : normalized;
    }

    private MediaType resolveContentType(String mimeType) {
        if (!StringUtils.hasText(mimeType)) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(mimeType);
        } catch (Exception ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private void fillBizDisplayInfo(AttachmentFile attachment, AttachmentVO vo) {
        if (!USER_AVATAR_BIZ_TYPE.equals(attachment.getBizType()) || attachment.getBizId() == null) {
            return;
        }
        SysUser user = userMapper.selectOneById(attachment.getBizId());
        if (user == null || !TenantContext.getTenantId().equals(user.getTenantId())) {
            return;
        }
        vo.setBizName(user.getRealName());
        vo.setBizCode(user.getUsername());
    }

    private void clearUserAvatarReferenceIfNecessary(AttachmentFile attachment) {
        if (!USER_AVATAR_BIZ_TYPE.equals(attachment.getBizType()) || attachment.getBizId() == null) {
            return;
        }
        SysUser user = userMapper.selectOneById(attachment.getBizId());
        if (user == null || !TenantContext.getTenantId().equals(user.getTenantId())) {
            return;
        }
        if (!StringUtils.hasText(user.getAvatar()) || !user.getAvatar().trim().equals(attachment.getFileUrl())) {
            return;
        }
        user.setAvatar(null);
        userMapper.update(user);
    }
}
