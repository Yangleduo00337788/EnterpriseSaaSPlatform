package com.flowx.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.util.AssertUtil;
import com.flowx.system.entity.SysLoginLog;
import com.flowx.system.mapper.SysLoginLogMapper;
import com.flowx.system.service.LoginLogService;
import com.flowx.system.vo.LoginLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Login log service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    @Override
    public void createLog(SysLoginLog log) {
        AssertUtil.notNull(log, "登录日志不能为空");
        loginLogMapper.insert(log);
    }

    @Override
    public PageResult<LoginLogVO> listLogs(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.orderBy("login_time", false);

        Page<SysLoginLog> logPage = loginLogMapper.paginate(pageNum, pageSize, wrapper);
        List<LoginLogVO> voList = logPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(logPage.getTotalRow(), voList, pageNum, pageSize);
    }

    /**
     * Convert SysLoginLog entity to LoginLogVO
     */
    private LoginLogVO convertToVO(SysLoginLog entity) {
        LoginLogVO vo = new LoginLogVO();
        vo.setId(entity.getId());
        vo.setUsername(entity.getUsername());
        vo.setLoginIp(entity.getLoginIp());
        vo.setLoginLocation(entity.getLoginLocation());
        vo.setBrowser(entity.getBrowser());
        vo.setOs(entity.getOs());
        vo.setStatus(entity.getStatus());
        vo.setMsg(entity.getMsg());
        vo.setLoginTime(entity.getLoginTime());
        return vo;
    }
}
