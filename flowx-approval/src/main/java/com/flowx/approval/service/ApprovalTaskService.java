package com.flowx.approval.service;

import com.flowx.approval.dto.ApprovalQueryDTO;
import com.flowx.approval.dto.ApprovalTaskCompleteDTO;
import com.flowx.approval.vo.ApprovalTaskVO;
import com.flowx.common.core.result.PageResult;

/**
 * Approval task service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface ApprovalTaskService {

    PageResult<ApprovalTaskVO> getTodoTasks(ApprovalQueryDTO queryDTO);

    PageResult<ApprovalTaskVO> getDoneTasks(ApprovalQueryDTO queryDTO);

    ApprovalTaskVO getTaskById(Long id);

    void approve(Long id, ApprovalTaskCompleteDTO dto);

    void reject(Long id, ApprovalTaskCompleteDTO dto);

    void delegate(Long id, Long targetUserId);
}