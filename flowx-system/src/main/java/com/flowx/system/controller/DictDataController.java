package com.flowx.system.controller;

import com.flowx.common.core.result.R;
import com.flowx.system.dto.DictDataDTO;
import com.flowx.system.service.DictDataService;
import com.flowx.system.vo.DictDataVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Dictionary data management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * Get dict data by ID
     *
     * @param id dict data ID
     * @return dict data VO
     */
    @GetMapping("/{id}")
    public R<DictDataVO> getDictDataById(@PathVariable("id") Long id) {
        DictDataVO vo = dictDataService.getDictDataById(id);
        return R.ok(vo);
    }

    /**
     * Get dict data list by dict type
     *
     * @param dictType dict type
     * @return dict data list
     */
    @GetMapping("/type/{dictType}")
    public R<List<DictDataVO>> getDictDataByType(@PathVariable("dictType") String dictType) {
        List<DictDataVO> dataList = dictDataService.getDictDataByType(dictType);
        return R.ok(dataList);
    }

    /**
     * Create new dict data
     *
     * @param dto dict data creation DTO
     * @return created dict data ID
     */
    @PostMapping
    public R<Long> createDictData(@Valid @RequestBody DictDataDTO dto) {
        Long dictDataId = dictDataService.createDictData(dto);
        return R.ok(dictDataId);
    }

    /**
     * Update existing dict data (id from request body)
     *
     * @param dto dict data update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateDictData(@Valid @RequestBody DictDataDTO dto) {
        dictDataService.updateDictData(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete dict data by IDs (comma-separated)
     *
     * @param ids dict data IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteDictData(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            dictDataService.deleteDictData(id);
        }
        return R.ok();
    }
}