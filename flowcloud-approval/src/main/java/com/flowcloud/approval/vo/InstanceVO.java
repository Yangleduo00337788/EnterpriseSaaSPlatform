package com.flowcloud.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InstanceVO {

    private Long id;
    private String instanceNo;
    private Long templateId;
    private String templateName;
    private String category;
    private String title;
    private Long applicantId;
    private String applicantName;
    private String formData;
    private String status;
    private String statusLabel;
    private Integer currentNode;
    private LocalDateTime submitTime;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private List<RecordVO> records;
    private List<TaskVO> tasks;
}
