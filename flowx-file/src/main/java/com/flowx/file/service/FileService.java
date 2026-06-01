package com.flowx.file.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.file.dto.FileQueryDTO;
import com.flowx.file.vo.FileInfoVO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * File service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface FileService {

    /**
     * Upload a file
     *
     * @param file multipart file
     * @return file info VO
     */
    FileInfoVO upload(MultipartFile file);

    /**
     * Download a file
     *
     * @param fileId file ID
     * @return input stream resource
     */
    InputStreamResource download(Long fileId);

    /**
     * Delete a file (soft delete)
     *
     * @param fileId file ID
     */
    void delete(Long fileId);

    /**
     * Get file info by ID
     *
     * @param fileId file ID
     * @return file info VO
     */
    FileInfoVO getFileInfo(Long fileId);

    /**
     * List files with pagination
     *
     * @param queryDTO query parameters
     * @return paginated file list
     */
    PageResult<FileInfoVO> listFiles(FileQueryDTO queryDTO);

    /**
     * Get presigned download URL
     *
     * @param fileId file ID
     * @return presigned URL string
     */
    String getPresignedDownloadUrl(Long fileId);

    /**
     * Get preview URL for a file
     *
     * @param fileId file ID
     * @return presigned URL for preview
     */
    String preview(Long fileId);

    /**
     * Batch delete files
     *
     * @param fileIds list of file IDs
     */
    void batchDelete(List<Long> fileIds);
}
