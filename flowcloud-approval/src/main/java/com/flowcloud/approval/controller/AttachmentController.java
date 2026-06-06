package com.flowcloud.approval.controller;

import com.flowcloud.approval.entity.AttachmentFile;
import com.flowcloud.approval.mapper.AttachmentFileMapper;
import com.flowcloud.approval.vo.AttachmentVO;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.Result;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.SysUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentFileMapper attachmentMapper;
    private final SysUserMapper userMapper;

    @Value("${flowcloud.upload.path:${user.home}/flowcloud-uploads}")
    private String uploadPath;

    @Value("${flowcloud.upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @PostMapping("/upload")
    public Result<AttachmentVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId,
            @RequestParam(required = false) String fieldName) throws IOException {

        if (file.isEmpty()) throw new BusinessException("请选择要上传的文件");

        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
        String fileKey = datePath + "/" + storedName;

        File destDir = new File(uploadPath + "/" + datePath);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new BusinessException("存储目录创建失败");
        }
        file.transferTo(new File(destDir, storedName));

        SysUser uploader = userMapper.selectOneById(TenantContext.getUserId());

        AttachmentFile att = new AttachmentFile();
        att.setTenantId(TenantContext.getTenantId());
        att.setBizType(bizType);
        att.setBizId(bizId);
        att.setFieldName(fieldName);
        att.setOriginalName(file.getOriginalFilename());
        att.setFileKey(fileKey);
        att.setFileUrl(baseUrl + "/" + fileKey);
        att.setFileSize(file.getSize());
        att.setMimeType(file.getContentType());
        att.setUploaderId(TenantContext.getUserId());
        att.setUploaderName(uploader != null ? uploader.getRealName() : null);
        att.setCreateTime(LocalDateTime.now());
        attachmentMapper.insert(att);

        return Result.ok(toVO(att));
    }

    @GetMapping
    public Result<List<AttachmentVO>> list(
            @RequestParam String bizType, @RequestParam Long bizId,
            @RequestParam(required = false) String fieldName) {
        QueryWrapper query = QueryWrapper.create()
                .where(AttachmentFile::getBizType).eq(bizType)
                .and(AttachmentFile::getBizId).eq(bizId);
        if (fieldName != null) {
            query.and(AttachmentFile::getFieldName).eq(fieldName);
        }
        query.orderBy(AttachmentFile::getCreateTime, true);
        List<AttachmentVO> vos = attachmentMapper.selectListByQuery(query).stream().map(this::toVO).toList();
        return Result.ok(vos);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AttachmentFile att = attachmentMapper.selectOneById(id);
        if (att == null) return Result.ok();
        File file = new File(uploadPath + "/" + att.getFileKey());
        if (file.exists()) file.delete();
        attachmentMapper.deleteById(id);
        return Result.ok();
    }

    private AttachmentVO toVO(AttachmentFile att) {
        AttachmentVO vo = new AttachmentVO();
        vo.setId(att.getId());
        vo.setBizType(att.getBizType());
        vo.setBizId(att.getBizId());
        vo.setFieldName(att.getFieldName());
        vo.setOriginalName(att.getOriginalName());
        vo.setFileUrl(att.getFileUrl());
        vo.setFileSize(att.getFileSize());
        vo.setMimeType(att.getMimeType());
        vo.setUploaderName(att.getUploaderName());
        vo.setCreateTime(att.getCreateTime());
        return vo;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}