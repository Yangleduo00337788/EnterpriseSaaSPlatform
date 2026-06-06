package com.flowcloud.system.controller;

import com.flowcloud.common.result.Result;
import com.flowcloud.system.dto.PositionDTO;
import com.flowcloud.system.service.PositionService;
import com.flowcloud.system.vo.PositionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/positions")
@RequiredArgsConstructor
public class SysPositionController {

    private final PositionService positionService;

    @GetMapping
    public Result<List<PositionVO>> list() {
        return Result.ok(positionService.listAll());
    }

    @PostMapping
    public Result<Void> create(@RequestBody PositionDTO dto) {
        positionService.create(dto);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PositionDTO dto) {
        dto.setId(id);
        positionService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        positionService.delete(id);
        return Result.ok();
    }

    /** 查用户已有岗位 */
    @GetMapping("/user/{userId}")
    public Result<List<Long>> getUserPositions(@PathVariable Long userId) {
        return Result.ok(positionService.getUserPositionIds(userId));
    }

    /** 给用户分配岗位 */
    @PutMapping("/user/{userId}")
    public Result<Void> assignUserPositions(@PathVariable Long userId,
                                            @RequestBody Map<String, List<Long>> body) {
        positionService.assignUserPositions(userId, body.get("positionIds"));
        return Result.ok();
    }
}