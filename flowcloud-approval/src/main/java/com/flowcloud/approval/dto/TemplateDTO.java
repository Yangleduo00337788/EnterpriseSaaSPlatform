package com.flowcloud.approval.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TemplateDTO {

    private Long id;

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String description;
    private String formSchema;
    private List<FlowNodeDTO> flowNodes;
    private Integer status;
    private Integer sort;
}
