package com.flowx.system.controller;

import com.flowx.common.core.result.R;
import com.flowx.system.dto.DictDataDTO;
import com.flowx.system.service.DictDataService;
import com.flowx.system.vo.DictDataVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dictionary data management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/dict-data")
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
     * Update existing dict data
     *
     * @param id  dict data ID
     * @param dto dict data update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateDictData(@PathVariable("id") Long id, @Valid @RequestBody DictDataDTO dto) {
        dictDataService.updateDictData(id, dto);
        return R.ok();
    }

    /**
     * Delete dict data
     *
     * @param id dict data ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteDictData(@PathVariable("id") Long id) {
        dictDataService.deleteDictData(id);
        return R.ok();
    }
}
