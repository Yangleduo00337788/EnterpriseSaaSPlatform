package com.flowx.system.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.system.service.OperationLogService;
import com.flowx.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Operation log controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * List operation logs with pagination
     *
     * @param pageNum  page number
     * @param pageSize page size
     * @return paginated operation log list
     */
    @GetMapping("/list")
    public R<PageResult<OperationLogVO>> listLogs(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<OperationLogVO> result = operationLogService.listLogs(pageNum, pageSize);
        return R.ok(result);
    }
}
