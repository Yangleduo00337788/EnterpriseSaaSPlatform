package com.flowcloud.system.vo;

import lombok.Data;

@Data
public class PositionVO {
    private Long id;
    private String positionCode;
    private String positionName;
    private Long deptId;
    private String deptName;
    private Integer sort;
    private Integer status;
    private String remark;
}