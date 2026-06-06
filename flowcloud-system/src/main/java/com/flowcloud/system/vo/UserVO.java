package com.flowcloud.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Long deptId;
    private String deptName;
    private Long managerId;
    private String managerName;
    private String jobTitle;
    private String workStatus;
    private Integer status;
    private List<Long> roleIds;
    private List<String> roleNames;
    private LocalDateTime createTime;
}
