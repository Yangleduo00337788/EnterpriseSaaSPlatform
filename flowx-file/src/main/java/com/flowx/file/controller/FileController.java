package com.flowx.file.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.file.dto.FileQueryDTO;
import com.flowx.file.service.FileService;
import com.flowx.file.vo.FileInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * File management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * Upload a file
     *
     * @param file multipart file
     * @return file info VO
     */
    @PostMapping("/upload")
    public R<FileInfoVO> upload(@RequestParam("file") MultipartFile file) {
        FileInfoVO vo = fileService.upload(file);
        return R.ok(vo);
    }

    /**
     * Get file info by ID
     *
     * @param id file ID
     * @return file info VO
     */
    @GetMapping("/{id}")
    public R<FileInfoVO> getFileInfo(@PathVariable("id") Long id) {
        FileInfoVO vo = fileService.getFileInfo(id);
        return R.ok(vo);
    }

    /**
     * List files with pagination
     *
     * @param queryDTO query parameters
     * @return paginated file list
     */
    @GetMapping("/list")
    public R<PageResult<FileInfoVO>> listFiles(FileQueryDTO queryDTO) {
        PageResult<FileInfoVO> result = fileService.listFiles(queryDTO);
        return R.ok(result);
    }

    /**
     * Delete files by IDs (comma-separated)
     *
     * @param ids file IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteFiles(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            fileService.delete(id);
        }
        return R.ok();
    }

    /**
     * Download a file
     *
     * @param id file ID
     * @return file input stream
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") Long id) {
        FileInfoVO fileInfo = fileService.getFileInfo(id);
        InputStreamResource resource = fileService.download(id);

        String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileInfo.getFileSize())
                .body(resource);
    }

    /**
     * Get preview URL for a file
     *
     * @param id file ID
     * @return presigned preview URL
     */
    @GetMapping("/{id}/preview")
    public R<String> preview(@PathVariable("id") Long id) {
        String url = fileService.preview(id);
        return R.ok(url);
    }

    /**
     * Batch delete files
     *
     * @param fileIds list of file IDs
     * @return success response
     */
    @PostMapping("/batch-delete")
    public R<Void> batchDelete(@RequestBody List<Long> fileIds) {
        fileService.batchDelete(fileIds);
        return R.ok();
    }
}