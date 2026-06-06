package com.flowcloud.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordVO {

    private Integer nodeIndex;
    private String nodeName;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String comment;
    private LocalDateTime createTime;
}
