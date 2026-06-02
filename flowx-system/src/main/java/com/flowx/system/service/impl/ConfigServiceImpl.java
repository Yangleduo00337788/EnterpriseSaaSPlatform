package com.flowx.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.constant.CacheConstant;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.util.AssertUtil;
import com.flowx.infrastructure.redis.RedisService;
import com.flowx.system.convert.ConfigConvert;
import com.flowx.system.dto.ConfigDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.entity.SysConfig;
import com.flowx.system.mapper.SysConfigMapper;
import com.flowx.system.service.ConfigService;
import com.flowx.system.vo.ConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * System config service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper configMapper;
    private final ConfigConvert configConvert;
    private final RedisService redisService;

    @Override
    public ConfigVO getConfigById(Long configId) {
        AssertUtil.notNull(configId, "参数ID不能为空");
        SysConfig config = configMapper.selectOneById(configId);
        AssertUtil.notNull(config, "配置参数不存在");
        return configConvert.toVO(config);
    }

    @Override
    public String getConfigByKey(String configKey) {
        AssertUtil.notBlank(configKey, "参数键名不能为空");

        // Try cache first
        String cacheKey = CacheConstant.CONFIG + configKey;
        Object cached = redisService.get(cacheKey);
        if (cached != null) {
            return cached.toString();
        }

        // Query from database
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("config_key", configKey);
        SysConfig config = configMapper.selectOne(wrapper);
        if (config == null) {
            return null;
        }

        // Put into cache
        redisService.set(cacheKey, config.getConfigValue(), CacheConstant.DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return config.getConfigValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConfig(ConfigDTO dto) {
        AssertUtil.notNull(dto, "配置信息不能为空");
        AssertUtil.notBlank(dto.getConfigName(), "参数名称不能为空");
        AssertUtil.notBlank(dto.getConfigKey(), "参数键名不能为空");
        AssertUtil.notBlank(dto.getConfigValue(), "参数键值不能为空");

        // Check config key uniqueness
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("config_key", dto.getConfigKey());
        Long count = configMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("参数键名已存在");
        }

        SysConfig config = configConvert.toEntity(dto);

        // Set defaults
        if (config.getConfigType() == null) {
            config.setConfigType("N");
        }

        configMapper.insert(config);

        // Cache the value
        String cacheKey = CacheConstant.CONFIG + config.getConfigKey();
        redisService.set(cacheKey, config.getConfigValue(), CacheConstant.DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);

        log.info("Created config: {} = {}", config.getConfigKey(), config.getConfigValue());
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Long configId, ConfigDTO dto) {
        AssertUtil.notNull(configId, "参数ID不能为空");
        AssertUtil.notNull(dto, "配置信息不能为空");

        SysConfig config = configMapper.selectOneById(configId);
        AssertUtil.notNull(config, "配置参数不存在");

        // Check config key uniqueness if changed
        if (StringUtils.hasText(dto.getConfigKey()) && !dto.getConfigKey().equals(config.getConfigKey())) {
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper.eq("config_key", dto.getConfigKey());
            wrapper.ne("id", configId);
            Long count = configMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("参数键名已存在");
            }
        }

        String oldConfigKey = config.getConfigKey();
        configConvert.updateEntity(dto, config);
        configMapper.updateById(config);

        // Evict old cache and set new cache
        redisService.delete(CacheConstant.CONFIG + oldConfigKey);
        String cacheKey = CacheConstant.CONFIG + config.getConfigKey();
        redisService.set(cacheKey, config.getConfigValue(), CacheConstant.DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);

        log.info("Updated config: {}", configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long configId) {
        AssertUtil.notNull(configId, "参数ID不能为空");
        SysConfig config = configMapper.selectOneById(configId);
        AssertUtil.notNull(config, "配置参数不存在");

        // Prevent deletion of system built-in configs
        if ("Y".equals(config.getConfigType())) {
            throw new BizException("不允许删除系统内置参数");
        }

        // Soft delete
        configMapper.deleteById(configId);

        // Evict cache
        redisService.delete(CacheConstant.CONFIG + config.getConfigKey());
        log.info("Deleted config: {}", configId);
    }

    @Override
    public PageResult<ConfigVO> listConfigs(TenantQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");

        QueryWrapper wrapper = QueryWrapper.create();

        // Reuse tenantName as config name search, contactName as config key search
        if (StringUtils.hasText(queryDTO.getTenantName())) {
            wrapper.like("config_name", queryDTO.getTenantName());
        }
        if (StringUtils.hasText(queryDTO.getContactName())) {
            wrapper.like("config_key", queryDTO.getContactName());
        }

        wrapper.orderBy("create_time", false);

        Page<SysConfig> configPage = configMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);
        List<ConfigVO> voList = configConvert.toVOList(configPage.getRecords());

        return PageResult.of(configPage.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }
}
