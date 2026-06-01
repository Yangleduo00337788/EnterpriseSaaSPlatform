package com.flowx.system.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.system.dto.DictDataDTO;
import com.flowx.system.dto.DictTypeDTO;
import com.flowx.system.dto.TenantQueryDTO;
import com.flowx.system.service.DictDataService;
import com.flowx.system.service.DictTypeService;
import com.flowx.system.vo.DictDataVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dictionary type management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/dict-types")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;
    private final DictDataService dictDataService;

    /**
     * Get dict type by ID
     *
     * @param id dict type ID
     * @return dict type DTO
     */
    @GetMapping("/{id}")
    public R<DictTypeDTO> getDictTypeById(@PathVariable("id") Long id) {
        DictTypeDTO dto = dictTypeService.getDictTypeById(id);
        return R.ok(dto);
    }

    /**
     * List dict types with pagination
     *
     * @param queryDTO query parameters
     * @return paginated dict type list
     */
    @GetMapping("/list")
    public R<PageResult<DictTypeDTO>> listDictTypes(TenantQueryDTO queryDTO) {
        PageResult<DictTypeDTO> result = dictTypeService.listDictTypes(queryDTO);
        return R.ok(result);
    }

    /**
     * Get dict data by dict type
     *
     * @param dictType dict type string
     * @return dict data list
     */
    @GetMapping("/{dictType}/data")
    public R<List<DictDataVO>> getDictDataByType(@PathVariable("dictType") String dictType) {
        List<DictDataVO> dataList = dictDataService.getDictDataByType(dictType);
        return R.ok(dataList);
    }

    /**
     * Create new dict type
     *
     * @param dto dict type creation DTO
     * @return created dict type ID
     */
    @PostMapping
    public R<Long> createDictType(@Valid @RequestBody DictTypeDTO dto) {
        Long dictId = dictTypeService.createDictType(dto);
        return R.ok(dictId);
    }

    /**
     * Update existing dict type
     *
     * @param id  dict type ID
     * @param dto dict type update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateDictType(@PathVariable("id") Long id, @Valid @RequestBody DictTypeDTO dto) {
        dictTypeService.updateDictType(id, dto);
        return R.ok();
    }

    /**
     * Delete dict type
     *
     * @param id dict type ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteDictType(@PathVariable("id") Long id) {
        dictTypeService.deleteDictType(id);
        return R.ok();
    }
}
