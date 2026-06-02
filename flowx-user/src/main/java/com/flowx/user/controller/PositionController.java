package com.flowx.user.controller;

import com.flowx.common.core.result.R;
import com.flowx.user.dto.PositionDTO;
import com.flowx.user.service.PositionService;
import com.flowx.user.vo.PositionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Position management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * Get position by ID
     *
     * @param id position ID
     * @return position VO
     */
    @GetMapping("/{id}")
    public R<PositionVO> getPositionById(@PathVariable("id") Long id) {
        PositionVO positionVO = positionService.getPositionById(id);
        return R.ok(positionVO);
    }

    /**
     * List all positions
     *
     * @return list of position VOs
     */
    @GetMapping("/list")
    public R<List<PositionVO>> listPositions() {
        List<PositionVO> positions = positionService.listPositions();
        return R.ok(positions);
    }

    /**
     * Create new position
     *
     * @param dto position creation DTO
     * @return created position ID
     */
    @PostMapping
    public R<Long> createPosition(@Valid @RequestBody PositionDTO dto) {
        Long positionId = positionService.createPosition(dto);
        return R.ok(positionId);
    }

    /**
     * Update existing position (id from request body)
     *
     * @param dto position update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updatePosition(@Valid @RequestBody PositionDTO dto) {
        positionService.updatePosition(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete positions by IDs (comma-separated)
     *
     * @param ids position IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deletePositions(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            positionService.deletePosition(id);
        }
        return R.ok();
    }
}