package com.flowx.file.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * File information entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("file_info")
public class FileInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Stored file name (unique)
     */
    @Column("file_name")
    private String fileName;

    /**
     * Original file name (from upload)
     */
    @Column("original_name")
    private String originalName;

    /**
     * File storage path
     */
    @Column("file_path")
    private String filePath;

    /**
     * File access URL
     */
    @Column("file_url")
    private String fileUrl;

    /**
     * File size in bytes
     */
    @Column("file_size")
    private Long fileSize;

    /**
     * File type category (image, document, video, audio, etc.)
     */
    @Column("file_type")
    private String fileType;

    /**
     * File extension (e.g., pdf, jpg, png)
     */
    @Column("file_extension")
    private String fileExtension;

    /**
     * Storage type: minio/local
     */
    @Column("storage_type")
    private String storageType;

    /**
     * MD5 hash of file content
     */
    @Column("md5_hash")
    private String md5Hash;

    /**
     * Upload user ID
     */
    @Column("upload_user_id")
    private Long uploadUserId;

    /**
     * Download count
     */
    @Column("download_count")
    private Integer downloadCount;

    /**
     * Status (0=deleted, 1=normal)
     */
    @Column("status")
    private Integer status;

    /**
     * Thumbnail URL (for image/video files)
     */
    @Column("thumbnail_url")
    private String thumbnailUrl;
}
