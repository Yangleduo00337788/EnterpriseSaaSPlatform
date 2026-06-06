package com.flowcloud.system.vo;

import lombok.Data;

@Data
public class DictDataVO {

    private Long id;
    private Long dictTypeId;
    private String dictLabel;
    private String dictValue;
    private Integer sort;
    private Integer status;
    private String remark;
}
