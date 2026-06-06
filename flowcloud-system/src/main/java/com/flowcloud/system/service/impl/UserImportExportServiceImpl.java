package com.flowcloud.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.UserDTO;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.entity.SysRole;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.flowcloud.system.mapper.SysRoleMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.service.SysUserService;
import com.flowcloud.system.service.UserImportExportService;
import com.flowcloud.system.vo.UserExcelVO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserImportExportServiceImpl implements UserImportExportService {

    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserService userService;

    @Override
    public void exportUsers(HttpServletResponse response) throws IOException {
        List<SysUser> users = userMapper.selectListByQuery(
                QueryWrapper.create().where(SysUser::getTenantId).eq(TenantContext.getTenantId()));
        Map<Long, String> deptNames = deptMapper.selectListByQuery(
                        QueryWrapper.create().where(SysDept::getTenantId).eq(TenantContext.getTenantId()))
                .stream().collect(Collectors.toMap(SysDept::getId, SysDept::getDeptName, (a, b) -> a));
        List<UserExcelVO> rows = new ArrayList<>();
        for (SysUser user : users) {
            UserExcelVO row = new UserExcelVO();
            row.setUsername(user.getUsername());
            row.setRealName(user.getRealName());
            row.setEmail(user.getEmail());
            row.setPhone(user.getPhone());
            row.setDeptName(user.getDeptId() != null ? deptNames.get(user.getDeptId()) : null);
            row.setJobTitle(user.getJobTitle());
            row.setWorkStatus(user.getWorkStatus());
            rows.add(row);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("员工列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), UserExcelVO.class).sheet("员工").doWrite(rows);
    }

    @Override
    public Map<String, Object> importUsers(MultipartFile file) throws IOException {
        List<UserExcelVO> rows = EasyExcel.read(file.getInputStream())
                .head(UserExcelVO.class).sheet().doReadSync();
        Map<String, Long> deptMap = deptMapper.selectListByQuery(
                        QueryWrapper.create().where(SysDept::getTenantId).eq(TenantContext.getTenantId()))
                .stream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getId, (a, b) -> a));
        Map<String, Long> roleMap = roleMapper.selectListByQuery(
                        QueryWrapper.create().where(SysRole::getTenantId).eq(TenantContext.getTenantId()))
                .stream().collect(Collectors.toMap(SysRole::getRoleCode, SysRole::getId, (a, b) -> a));

        int success = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            UserExcelVO row = rows.get(i);
            int line = i + 2;
            if (!StringUtils.hasText(row.getUsername()) || !StringUtils.hasText(row.getRealName())) {
                errors.add("第" + line + "行：用户名和姓名不能为空");
                continue;
            }
            try {
                UserDTO dto = new UserDTO();
                dto.setUsername(row.getUsername().trim());
                dto.setRealName(row.getRealName().trim());
                dto.setEmail(row.getEmail());
                dto.setPhone(row.getPhone());
                dto.setJobTitle(row.getJobTitle());
                dto.setWorkStatus(StringUtils.hasText(row.getWorkStatus()) ? row.getWorkStatus() : "active");
                if (StringUtils.hasText(row.getDeptName()) && deptMap.containsKey(row.getDeptName())) {
                    dto.setDeptId(deptMap.get(row.getDeptName()));
                }
                if (StringUtils.hasText(row.getRoleCodes())) {
                    List<Long> roleIds = Arrays.stream(row.getRoleCodes().split("[,，]"))
                            .map(String::trim)
                            .filter(StringUtils::hasText)
                            .map(roleMap::get)
                            .filter(id -> id != null)
                            .toList();
                    dto.setRoleIds(roleIds);
                }
                dto.setPassword("123456");
                userService.createUser(dto);
                success++;
            } catch (Exception e) {
                errors.add("第" + line + "行：" + e.getMessage());
            }
        }
        if (success == 0 && !errors.isEmpty()) {
            throw new BusinessException("导入失败：" + String.join("；", errors.subList(0, Math.min(errors.size(), 5))));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", success);
        result.put("failCount", errors.size());
        result.put("errors", errors);
        return result;
    }
}
