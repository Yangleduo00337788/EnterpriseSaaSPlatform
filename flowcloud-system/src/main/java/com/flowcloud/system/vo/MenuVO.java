package com.flowcloud.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuVO {

    private Long id;
    private Long parentId;
    private String permCode;
    private String permName;
    private String permType;
    private String path;
    private String icon;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private List<MenuVO> children = new ArrayList<>();
}
