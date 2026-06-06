package com.flowcloud.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskVO {

    private Long id;
    private Long instanceId;
    private String instanceNo;
    private String title;
    private Integer nodeIndex;
    private String nodeName;
    private Long approverId;
    private String approverName;
    private String status;
    private String statusLabel;
    private String comment;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;
}
