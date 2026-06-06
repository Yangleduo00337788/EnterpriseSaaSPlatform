package com.flowcloud.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class PermissionVO {

    private Long id;
    private Long parentId;
    private String permCode;
    private String permName;
    private String permType;
    private String path;
    private Integer sort;
    private List<PermissionVO> children;
}
