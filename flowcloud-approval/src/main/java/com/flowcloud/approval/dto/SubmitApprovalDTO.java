package com.flowcloud.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitApprovalDTO {

    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String formData;
}
