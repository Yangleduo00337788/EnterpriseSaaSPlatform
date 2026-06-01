package com.flowx.ai.controller;

import com.flowx.ai.dto.PromptTemplateDTO;
import com.flowx.ai.service.AiPromptTemplateService;
import com.flowx.ai.vo.AiPromptTemplateVO;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI prompt template controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/ai/templates")
@RequiredArgsConstructor
public class AiPromptTemplateController {

    private final AiPromptTemplateService promptTemplateService;

    /**
     * Get template by ID
     *
     * @param id template ID
     * @return template VO
     */
    @GetMapping("/{id}")
    public R<AiPromptTemplateVO> getTemplateById(@PathVariable("id") Long id) {
        AiPromptTemplateVO vo = promptTemplateService.getTemplateById(id);
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
    public R<PageResult<AiPromptTemplateVO>> listTemplates(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "templateType", required = false) String templateType) {
        PageResult<AiPromptTemplateVO> result = promptTemplateService.listTemplates(pageNum, pageSize, templateType);
        return R.ok(result);
    }

    /**
     * Create new template
     *
     * @param dto template data
     * @return created template ID
     */
    @PostMapping
    public R<Long> createTemplate(@Valid @RequestBody PromptTemplateDTO dto) {
        Long templateId = promptTemplateService.createTemplate(dto);
        return R.ok(templateId);
    }

    /**
     * Update existing template
     *
     * @param id  template ID
     * @param dto template data
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateTemplate(@PathVariable("id") Long id, @Valid @RequestBody PromptTemplateDTO dto) {
        promptTemplateService.updateTemplate(id, dto);
        return R.ok();
    }

    /**
     * Delete template
     *
     * @param id template ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteTemplate(@PathVariable("id") Long id) {
        promptTemplateService.deleteTemplate(id);
        return R.ok();
    }
}
