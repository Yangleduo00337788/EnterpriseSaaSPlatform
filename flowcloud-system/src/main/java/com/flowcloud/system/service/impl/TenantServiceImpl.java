package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.TenantProfileDTO;
import com.flowcloud.system.entity.SysTenant;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.SysTenantMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.service.TenantService;
import com.flowcloud.system.vo.TenantProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> FEATURE_KEYS = List.of("approval", "report", "message", "tenantSettings");

    private final SysTenantMapper tenantMapper;
    private final SysUserMapper userMapper;

    @Override
    public TenantProfileVO getCurrentTenant() {
        SysTenant tenant = tenantMapper.selectOneById(TenantContext.getTenantId());
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        return toVO(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentTenant(TenantProfileDTO dto) {
        SysTenant tenant = tenantMapper.selectOneById(TenantContext.getTenantId());
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        tenant.setTenantName(dto.getTenantName());
        tenant.setContactName(dto.getContactName());
        tenant.setContactPhone(dto.getContactPhone());
        tenant.setContactEmail(dto.getContactEmail());
        tenant.setLogo(dto.getLogo());
        tenant.setThemeColor(dto.getThemeColor());
        if (StringUtils.hasText(dto.getPlanType())) {
            tenant.setPlanType(dto.getPlanType());
        }
        if (dto.getMaxUsers() != null) {
            tenant.setMaxUsers(dto.getMaxUsers());
        }
        if (StringUtils.hasText(dto.getExpireTime())) {
            tenant.setExpireTime(LocalDateTime.parse(dto.getExpireTime(), FORMATTER));
        }
        if (StringUtils.hasText(dto.getPackageConfig())) {
            tenant.setPackageConfig(dto.getPackageConfig());
        }
        if (StringUtils.hasText(dto.getFeatureConfig())) {
            tenant.setFeatureConfig(dto.getFeatureConfig());
        }
        tenantMapper.update(tenant);
    }

    private TenantProfileVO toVO(SysTenant tenant) {
        TenantProfileVO vo = new TenantProfileVO();
        long currentUsers = userMapper.selectCountByQuery(
                com.mybatisflex.core.query.QueryWrapper.create()
                        .where(SysUser::getTenantId).eq(tenant.getId()));
        vo.setId(tenant.getId());
        vo.setTenantCode(tenant.getTenantCode());
        vo.setTenantName(tenant.getTenantName());
        vo.setContactName(tenant.getContactName());
        vo.setContactPhone(tenant.getContactPhone());
        vo.setContactEmail(tenant.getContactEmail());
        vo.setLogo(tenant.getLogo());
        vo.setThemeColor(tenant.getThemeColor());
        vo.setStatus(tenant.getStatus());
        vo.setPlanType(tenant.getPlanType());
        vo.setMaxUsers(tenant.getMaxUsers());
        vo.setCurrentUsers((int) currentUsers);
        vo.setRemainingUserSlots(tenant.getMaxUsers() == null ? null : Math.max(tenant.getMaxUsers() - (int) currentUsers, 0));
        vo.setExpireTime(tenant.getExpireTime() == null ? null : tenant.getExpireTime().format(FORMATTER));
        vo.setExpired(tenant.getExpireTime() != null && tenant.getExpireTime().isBefore(LocalDateTime.now()));
        vo.setPackageConfig(tenant.getPackageConfig());
        vo.setFeatureConfig(tenant.getFeatureConfig());
        vo.setEnabledFeatures(resolveEnabledFeatures(tenant.getFeatureConfig()));
        return vo;
    }

    private List<String> resolveEnabledFeatures(String featureConfig) {
        if (!StringUtils.hasText(featureConfig)) {
            return List.of();
        }
        return FEATURE_KEYS.stream()
                .filter(key -> cn.hutool.json.JSONUtil.parseObj(featureConfig).getBool(key, false))
                .toList();
    }
}