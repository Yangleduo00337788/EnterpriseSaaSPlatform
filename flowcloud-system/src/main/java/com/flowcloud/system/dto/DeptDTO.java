package com.flowcloud.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeptDTO {

    private Long id;
    private Long parentId;

    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    private String leader;
    private Long leaderUserId;
    private Integer sort;
    private Integer status;
}