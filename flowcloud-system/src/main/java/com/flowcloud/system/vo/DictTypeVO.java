package com.flowcloud.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class DictTypeVO {

    private Long id;
    private String dictCode;
    private String dictName;
    private Integer status;
    private String remark;
    private List<DictDataVO> items;
}
