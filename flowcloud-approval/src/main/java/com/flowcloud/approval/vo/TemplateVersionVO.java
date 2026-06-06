package com.flowcloud.approval.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TemplateVersionVO {
    private Long id;
    private Long templateId;
    private Integer version;
    private String flowConfig;
    private String formSchema;
    private String remark;
    private LocalDateTime createTime;
}