package com.flowx.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flowx.common.core.constant.CacheConstant;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.infrastructure.redis.RedisService;
import com.flowx.system.convert.DictDataConvert;
import com.flowx.system.dto.DictDataDTO;
import com.flowx.system.entity.SysDictData;
import com.flowx.system.mapper.SysDictDataMapper;
import com.flowx.system.service.DictDataService;
import com.flowx.system.vo.DictDataVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Dictionary data service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private final SysDictDataMapper dictDataMapper;
    private final DictDataConvert dictDataConvert;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Override
    public DictDataVO getDictDataById(Long dictDataId) {
        AssertUtil.notNull(dictDataId, "字典数据ID不能为空");
        SysDictData dictData = dictDataMapper.selectById(dictDataId);
        AssertUtil.notNull(dictData, "字典数据不存在");
        return dictDataConvert.toVO(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDictData(DictDataDTO dto) {
        AssertUtil.notNull(dto, "字典数据信息不能为空");
        AssertUtil.notBlank(dto.getDictType(), "字典类型不能为空");
        AssertUtil.notBlank(dto.getDictLabel(), "字典标签不能为空");
        AssertUtil.notBlank(dto.getDictValue(), "字典值不能为空");

        SysDictData dictData = dictDataConvert.toEntity(dto);

        // Set defaults
        if (dictData.getSort() == null) {
            dictData.setSort(0);
        }
        if (dictData.getStatus() == null) {
            dictData.setStatus(1);
        }

        dictDataMapper.insert(dictData);

        // Evict cache for this dict type
        redisService.delete(CacheConstant.DICT_DATA + dto.getDictType());
        log.info("Created dict data: {} - {}", dto.getDictType(), dto.getDictLabel());
        return dictData.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(Long dictDataId, DictDataDTO dto) {
        AssertUtil.notNull(dictDataId, "字典数据ID不能为空");
        AssertUtil.notNull(dto, "字典数据信息不能为空");

        SysDictData dictData = dictDataMapper.selectById(dictDataId);
        AssertUtil.notNull(dictData, "字典数据不存在");

        String oldDictType = dictData.getDictType();
        dictDataConvert.updateEntity(dto, dictData);
        dictDataMapper.updateById(dictData);

        // Evict cache for both old and new dict type
        redisService.delete(CacheConstant.DICT_DATA + oldDictType);
        if (StringUtils.hasText(dto.getDictType()) && !dto.getDictType().equals(oldDictType)) {
            redisService.delete(CacheConstant.DICT_DATA + dto.getDictType());
        }
        log.info("Updated dict data: {}", dictDataId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long dictDataId) {
        AssertUtil.notNull(dictDataId, "字典数据ID不能为空");
        SysDictData dictData = dictDataMapper.selectById(dictDataId);
        AssertUtil.notNull(dictData, "字典数据不存在");

        // Soft delete
        dictDataMapper.deleteById(dictDataId);

        // Evict cache
        redisService.delete(CacheConstant.DICT_DATA + dictData.getDictType());
        log.info("Deleted dict data: {}", dictDataId);
    }

    @Override
    public List<DictDataVO> getDictDataByType(String dictType) {
        AssertUtil.notBlank(dictType, "字典类型不能为空");

        // Try to get from cache first
        String cacheKey = CacheConstant.DICT_DATA + dictType;
        try {
            Object cached = redisService.get(cacheKey);
            if (cached != null) {
                String json = cached.toString();
                return objectMapper.readValue(json, new TypeReference<List<DictDataVO>>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to read dict data from cache for type: {}", dictType, e);
        }

        // Query from database
        QueryWrapper<SysDictData> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_type", dictType);
        wrapper.eq("status", 1);
        wrapper.orderByAsc("sort");
        List<SysDictData> entities = dictDataMapper.selectList(wrapper);
        List<DictDataVO> voList = dictDataConvert.toVOList(entities);

        // Put into cache
        try {
            String json = objectMapper.writeValueAsString(voList);
            redisService.set(cacheKey, json, CacheConstant.DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Failed to cache dict data for type: {}", dictType, e);
        }

        return voList;
    }
}
