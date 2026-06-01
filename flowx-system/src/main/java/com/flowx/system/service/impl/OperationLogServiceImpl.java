package com.flowx.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.util.AssertUtil;
import com.flowx.system.entity.SysOperationLog;
import com.flowx.system.mapper.SysOperationLogMapper;
import com.flowx.system.service.OperationLogService;
import com.flowx.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Operation log service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final SysOperationLogMapper operationLogMapper;

    @Override
    public void createLog(SysOperationLog log) {
        AssertUtil.notNull(log, "操作日志不能为空");
        operationLogMapper.insert(log);
    }

    @Override
    public PageResult<OperationLogVO> listLogs(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysOperationLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("oper_time");

        Page<SysOperationLog> logPage = operationLogMapper.selectPage(page, wrapper);
        List<OperationLogVO> voList = logPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(logPage.getTotal(), voList, pageNum, pageSize);
    }

    /**
     * Convert SysOperationLog entity to OperationLogVO
     */
    private OperationLogVO convertToVO(SysOperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setBusinessType(entity.getBusinessType());
        vo.setMethod(entity.getMethod());
        vo.setRequestMethod(entity.getRequestMethod());
        vo.setRequestUrl(entity.getRequestUrl());
        vo.setRequestParam(entity.getRequestParam());
        vo.setResponseResult(entity.getResponseResult());
        vo.setOperatorType(entity.getOperatorType());
        vo.setOperUserId(entity.getOperUserId());
        vo.setOperUserName(entity.getOperUserName());
        vo.setOperIp(entity.getOperIp());
        vo.setOperLocation(entity.getOperLocation());
        vo.setStatus(entity.getStatus());
        vo.setErrorMsg(entity.getErrorMsg());
        vo.setOperTime(entity.getOperTime());
        vo.setCostTime(entity.getCostTime());
        return vo;
    }
}
