package com.flowcloud.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateDTO {

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String phone;

    private String email;

    private String avatar;
}
