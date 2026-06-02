package com.flowx.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.system.convert.TenantConvert;
import com.flowx.system.dto.TenantDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.entity.SysTenant;
import com.flowx.system.entity.SysTenantPackage;
import com.flowx.system.mapper.SysTenantMapper;
import com.flowx.system.mapper.SysTenantPackageMapper;
import com.flowx.system.service.TenantService;
import com.flowx.system.vo.TenantStatsVO;
import com.flowx.system.vo.TenantVO;
import com.flowx.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tenant service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final SysTenantMapper tenantMapper;
    private final SysTenantPackageMapper tenantPackageMapper;
    private final TenantConvert tenantConvert;
    private final SysUserMapper userMapper;

    @Override
    public TenantVO getTenantById(Long tenantId) {
        AssertUtil.notNull(tenantId, "租户ID不能为空");
        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        AssertUtil.notNull(tenant, ResultCodeEnum.TENANT_NOT_FOUND.getCode(), ResultCodeEnum.TENANT_NOT_FOUND.getMessage());
        return buildTenantVO(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTenant(TenantDTO dto) {
        AssertUtil.notNull(dto, "租户信息不能为空");
        AssertUtil.notBlank(dto.getTenantName(), "租户名称不能为空");
        AssertUtil.notBlank(dto.getContactName(), "联系人不能为空");

        SysTenant tenant = tenantConvert.toEntity(dto);

        // Set defaults
        if (tenant.getStatus() == null) {
            tenant.setStatus(0);
        }
        if (tenant.getAccountLimit() == null) {
            tenant.setAccountLimit(100);
        }

        tenantMapper.insert(tenant);
        log.info("Created tenant: {}", tenant.getTenantName());
        return tenant.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenant(Long tenantId, TenantDTO dto) {
        AssertUtil.notNull(tenantId, "租户ID不能为空");
        AssertUtil.notNull(dto, "租户信息不能为空");

        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        AssertUtil.notNull(tenant, ResultCodeEnum.TENANT_NOT_FOUND.getCode(), ResultCodeEnum.TENANT_NOT_FOUND.getMessage());

        tenantConvert.updateEntity(dto, tenant);
        tenantMapper.updateById(tenant);
        log.info("Updated tenant: {}", tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long tenantId) {
        AssertUtil.notNull(tenantId, "租户ID不能为空");
        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        AssertUtil.notNull(tenant, ResultCodeEnum.TENANT_NOT_FOUND.getCode(), ResultCodeEnum.TENANT_NOT_FOUND.getMessage());

        // Soft delete
        tenantMapper.deleteById(tenantId);
        log.info("Deleted tenant: {}", tenantId);
    }

    @Override
    public PageResult<TenantVO> listTenants(TenantQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");

        QueryWrapper wrapper = buildTenantQueryWrapper(queryDTO);

        Page<SysTenant> tenantPage = tenantMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);
        List<TenantVO> voList = tenantPage.getRecords().stream()
                .map(this::buildTenantVO)
                .collect(Collectors.toList());

        return PageResult.of(tenantPage.getTotalRow(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPackage(Long tenantId, Long packageId) {
        AssertUtil.notNull(tenantId, "租户ID不能为空");
        AssertUtil.notNull(packageId, "套餐ID不能为空");

        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        AssertUtil.notNull(tenant, ResultCodeEnum.TENANT_NOT_FOUND.getCode(), ResultCodeEnum.TENANT_NOT_FOUND.getMessage());

        // Verify package exists
        SysTenantPackage pkg = tenantPackageMapper.selectOneById(packageId);
        AssertUtil.notNull(pkg, "租户套餐不存在");

        tenant.setPackageId(packageId);
        tenantMapper.updateById(tenant);
        log.info("Assigned package {} to tenant {}", packageId, tenantId);
    }

    @Override
    public TenantStatsVO getTenantStats(Long tenantId) {
        AssertUtil.notNull(tenantId, "租户ID不能为空");
        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        AssertUtil.notNull(tenant, ResultCodeEnum.TENANT_NOT_FOUND.getCode(), ResultCodeEnum.TENANT_NOT_FOUND.getMessage());

        TenantStatsVO stats = new TenantStatsVO();

        // Count users for this tenant
        QueryWrapper userWrapper = QueryWrapper.create();
        userWrapper.eq("tenant_id", tenantId);
        Long userCount = userMapper.selectCount(userWrapper);
        stats.setUserCount(userCount);

        // Storage and API stats (placeholder - would integrate with file service and monitoring)
        stats.setStorageUsed(0L);
        stats.setStorageLimit(1073741824L); // 1GB default
        stats.setApiCallCount(0L);
        stats.setLastActiveTime(tenant.getUpdateTime());

        return stats;
    }

    /**
     * Build TenantVO with package name
     */
    private TenantVO buildTenantVO(SysTenant tenant) {
        TenantVO vo = tenantConvert.toVO(tenant);
        if (tenant.getPackageId() != null) {
            SysTenantPackage pkg = tenantPackageMapper.selectOneById(tenant.getPackageId());
            if (pkg != null) {
                vo.setPackageName(pkg.getPackageName());
            }
        }
        return vo;
    }

    /**
     * Build query wrapper from TenantQueryDTO
     */
    private QueryWrapper buildTenantQueryWrapper(TenantQueryDTO queryDTO) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (StringUtils.hasText(queryDTO.getTenantName())) {
            wrapper.like("tenant_name", queryDTO.getTenantName());
        }
        if (StringUtils.hasText(queryDTO.getContactName())) {
            wrapper.like("contact_name", queryDTO.getContactName());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }

        wrapper.orderBy("create_time", false);
        return wrapper;
    }
}
