package com.flowcloud.system.dto;

import lombok.Data;

@Data
public class PositionDTO {
    private Long id;
    private String positionCode;
    private String positionName;
    private Long deptId;
    private Integer sort;
    private Integer status;
    private String remark;
}