package com.flowx.system.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.system.dto.DictTypeDTO;
import com.flowx.system.dto.TenantQueryDTO;

import java.util.List;

/**
 * Dictionary type service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface DictTypeService {

    /**
     * Get dict type by ID
     *
     * @param dictId dict type ID
     * @return dict type DTO
     */
    DictTypeDTO getDictTypeById(Long dictId);

    /**
     * Get dict type by type string
     *
     * @param dictType dict type string
     * @return dict type DTO
     */
    DictTypeDTO getDictTypeByType(String dictType);

    /**
     * Create new dict type
     *
     * @param dto dict type creation DTO
     * @return created dict type ID
     */
    Long createDictType(DictTypeDTO dto);

    /**
     * Update existing dict type
     *
     * @param dictId dict type ID
     * @param dto    dict type update DTO
     */
    void updateDictType(Long dictId, DictTypeDTO dto);

    /**
     * Delete dict type (soft delete)
     *
     * @param dictId dict type ID
     */
    void deleteDictType(Long dictId);

    /**
     * List dict types with pagination
     *
     * @param queryDTO query parameters (reusing TenantQueryDTO pattern for pageNum/pageSize)
     * @return paginated dict type list
     */
    PageResult<DictTypeDTO> listDictTypes(TenantQueryDTO queryDTO);

    /**
     * List all dict types
     *
     * @return dict type list
     */
    List<DictTypeDTO> listAllDictTypes();
}
