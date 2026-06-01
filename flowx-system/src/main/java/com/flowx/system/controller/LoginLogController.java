package com.flowx.system.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.system.service.LoginLogService;
import com.flowx.system.vo.LoginLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Login log controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    /**
     * List login logs with pagination
     *
     * @param pageNum  page number
     * @param pageSize page size
     * @return paginated login log list
     */
    @GetMapping("/list")
    public R<PageResult<LoginLogVO>> listLogs(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<LoginLogVO> result = loginLogService.listLogs(pageNum, pageSize);
        return R.ok(result);
    }
}
