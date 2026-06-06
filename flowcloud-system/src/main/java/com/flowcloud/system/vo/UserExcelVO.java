package com.flowcloud.system.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserExcelVO {

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机")
    private String phone;

    @ExcelProperty("部门名称")
    private String deptName;

    @ExcelProperty("岗位")
    private String jobTitle;

    @ExcelProperty("在岗状态")
    private String workStatus;

    @ExcelProperty("角色编码")
    private String roleCodes;
}
