package com.flowx.message.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.message.service.MsgTemplateService;
import com.flowx.message.vo.MsgTemplateVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Message template management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/message/template")
@RequiredArgsConstructor
public class MsgTemplateController {

    private final MsgTemplateService templateService;

    /**
     * Get template by ID
     *
     * @param id template ID
     * @return template VO
     */
    @GetMapping("/{id}")
    public R<MsgTemplateVO> getTemplateById(@PathVariable("id") Long id) {
        MsgTemplateVO vo = templateService.getTemplateById(id);
        return R.ok(vo);
    }

    /**
     * Get template by code
     *
     * @param code template code
     * @return template VO
     */
    @GetMapping("/code/{code}")
    public R<MsgTemplateVO> getTemplateByCode(@PathVariable("code") String code) {
        MsgTemplateVO vo = templateService.getTemplateByCode(code);
        return R.ok(vo);
    }

    /**
     * List templates with pagination
     *
     * @param pageNum      page number
     * @param pageSize     page size
     * @param templateType optional type filter
     * @return paginated template list
     */
    @GetMapping("/list")
    public R<PageResult<MsgTemplateVO>> listTemplates(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "templateType", required = false) String templateType) {
        PageResult<MsgTemplateVO> result = templateService.listTemplates(pageNum, pageSize, templateType);
        return R.ok(result);
    }

    /**
     * Create new template
     *
     * @param vo template data
     * @return created template ID
     */
    @PostMapping
    public R<Long> createTemplate(@Valid @RequestBody MsgTemplateVO vo) {
        Long templateId = templateService.createTemplate(vo);
        return R.ok(templateId);
    }

    /**
     * Update existing template (id from request body)
     *
     * @param vo template data
     * @return success response
     */
    @PutMapping
    public R<Void> updateTemplate(@Valid @RequestBody MsgTemplateVO vo) {
        templateService.updateTemplate(vo.getId(), vo);
        return R.ok();
    }

    /**
     * Delete templates by IDs (comma-separated)
     *
     * @param ids template IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteTemplates(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            templateService.deleteTemplate(id);
        }
        return R.ok();
    }
}