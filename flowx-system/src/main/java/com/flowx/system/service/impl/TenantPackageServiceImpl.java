package com.flowx.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flowx.common.util.AssertUtil;
import com.flowx.system.convert.TenantPackageConvert;
import com.flowx.system.dto.TenantPackageDTO;
import com.flowx.system.entity.SysTenantPackage;
import com.flowx.system.mapper.SysTenantPackageMapper;
import com.flowx.system.service.TenantPackageService;
import com.flowx.system.vo.TenantPackageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tenant package service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantPackageServiceImpl implements TenantPackageService {

    private final SysTenantPackageMapper packageMapper;
    private final TenantPackageConvert packageConvert;

    @Override
    public TenantPackageVO getPackageById(Long packageId) {
        AssertUtil.notNull(packageId, "套餐ID不能为空");
        SysTenantPackage pkg = packageMapper.selectById(packageId);
        AssertUtil.notNull(pkg, "租户套餐不存在");
        return packageConvert.toVO(pkg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPackage(TenantPackageDTO dto) {
        AssertUtil.notNull(dto, "套餐信息不能为空");
        AssertUtil.notBlank(dto.getPackageName(), "套餐名称不能为空");

        SysTenantPackage pkg = packageConvert.toEntity(dto);

        // Set defaults
        if (pkg.getStatus() == null) {
            pkg.setStatus(1);
        }

        packageMapper.insert(pkg);
        log.info("Created tenant package: {}", pkg.getPackageName());
        return pkg.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePackage(Long packageId, TenantPackageDTO dto) {
        AssertUtil.notNull(packageId, "套餐ID不能为空");
        AssertUtil.notNull(dto, "套餐信息不能为空");

        SysTenantPackage pkg = packageMapper.selectById(packageId);
        AssertUtil.notNull(pkg, "租户套餐不存在");

        packageConvert.updateEntity(dto, pkg);
        packageMapper.updateById(pkg);
        log.info("Updated tenant package: {}", packageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePackage(Long packageId) {
        AssertUtil.notNull(packageId, "套餐ID不能为空");
        SysTenantPackage pkg = packageMapper.selectById(packageId);
        AssertUtil.notNull(pkg, "租户套餐不存在");

        // Soft delete
        packageMapper.deleteById(packageId);
        log.info("Deleted tenant package: {}", packageId);
    }

    @Override
    public List<TenantPackageVO> listPackages() {
        QueryWrapper<SysTenantPackage> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("create_time");
        List<SysTenantPackage> packages = packageMapper.selectList(wrapper);
        return packageConvert.toVOList(packages);
    }
}
