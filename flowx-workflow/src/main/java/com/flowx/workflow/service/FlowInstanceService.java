package com.flowx.workflow.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.workflow.dto.FlowInstanceDTO;
import com.flowx.workflow.dto.FlowInstanceQueryDTO;
import com.flowx.workflow.vo.FlowInstanceVO;

/**
 * Flow instance service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface FlowInstanceService {

    /**
     * Start a new process instance
     *
     * @param dto instance start DTO
     * @return created instance ID
     */
    Long startProcess(FlowInstanceDTO dto);

    /**
     * Get instances with pagination
     *
     * @param queryDTO query parameters
     * @return paginated instance list
     */
    PageResult<FlowInstanceVO> getInstances(FlowInstanceQueryDTO queryDTO);

    /**
     * Get instance detail with task history
     *
     * @param instanceId instance ID
     * @return instance detail VO
     */
    FlowInstanceVO getInstanceDetail(Long instanceId);

    /**
     * Cancel a running instance
     *
     * @param instanceId instance ID
     */
    void cancelInstance(Long instanceId);
}
