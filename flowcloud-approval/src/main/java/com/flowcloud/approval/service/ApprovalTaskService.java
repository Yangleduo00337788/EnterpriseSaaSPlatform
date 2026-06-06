package com.flowcloud.approval.service;

import com.flowcloud.approval.dto.TaskCompleteDTO;
import com.flowcloud.approval.vo.TaskVO;
import com.flowcloud.common.result.PageResult;

public interface ApprovalTaskService {

    PageResult<TaskVO> pagePendingTasks(int pageNum, int pageSize);

    PageResult<TaskVO> pageHandledTasks(int pageNum, int pageSize);

    void complete(TaskCompleteDTO dto);

    void remind(Long taskId);

    void remindAuto(Long taskId);
}
