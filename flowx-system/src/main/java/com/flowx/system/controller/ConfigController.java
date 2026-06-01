package com.flowx.system.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.system.dto.ConfigDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.service.ConfigService;
import com.flowx.system.vo.ConfigVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * System config management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    /**
     * Get config by ID
     *
     * @param id config ID
     * @return config VO
     */
    @GetMapping("/{id}")
    public R<ConfigVO> getConfigById(@PathVariable("id") Long id) {
        ConfigVO vo = configService.getConfigById(id);
        return R.ok(vo);
    }

    /**
     * Get config value by key
     *
     * @param configKey config key
     * @return config value
     */
    @GetMapping("/key/{configKey}")
    public R<String> getConfigByKey(@PathVariable("configKey") String configKey) {
        String value = configService.getConfigByKey(configKey);
        return R.ok(value);
    }

    /**
     * List configs with pagination
     *
     * @param queryDTO query parameters
     * @return paginated config list
     */
    @GetMapping("/list")
    public R<PageResult<ConfigVO>> listConfigs(TenantQueryDTO queryDTO) {
        PageResult<ConfigVO> result = configService.listConfigs(queryDTO);
        return R.ok(result);
    }

    /**
     * Create new config
     *
     * @param dto config creation DTO
     * @return created config ID
     */
    @PostMapping
    public R<Long> createConfig(@Valid @RequestBody ConfigDTO dto) {
        Long configId = configService.createConfig(dto);
        return R.ok(configId);
    }

    /**
     * Update existing config
     *
     * @param id  config ID
     * @param dto config update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateConfig(@PathVariable("id") Long id, @Valid @RequestBody ConfigDTO dto) {
        configService.updateConfig(id, dto);
        return R.ok();
    }

    /**
     * Delete config
     *
     * @param id config ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable("id") Long id) {
        configService.deleteConfig(id);
        return R.ok();
    }
}
