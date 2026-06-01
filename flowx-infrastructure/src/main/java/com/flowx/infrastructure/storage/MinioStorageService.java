package com.flowx.infrastructure.storage;

import com.flowx.infrastructure.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MinIO object storage service.
 * Provides file upload, download, delete, and presigned URL operations.
 */
@Slf4j
@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public MinioStorageService(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    /**
     * Upload file to MinIO.
     *
     * @param fileName    the object name
     * @param inputStream the file input stream
     * @param contentType the content type
     * @return the file access URL
     */
    public String uploadFile(String fileName, InputStream inputStream, String contentType) {
        try {
            ensureBucketExists();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(inputStream, -1, 10485760) // 10MB part size
                    .contentType(contentType)
                    .build());

            String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
            log.info("File uploaded successfully: {}", fileName);
            return url;
        } catch (Exception e) {
            log.error("Failed to upload file: {}", fileName, e);
            throw new RuntimeException("Failed to upload file: " + fileName, e);
        }
    }

    /**
     * Download file from MinIO.
     *
     * @param fileName the object name
     * @return the file input stream
     */
    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("Failed to download file: {}", fileName, e);
            throw new RuntimeException("Failed to download file: " + fileName, e);
        }
    }

    /**
     * Get presigned URL for file access.
     *
     * @param fileName the object name
     * @param expiry   expiry time in minutes
     * @return the presigned URL
     */
    public String getPresignedUrl(String fileName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .expiry(expiry, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get presigned URL: {}", fileName, e);
            throw new RuntimeException("Failed to get presigned URL: " + fileName, e);
        }
    }

    /**
     * Delete file from MinIO.
     *
     * @param fileName the object name
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build());
            log.info("File deleted successfully: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileName, e);
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    /**
     * List files with prefix.
     *
     * @param prefix the file name prefix
     * @return list of file names
     */
    public List<String> listFiles(String prefix) {
        try {
            List<String> fileNames = new ArrayList<>();

            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .prefix(prefix)
                    .recursive(true)
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(args);
            for (Result<Item> result : results) {
                fileNames.add(result.get().objectName());
            }

            log.info("Listed {} files with prefix: {}", fileNames.size(), prefix);
            return fileNames;
        } catch (Exception e) {
            log.error("Failed to list files with prefix: {}", prefix, e);
            throw new RuntimeException("Failed to list files with prefix: " + prefix, e);
        }
    }

    /**
     * Check if file exists.
     *
     * @param fileName the object name
     * @return true if file exists
     */
    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ensure the configured bucket exists, create if not.
     */
    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioConfig.getBucketName())
                .build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build());
            log.info("Created bucket: {}", minioConfig.getBucketName());
        }
    }
}
