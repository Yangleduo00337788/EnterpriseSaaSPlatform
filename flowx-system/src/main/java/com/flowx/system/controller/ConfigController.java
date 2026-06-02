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

import java.util.Arrays;
import java.util.List;

/**
 * System config management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/config")
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
     * Update existing config (id from request body)
     *
     * @param dto config update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateConfig(@Valid @RequestBody ConfigDTO dto) {
        configService.updateConfig(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete configs by IDs (comma-separated)
     *
     * @param ids config IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteConfigs(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            configService.deleteConfig(id);
        }
        return R.ok();
    }
}