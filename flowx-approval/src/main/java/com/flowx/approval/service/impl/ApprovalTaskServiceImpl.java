package com.flowx.approval.service.impl;

import com.flowx.approval.dto.ApprovalQueryDTO;
import com.flowx.approval.dto.ApprovalTaskCompleteDTO;
import com.flowx.approval.service.ApprovalTaskService;
import com.flowx.approval.vo.ApprovalTaskVO;
import com.flowx.common.core.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApprovalTaskServiceImpl implements ApprovalTaskService {

    @Override
    public PageResult<ApprovalTaskVO> getTodoTasks(ApprovalQueryDTO queryDTO) {
        log.info("Get todo tasks: pageNum={}, pageSize={}", queryDTO.getPageNum(), queryDTO.getPageSize());
        return PageResult.empty();
    }

    @Override
    public PageResult<ApprovalTaskVO> getDoneTasks(ApprovalQueryDTO queryDTO) {
        log.info("Get done tasks: pageNum={}, pageSize={}", queryDTO.getPageNum(), queryDTO.getPageSize());
        return PageResult.empty();
    }

    @Override
    public ApprovalTaskVO getTaskById(Long id) {
        log.info("Get task by id: {}", id);
        return null;
    }

    @Override
    public void approve(Long id, ApprovalTaskCompleteDTO dto) {
        log.info("Approve task: id={}, comment={}", id, dto.getComment());
    }

    @Override
    public void reject(Long id, ApprovalTaskCompleteDTO dto) {
        log.info("Reject task: id={}, comment={}", id, dto.getComment());
    }

    @Override
    public void delegate(Long id, Long targetUserId) {
        log.info("Delegate task: id={}, targetUserId={}", id, targetUserId);
    }
}