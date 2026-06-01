package com.flowx.system.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.system.entity.SysLoginLog;
import com.flowx.system.vo.LoginLogVO;

/**
 * Login log service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface LoginLogService {

    /**
     * Create login log
     *
     * @param log login log entity
     */
    void createLog(SysLoginLog log);

    /**
     * List login logs with pagination
     *
     * @param pageNum  page number
     * @param pageSize page size
     * @return paginated login log list
     */
    PageResult<LoginLogVO> listLogs(Integer pageNum, Integer pageSize);
}
