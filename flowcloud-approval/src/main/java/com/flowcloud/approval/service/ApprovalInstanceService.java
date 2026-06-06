package com.flowcloud.approval.service;

import com.flowcloud.approval.dto.SubmitApprovalDTO;
import com.flowcloud.approval.vo.InstanceVO;
import com.flowcloud.common.result.PageResult;

public interface ApprovalInstanceService {

    PageResult<InstanceVO> pageMySubmissions(String status, int pageNum, int pageSize);

    PageResult<InstanceVO> pageAll(String status, String category, int pageNum, int pageSize);

    InstanceVO getDetail(Long id);

    Long submit(SubmitApprovalDTO dto);

    void cancel(Long id);
}
