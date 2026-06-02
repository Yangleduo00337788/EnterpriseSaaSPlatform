package com.flowx.file.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.common.util.SecurityUtil;
import com.flowx.file.convert.FileInfoConvert;
import com.flowx.file.dto.FileQueryDTO;
import com.flowx.file.entity.FileInfo;
import com.flowx.file.mapper.FileInfoMapper;
import com.flowx.file.service.FileService;
import com.flowx.file.vo.FileInfoVO;
import com.flowx.infrastructure.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

/**
 * File service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileInfoMapper fileInfoMapper;
    private final FileInfoConvert fileInfoConvert;
    private final MinioStorageService minioStorageService;

    /**
     * Maximum file size: 100MB
     */
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    /**
     * Allowed file types
     */
    private static final String[] ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp",  // images
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv",  // documents
            "mp4", "avi", "mov", "wmv", "flv",  // videos
            "mp3", "wav", "aac", "flac"  // audio
    };

    @Override
    public FileInfoVO upload(MultipartFile file) {
        AssertUtil.notNull(file, "文件不能为空");

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BizException(ResultCodeEnum.FILE_SIZE_EXCEEDED);
        }

        // Get original filename and extension
        String originalName = file.getOriginalFilename();
        AssertUtil.notBlank(originalName, "文件名不能为空");

        String extension = getExtension(originalName);
        validateExtension(extension);

        // Generate unique filename
        String uniqueFileName = generateUniqueFileName(extension);

        // Calculate MD5 hash
        String md5Hash = calculateMd5(file);

        // Detect file type using Tika
        String fileType = detectFileType(file, extension);

        // Upload to MinIO
        String filePath = "files/" + java.time.LocalDate.now().toString().replace("-", "/") + "/" + uniqueFileName;
        String fileUrl;
        try {
            fileUrl = minioStorageService.uploadFile(filePath, file.getInputStream(), file.getContentType());
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", originalName, e);
            throw new BizException(ResultCodeEnum.FILE_UPLOAD_FAILED);
        }

        // Save file metadata to database
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(uniqueFileName);
        fileInfo.setOriginalName(originalName);
        fileInfo.setFilePath(filePath);
        fileInfo.setFileUrl(fileUrl);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(fileType);
        fileInfo.setFileExtension(extension);
        fileInfo.setStorageType("minio");
        fileInfo.setMd5Hash(md5Hash);
        fileInfo.setUploadUserId(SecurityUtil.getUserId());
        fileInfo.setDownloadCount(0);
        fileInfo.setStatus(1);

        fileInfoMapper.insert(fileInfo);
        log.info("Uploaded file: {} (ID: {})", originalName, fileInfo.getId());

        return fileInfoConvert.toVO(fileInfo);
    }

    @Override
    public InputStreamResource download(Long fileId) {
        AssertUtil.notNull(fileId, "文件ID不能为空");

        FileInfo fileInfo = fileInfoMapper.selectOneById(fileId);
        AssertUtil.notNull(fileInfo, ResultCodeEnum.NOT_FOUND.getCode(), "文件不存在");

        // Increment download count
        UpdateChain.of(FileInfo.class)
                .setRaw("download_count", "download_count + 1")
                .where(FileInfo::getId).eq(fileId)
                .update();

        // Get file from MinIO
        try {
            InputStream inputStream = minioStorageService.downloadFile(fileInfo.getFilePath());
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("Failed to download file from MinIO: {}", fileInfo.getFilePath(), e);
            throw new BizException("文件下载失败");
        }
    }

    @Override
    public void delete(Long fileId) {
        AssertUtil.notNull(fileId, "文件ID不能为空");

        FileInfo fileInfo = fileInfoMapper.selectOneById(fileId);
        AssertUtil.notNull(fileInfo, ResultCodeEnum.NOT_FOUND.getCode(), "文件不存在");

        // Soft delete in database
        fileInfoMapper.deleteById(fileId);

        // Optionally delete from MinIO (uncomment if needed)
        // try {
        //     minioStorageService.deleteFile(fileInfo.getFilePath());
        // } catch (Exception e) {
        //     log.warn("Failed to delete file from MinIO: {}", fileInfo.getFilePath(), e);
        // }

        log.info("Deleted file: {} (ID: {})", fileInfo.getOriginalName(), fileId);
    }

    @Override
    public FileInfoVO getFileInfo(Long fileId) {
        AssertUtil.notNull(fileId, "文件ID不能为空");

        FileInfo fileInfo = fileInfoMapper.selectOneById(fileId);
        AssertUtil.notNull(fileInfo, ResultCodeEnum.NOT_FOUND.getCode(), "文件不存在");

        return fileInfoConvert.toVO(fileInfo);
    }

    @Override
    public PageResult<FileInfoVO> listFiles(FileQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");

        QueryWrapper wrapper = buildFileQueryWrapper(queryDTO);

        Page<FileInfo> filePage = fileInfoMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);
        List<FileInfoVO> voList = fileInfoConvert.toVOList(filePage.getRecords());

        return PageResult.of(filePage.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public String getPresignedDownloadUrl(Long fileId) {
        AssertUtil.notNull(fileId, "文件ID不能为空");

        FileInfo fileInfo = fileInfoMapper.selectOneById(fileId);
        AssertUtil.notNull(fileInfo, ResultCodeEnum.NOT_FOUND.getCode(), "文件不存在");

        try {
            return minioStorageService.getPresignedUrl(fileInfo.getFilePath(), 60);
        } catch (Exception e) {
            log.error("Failed to get presigned URL for file: {}", fileInfo.getFilePath(), e);
            throw new BizException("获取下载链接失败");
        }
    }

    @Override
    public String preview(Long fileId) {
        AssertUtil.notNull(fileId, "文件ID不能为空");

        FileInfo fileInfo = fileInfoMapper.selectOneById(fileId);
        AssertUtil.notNull(fileInfo, ResultCodeEnum.NOT_FOUND.getCode(), "文件不存在");

        try {
            return minioStorageService.getPresignedUrl(fileInfo.getFilePath(), 30);
        } catch (Exception e) {
            log.error("Failed to get preview URL for file: {}", fileInfo.getFilePath(), e);
            throw new BizException("获取预览链接失败");
        }
    }

    @Override
    public void batchDelete(List<Long> fileIds) {
        AssertUtil.notNull(fileIds, "文件ID列表不能为空");

        for (Long fileId : fileIds) {
            try {
                delete(fileId);
            } catch (Exception e) {
                log.error("Failed to delete file: {}", fileId, e);
            }
        }
        log.info("Batch deleted {} files", fileIds.size());
    }

    /**
     * Get file extension from filename
     */
    private String getExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Validate file extension
     */
    private void validateExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            throw new BizException(ResultCodeEnum.FILE_TYPE_NOT_ALLOWED);
        }

        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return;
            }
        }
        throw new BizException(ResultCodeEnum.FILE_TYPE_NOT_ALLOWED);
    }

    /**
     * Generate unique filename
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * Calculate MD5 hash of file
     */
    private String calculateMd5(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(file.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("Failed to calculate MD5 hash", e);
            return null;
        }
    }

    /**
     * Detect file type using Tika
     */
    private String detectFileType(MultipartFile file, String extension) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());

            if (mimeType.startsWith("image/")) {
                return "image";
            } else if (mimeType.startsWith("video/")) {
                return "video";
            } else if (mimeType.startsWith("audio/")) {
                return "audio";
            } else if (mimeType.equals("application/pdf") ||
                    mimeType.startsWith("application/msword") ||
                    mimeType.startsWith("application/vnd.openxmlformats") ||
                    mimeType.equals("text/plain") ||
                    mimeType.equals("text/csv")) {
                return "document";
            } else {
                return "other";
            }
        } catch (Exception e) {
            log.warn("Failed to detect file type, using extension: {}", extension, e);
            return detectFileTypeByExtension(extension);
        }
    }

    /**
     * Detect file type by extension (fallback)
     */
    private String detectFileTypeByExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
                return "image";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
            case "flv":
                return "video";
            case "mp3":
            case "wav":
            case "aac":
            case "flac":
                return "audio";
            case "pdf":
            case "doc":
            case "docx":
            case "xls":
            case "xlsx":
            case "ppt":
            case "pptx":
            case "txt":
            case "csv":
                return "document";
            default:
                return "other";
        }
    }

    /**
     * Build query wrapper from FileQueryDTO
     */
    private QueryWrapper buildFileQueryWrapper(FileQueryDTO queryDTO) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (StringUtils.hasText(queryDTO.getFileName())) {
            wrapper.like("file_name", queryDTO.getFileName());
        }
        if (StringUtils.hasText(queryDTO.getFileType())) {
            wrapper.eq("file_type", queryDTO.getFileType());
        }
        if (queryDTO.getUploadUserId() != null) {
            wrapper.eq("upload_user_id", queryDTO.getUploadUserId());
        }

        wrapper.eq("status", 1); // only active files
        wrapper.orderBy("create_time", false);
        return wrapper;
    }
}
