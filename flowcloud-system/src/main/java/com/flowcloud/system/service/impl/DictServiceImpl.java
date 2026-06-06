package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.DictDataDTO;
import com.flowcloud.system.dto.DictTypeDTO;
import com.flowcloud.system.entity.SysDictData;
import com.flowcloud.system.entity.SysDictType;
import com.flowcloud.system.mapper.SysDictDataMapper;
import com.flowcloud.system.mapper.SysDictTypeMapper;
import com.flowcloud.system.service.DictService;
import com.flowcloud.system.vo.DictDataVO;
import com.flowcloud.system.vo.DictTypeVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;

    @Override
    public List<DictTypeVO> listTypes() {
        return dictTypeMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where(SysDictType::getTenantId).eq(TenantContext.getTenantId())
                                .orderBy(SysDictType::getDictCode, true))
                .stream()
                .map(this::toTypeVO)
                .toList();
    }

    @Override
    public DictTypeVO getById(Long id) {
        SysDictType type = getTypeOrThrow(id);
        DictTypeVO vo = toTypeVO(type);
        vo.setItems(loadItems(id));
        return vo;
    }

    @Override
    public List<DictDataVO> listByCode(String dictCode) {
        SysDictType type = dictTypeMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SysDictType::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysDictType::getDictCode).eq(dictCode)
                        .and(SysDictType::getStatus).eq(1));
        if (type == null) {
            return List.of();
        }
        return loadItems(type.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictTypeDTO dto) {
        ensureCodeUnique(dto.getDictCode(), null);
        SysDictType type = new SysDictType();
        type.setTenantId(TenantContext.getTenantId());
        type.setDictCode(dto.getDictCode());
        type.setDictName(dto.getDictName());
        type.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        type.setRemark(dto.getRemark());
        dictTypeMapper.insert(type);
        saveItems(type.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictTypeDTO dto) {
        SysDictType type = getTypeOrThrow(dto.getId());
        ensureCodeUnique(dto.getDictCode(), dto.getId());
        type.setDictCode(dto.getDictCode());
        type.setDictName(dto.getDictName());
        if (dto.getStatus() != null) {
            type.setStatus(dto.getStatus());
        }
        type.setRemark(dto.getRemark());
        dictTypeMapper.update(type);
        dictDataMapper.deleteByQuery(
                QueryWrapper.create().where(SysDictData::getDictTypeId).eq(type.getId()));
        saveItems(type.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getTypeOrThrow(id);
        dictDataMapper.deleteByQuery(
                QueryWrapper.create().where(SysDictData::getDictTypeId).eq(id));
        dictTypeMapper.deleteById(id);
    }

    private void saveItems(Long typeId, List<DictDataDTO> items) {
        if (items == null) {
            return;
        }
        int sort = 0;
        for (DictDataDTO item : items) {
            SysDictData data = new SysDictData();
            data.setTenantId(TenantContext.getTenantId());
            data.setDictTypeId(typeId);
            data.setDictLabel(item.getDictLabel());
            data.setDictValue(item.getDictValue());
            data.setSort(item.getSort() != null ? item.getSort() : sort++);
            data.setStatus(item.getStatus() != null ? item.getStatus() : 1);
            data.setRemark(item.getRemark());
            dictDataMapper.insert(data);
        }
    }

    private List<DictDataVO> loadItems(Long typeId) {
        return dictDataMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where(SysDictData::getDictTypeId).eq(typeId)
                                .orderBy(SysDictData::getSort, true))
                .stream()
                .map(this::toDataVO)
                .toList();
    }

    private SysDictType getTypeOrThrow(Long id) {
        SysDictType type = dictTypeMapper.selectOneById(id);
        if (type == null || !type.getTenantId().equals(TenantContext.getTenantId())) {
            throw new BusinessException("字典不存在");
        }
        return type;
    }

    private void ensureCodeUnique(String dictCode, Long excludeId) {
        QueryWrapper query = QueryWrapper.create()
                .where(SysDictType::getTenantId).eq(TenantContext.getTenantId())
                .and(SysDictType::getDictCode).eq(dictCode);
        if (excludeId != null) {
            query.and(SysDictType::getId).ne(excludeId);
        }
        if (dictTypeMapper.selectCountByQuery(query) > 0) {
            throw new BusinessException("字典编码已存在");
        }
    }

    private DictTypeVO toTypeVO(SysDictType type) {
        DictTypeVO vo = new DictTypeVO();
        vo.setId(type.getId());
        vo.setDictCode(type.getDictCode());
        vo.setDictName(type.getDictName());
        vo.setStatus(type.getStatus());
        vo.setRemark(type.getRemark());
        return vo;
    }

    private DictDataVO toDataVO(SysDictData data) {
        DictDataVO vo = new DictDataVO();
        vo.setId(data.getId());
        vo.setDictTypeId(data.getDictTypeId());
        vo.setDictLabel(data.getDictLabel());
        vo.setDictValue(data.getDictValue());
        vo.setSort(data.getSort());
        vo.setStatus(data.getStatus());
        vo.setRemark(data.getRemark());
        return vo;
    }
}
