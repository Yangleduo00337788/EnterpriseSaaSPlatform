package com.flowx.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.infrastructure.redis.RedisService;
import com.flowx.system.convert.DictTypeConvert;
import com.flowx.system.dto.DictTypeDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.entity.SysDictData;
import com.flowx.system.entity.SysDictType;
import com.flowx.system.mapper.SysDictDataMapper;
import com.flowx.system.mapper.SysDictTypeMapper;
import com.flowx.system.service.DictTypeService;
import com.flowx.common.core.constant.CacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Dictionary type service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl implements DictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    private final DictTypeConvert dictTypeConvert;
    private final RedisService redisService;

    @Override
    public DictTypeDTO getDictTypeById(Long dictId) {
        AssertUtil.notNull(dictId, "字典类型ID不能为空");
        SysDictType dictType = dictTypeMapper.selectOneById(dictId);
        AssertUtil.notNull(dictType, ResultCodeEnum.DICT_NOT_FOUND.getCode(), ResultCodeEnum.DICT_NOT_FOUND.getMessage());
        return dictTypeConvert.toDTO(dictType);
    }

    @Override
    public DictTypeDTO getDictTypeByType(String dictType) {
        AssertUtil.notBlank(dictType, "字典类型不能为空");
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("dict_type", dictType);
        SysDictType entity = dictTypeMapper.selectOne(wrapper);
        AssertUtil.notNull(entity, ResultCodeEnum.DICT_NOT_FOUND.getCode(), ResultCodeEnum.DICT_NOT_FOUND.getMessage());
        return dictTypeConvert.toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDictType(DictTypeDTO dto) {
        AssertUtil.notNull(dto, "字典类型信息不能为空");
        AssertUtil.notBlank(dto.getDictName(), "字典名称不能为空");
        AssertUtil.notBlank(dto.getDictType(), "字典类型不能为空");

        // Check dict type uniqueness
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("dict_type", dto.getDictType());
        Long count = dictTypeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("字典类型已存在");
        }

        SysDictType dictType = dictTypeConvert.toEntity(dto);

        // Set defaults
        if (dictType.getStatus() == null) {
            dictType.setStatus(1);
        }

        dictTypeMapper.insert(dictType);
        log.info("Created dict type: {}", dictType.getDictType());
        return dictType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(Long dictId, DictTypeDTO dto) {
        AssertUtil.notNull(dictId, "字典类型ID不能为空");
        AssertUtil.notNull(dto, "字典类型信息不能为空");

        SysDictType dictType = dictTypeMapper.selectOneById(dictId);
        AssertUtil.notNull(dictType, ResultCodeEnum.DICT_NOT_FOUND.getCode(), ResultCodeEnum.DICT_NOT_FOUND.getMessage());

        // Check dict type uniqueness if changed
        if (StringUtils.hasText(dto.getDictType()) && !dto.getDictType().equals(dictType.getDictType())) {
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper.eq("dict_type", dto.getDictType());
            wrapper.ne("id", dictId);
            Long count = dictTypeMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("字典类型已存在");
            }
        }

        String oldDictType = dictType.getDictType();
        dictTypeConvert.updateEntity(dto, dictType);
        dictTypeMapper.updateById(dictType);

        // If dict type changed, update related dict data and evict cache
        if (StringUtils.hasText(dto.getDictType()) && !dto.getDictType().equals(oldDictType)) {
            QueryWrapper updateWrapper = QueryWrapper.create();
            updateWrapper.eq("dict_type", oldDictType);
            SysDictData updateEntity = new SysDictData();
            updateEntity.setDictType(dto.getDictType());
            dictDataMapper.updateByQuery(updateEntity, updateWrapper);

            // Evict old cache
            redisService.delete(CacheConstant.DICT_DATA + oldDictType);
        }

        // Evict cache
        redisService.delete(CacheConstant.DICT_DATA + dictType.getDictType());
        log.info("Updated dict type: {}", dictId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long dictId) {
        AssertUtil.notNull(dictId, "字典类型ID不能为空");
        SysDictType dictType = dictTypeMapper.selectOneById(dictId);
        AssertUtil.notNull(dictType, ResultCodeEnum.DICT_NOT_FOUND.getCode(), ResultCodeEnum.DICT_NOT_FOUND.getMessage());

        // Soft delete dict type
        dictTypeMapper.deleteById(dictId);

        // Delete related dict data
        QueryWrapper deleteWrapper = QueryWrapper.create();
        deleteWrapper.eq("dict_type", dictType.getDictType());
        dictDataMapper.deleteByQuery(deleteWrapper);

        // Evict cache
        redisService.delete(CacheConstant.DICT_DATA + dictType.getDictType());
        log.info("Deleted dict type: {}", dictId);
    }

    @Override
    public PageResult<DictTypeDTO> listDictTypes(TenantQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");

        QueryWrapper wrapper = QueryWrapper.create();

        // Reuse tenantName field as dict name search, and status as status filter
        if (StringUtils.hasText(queryDTO.getTenantName())) {
            wrapper.like("dict_name", queryDTO.getTenantName());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }

        wrapper.orderBy("create_time", false);

        Page<SysDictType> dictTypePage = dictTypeMapper.paginate(queryDTO.getPageNum(), queryDTO.getPageSize(), wrapper);
        List<DictTypeDTO> dtoList = dictTypeConvert.toDTOList(dictTypePage.getRecords());

        return PageResult.of(dictTypePage.getTotalRow(), dtoList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<DictTypeDTO> listAllDictTypes() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("status", 1);
        wrapper.orderBy("create_time", true);
        List<SysDictType> entities = dictTypeMapper.selectList(wrapper);
        return dictTypeConvert.toDTOList(entities);
    }
}
