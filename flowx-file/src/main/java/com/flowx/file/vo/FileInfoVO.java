package com.flowx.file.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * File information view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FileInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * File ID
     */
    private Long id;

    /**
     * Stored file name
     */
    private String fileName;

    /**
     * Original file name
     */
    private String originalName;

    /**
     * File storage path
     */
    private String filePath;

    /**
     * File access URL
     */
    private String fileUrl;

    /**
     * File size in bytes
     */
    private Long fileSize;

    /**
     * File type category
     */
    private String fileType;

    /**
     * File extension
     */
    private String fileExtension;

    /**
     * Storage type
     */
    private String storageType;

    /**
     * MD5 hash
     */
    private String md5Hash;

    /**
     * Upload user ID
     */
    private Long uploadUserId;

    /**
     * Download count
     */
    private Integer downloadCount;

    /**
     * Status
     */
    private Integer status;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
