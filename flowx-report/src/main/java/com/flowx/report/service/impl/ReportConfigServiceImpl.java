package com.flowx.report.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.report.entity.RptReportConfig;
import com.flowx.report.mapper.RptReportMapper;
import com.flowx.report.service.ReportConfigService;
import com.flowx.report.vo.ReportConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Report configuration service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportConfigServiceImpl implements ReportConfigService {

    private final RptReportMapper reportMapper;

    @Override
    public ReportConfigVO getConfigById(Long configId) {
        AssertUtil.notNull(configId, "报表配置ID不能为空");
        RptReportConfig config = reportMapper.selectOneById(configId);
        AssertUtil.notNull(config, ResultCodeEnum.NOT_FOUND.getCode(), "报表配置不存在");
        return toVO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConfig(RptReportConfig config) {
        AssertUtil.notNull(config, "报表配置信息不能为空");
        AssertUtil.notBlank(config.getReportName(), "报表名称不能为空");
        AssertUtil.notBlank(config.getReportCode(), "报表编码不能为空");

        // Check report code uniqueness
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("report_code", config.getReportCode());
        Long count = reportMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException(ResultCodeEnum.DUPLICATE_DATA.getCode(), "报表编码已存在");
        }

        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        if (config.getSort() == null) {
            config.setSort(0);
        }

        reportMapper.insert(config);
        log.info("Created report config: {}", config.getReportCode());
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Long configId, RptReportConfig config) {
        AssertUtil.notNull(configId, "报表配置ID不能为空");
        AssertUtil.notNull(config, "报表配置信息不能为空");

        RptReportConfig existing = reportMapper.selectOneById(configId);
        AssertUtil.notNull(existing, ResultCodeEnum.NOT_FOUND.getCode(), "报表配置不存在");

        // Check report code uniqueness if changed
        if (StringUtils.hasText(config.getReportCode()) && !config.getReportCode().equals(existing.getReportCode())) {
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper.eq("report_code", config.getReportCode());
            Long count = reportMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException(ResultCodeEnum.DUPLICATE_DATA.getCode(), "报表编码已存在");
            }
        }

        if (StringUtils.hasText(config.getReportName())) {
            existing.setReportName(config.getReportName());
        }
        if (StringUtils.hasText(config.getReportCode())) {
            existing.setReportCode(config.getReportCode());
        }
        if (StringUtils.hasText(config.getReportType())) {
            existing.setReportType(config.getReportType());
        }
        if (StringUtils.hasText(config.getDataSource())) {
            existing.setDataSource(config.getDataSource());
        }
        if (StringUtils.hasText(config.getChartType())) {
            existing.setChartType(config.getChartType());
        }
        if (config.getConfigJson() != null) {
            existing.setConfigJson(config.getConfigJson());
        }
        if (config.getStatus() != null) {
            existing.setStatus(config.getStatus());
        }
        if (config.getSort() != null) {
            existing.setSort(config.getSort());
        }

        reportMapper.updateById(existing);
        log.info("Updated report config: {}", configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long configId) {
        AssertUtil.notNull(configId, "报表配置ID不能为空");
        RptReportConfig config = reportMapper.selectOneById(configId);
        AssertUtil.notNull(config, ResultCodeEnum.NOT_FOUND.getCode(), "报表配置不存在");

        reportMapper.deleteById(configId);
        log.info("Deleted report config: {}", configId);
    }

    @Override
    public PageResult<ReportConfigVO> listConfigs(Integer pageNum, Integer pageSize, String reportType) {
        QueryWrapper wrapper = QueryWrapper.create();

        if (StringUtils.hasText(reportType)) {
            wrapper.eq("report_type", reportType);
        }

        wrapper.orderBy("sort", true).orderBy("create_time", false);

        Page<RptReportConfig> configPage = reportMapper.paginate(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10, wrapper);
        List<ReportConfigVO> voList = configPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(configPage.getTotalRow(), voList,
                pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
    }

    /**
     * Convert entity to VO
     */
    private ReportConfigVO toVO(RptReportConfig entity) {
        ReportConfigVO vo = new ReportConfigVO();
        vo.setId(entity.getId());
        vo.setReportName(entity.getReportName());
        vo.setReportCode(entity.getReportCode());
        vo.setReportType(entity.getReportType());
        vo.setDataSource(entity.getDataSource());
        vo.setChartType(entity.getChartType());
        vo.setConfigJson(entity.getConfigJson());
        vo.setStatus(entity.getStatus());
        vo.setSort(entity.getSort());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
