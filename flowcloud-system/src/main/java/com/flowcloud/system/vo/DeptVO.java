package com.flowcloud.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeptVO {

    private Long id;
    private Long parentId;
    private String deptName;
    private String leader;
    private Long leaderUserId;
    private String ancestors;
    private Integer sort;
    private Integer status;
    private List<DeptVO> children = new ArrayList<>();
}