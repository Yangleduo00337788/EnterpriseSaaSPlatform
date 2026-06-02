package com.flowx.user.controller;

import com.flowx.common.core.result.R;
import com.flowx.user.dto.MenuDTO;
import com.flowx.user.service.MenuService;
import com.flowx.user.vo.MenuVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Menu management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Get menu by ID
     *
     * @param id menu ID
     * @return menu VO
     */
    @GetMapping("/{id}")
    public R<MenuVO> getMenuById(@PathVariable("id") Long id) {
        MenuVO menuVO = menuService.getMenuById(id);
        return R.ok(menuVO);
    }

    /**
     * Get menu tree structure
     *
     * @return tree-structured menu VOs
     */
    @GetMapping("/tree")
    public R<List<MenuVO>> getMenuTree() {
        List<MenuVO> tree = menuService.getMenuTree();
        return R.ok(tree);
    }

    /**
     * List all menus (flat)
     *
     * @return list of menu VOs
     */
    @GetMapping("/list")
    public R<List<MenuVO>> listMenus() {
        List<MenuVO> menus = menuService.listMenus();
        return R.ok(menus);
    }

    /**
     * Create new menu
     *
     * @param dto menu creation DTO
     * @return created menu ID
     */
    @PostMapping
    public R<Long> createMenu(@Valid @RequestBody MenuDTO dto) {
        Long menuId = menuService.createMenu(dto);
        return R.ok(menuId);
    }

    /**
     * Update existing menu (id from request body)
     *
     * @param dto menu update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateMenu(@Valid @RequestBody MenuDTO dto) {
        menuService.updateMenu(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete menus by IDs (comma-separated)
     *
     * @param ids menu IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteMenus(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            menuService.deleteMenu(id);
        }
        return R.ok();
    }
}