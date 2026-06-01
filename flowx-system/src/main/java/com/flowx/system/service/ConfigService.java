package com.flowx.system.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.system.dto.ConfigDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.vo.ConfigVO;

/**
 * System config service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface ConfigService {

    /**
     * Get config by ID
     *
     * @param configId config ID
     * @return config VO
     */
    ConfigVO getConfigById(Long configId);

    /**
     * Get config value by key
     *
     * @param configKey config key
     * @return config value
     */
    String getConfigByKey(String configKey);

    /**
     * Create new config
     *
     * @param dto config creation DTO
     * @return created config ID
     */
    Long createConfig(ConfigDTO dto);

    /**
     * Update existing config
     *
     * @param configId config ID
     * @param dto      config update DTO
     */
    void updateConfig(Long configId, ConfigDTO dto);

    /**
     * Delete config (soft delete)
     *
     * @param configId config ID
     */
    void deleteConfig(Long configId);

    /**
     * List configs with pagination
     *
     * @param queryDTO query parameters
     * @return paginated config list
     */
    PageResult<ConfigVO> listConfigs(TenantQueryDTO queryDTO);
}
