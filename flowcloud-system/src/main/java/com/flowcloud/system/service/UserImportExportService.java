package com.flowcloud.system.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UserImportExportService {

    void exportUsers(HttpServletResponse response) throws IOException;

    Map<String, Object> importUsers(MultipartFile file) throws IOException;
}
