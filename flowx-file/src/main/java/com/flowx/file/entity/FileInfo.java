package com.flowx.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("file_info")
public class FileInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Stored file name (unique)
     */
    @TableField("file_name")
    private String fileName;

    /**
     * Original file name (from upload)
     */
    @TableField("original_name")
    private String originalName;

    /**
     * File storage path
     */
    @TableField("file_path")
    private String filePath;

    /**
     * File access URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * File size in bytes
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * File type category (image, document, video, audio, etc.)
     */
    @TableField("file_type")
    private String fileType;

    /**
     * File extension (e.g., pdf, jpg, png)
     */
    @TableField("file_extension")
    private String fileExtension;

    /**
     * Storage type: minio/local
     */
    @TableField("storage_type")
    private String storageType;

    /**
     * MD5 hash of file content
     */
    @TableField("md5_hash")
    private String md5Hash;

    /**
     * Upload user ID
     */
    @TableField("upload_user_id")
    private Long uploadUserId;

    /**
     * Download count
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * Status (0=deleted, 1=normal)
     */
    @TableField("status")
    private Integer status;

    /**
     * Thumbnail URL (for image/video files)
     */
    @TableField("thumbnail_url")
    private String thumbnailUrl;
}
