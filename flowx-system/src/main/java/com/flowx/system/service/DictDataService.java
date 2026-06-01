package com.flowx.system.service;

import com.flowx.system.dto.DictDataDTO;
import com.flowx.system.vo.DictDataVO;

import java.util.List;

/**
 * Dictionary data service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface DictDataService {

    /**
     * Get dict data by ID
     *
     * @param dictDataId dict data ID
     * @return dict data VO
     */
    DictDataVO getDictDataById(Long dictDataId);

    /**
     * Create new dict data
     *
     * @param dto dict data creation DTO
     * @return created dict data ID
     */
    Long createDictData(DictDataDTO dto);

    /**
     * Update existing dict data
     *
     * @param dictDataId dict data ID
     * @param dto        dict data update DTO
     */
    void updateDictData(Long dictDataId, DictDataDTO dto);

    /**
     * Delete dict data (soft delete)
     *
     * @param dictDataId dict data ID
     */
    void deleteDictData(Long dictDataId);

    /**
     * Get dict data list by dict type
     *
     * @param dictType dict type
     * @return dict data VO list
     */
    List<DictDataVO> getDictDataByType(String dictType);
}
