package com.flowcloud.system.service;

import com.flowcloud.system.dto.ChangePasswordDTO;
import com.flowcloud.system.dto.LoginDTO;
import com.flowcloud.system.dto.ProfileUpdateDTO;
import com.flowcloud.system.dto.RegisterDTO;
import com.flowcloud.system.vo.LoginVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    LoginVO getCurrentUser();

    LoginVO updateCurrentProfile(ProfileUpdateDTO dto);

    String uploadCurrentUserAvatar(MultipartFile file) throws IOException;

    void changeCurrentPassword(ChangePasswordDTO dto);
}
