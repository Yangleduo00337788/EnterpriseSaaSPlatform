package com.flowx.system.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.system.entity.SysOperationLog;
import com.flowx.system.vo.OperationLogVO;

/**
 * Operation log service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface OperationLogService {

    /**
     * Create operation log
     *
     * @param log operation log entity
     */
    void createLog(SysOperationLog log);

    /**
     * List operation logs with pagination
     *
     * @param pageNum  page number
     * @param pageSize page size
     * @return paginated operation log list
     */
    PageResult<OperationLogVO> listLogs(Integer pageNum, Integer pageSize);
}
