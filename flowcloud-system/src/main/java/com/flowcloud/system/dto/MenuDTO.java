package com.flowcloud.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuDTO {

    private Long id;
    private Long parentId;

    @NotBlank(message = "权限编码不能为空")
    private String permCode;

    @NotBlank(message = "菜单名称不能为空")
    private String permName;

    @NotBlank(message = "菜单类型不能为空")
    private String permType;

    private String path;
    private String icon;
    private Integer sort;
    private Integer status;
}
