package com.flowcloud.system.service;

import com.flowcloud.system.dto.LoginDTO;
import com.flowcloud.system.dto.RegisterDTO;
import com.flowcloud.system.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    LoginVO getCurrentUser();
}
