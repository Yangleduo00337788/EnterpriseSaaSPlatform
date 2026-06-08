package com.flowcloud.approval.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AttachmentBatchOperationDTO {

    @NotEmpty(message = "文件ID不能为空")
    private List<Long> ids;
}
