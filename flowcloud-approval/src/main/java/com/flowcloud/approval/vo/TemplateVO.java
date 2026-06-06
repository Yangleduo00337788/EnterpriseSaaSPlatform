package com.flowcloud.approval.vo;

import com.flowcloud.approval.dto.FlowNodeDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TemplateVO {

    private Long id;
    private String templateCode;
    private String templateName;
    private String category;
    private String description;
    private String formSchema;
    private List<FlowNodeDTO> flowNodes;
    private Integer status;
    private String statusLabel;
    private Integer sort;
    private Integer pubVersion;
    private LocalDateTime createTime;
}
