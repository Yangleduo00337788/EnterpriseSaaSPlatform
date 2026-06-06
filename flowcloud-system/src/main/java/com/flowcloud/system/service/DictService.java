package com.flowcloud.system.service;

import com.flowcloud.system.dto.DictTypeDTO;
import com.flowcloud.system.vo.DictDataVO;
import com.flowcloud.system.vo.DictTypeVO;

import java.util.List;

public interface DictService {

    List<DictTypeVO> listTypes();

    DictTypeVO getById(Long id);

    List<DictDataVO> listByCode(String dictCode);

    void create(DictTypeDTO dto);

    void update(DictTypeDTO dto);

    void delete(Long id);
}
