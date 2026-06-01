package com.flowx.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.exception.NotFoundException;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.common.util.SecurityUtil;
import com.flowx.workflow.dto.FlowDefinitionDTO;
import com.flowx.workflow.dto.FlowDeployDTO;
import com.flowx.workflow.entity.FlowDefinition;
import com.flowx.workflow.mapper.FlowCategoryMapper;
import com.flowx.workflow.entity.FlowCategory;
import com.flowx.workflow.mapper.FlowDefinitionMapper;
import com.flowx.workflow.service.FlowDefinitionService;
import com.flowx.workflow.vo.FlowDefinitionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Flow definition service implementation
 * <p>
 * Integrates with Flowable's RepositoryService for BPMN deployment and management.
 * </p>
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowDefinitionServiceImpl implements FlowDefinitionService {

    private final FlowDefinitionMapper definitionMapper;
    private final FlowCategoryMapper categoryMapper;
    private final RepositoryService repositoryService;

    @Override
    public FlowDefinitionVO getDefinitionById(Long definitionId) {
        AssertUtil.notNull(definitionId, "流程定义ID不能为空");
        FlowDefinition definition = definitionMapper.selectById(definitionId);
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());
        return convertToVO(definition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefinition(FlowDefinitionDTO dto) {
        AssertUtil.notNull(dto, "流程定义信息不能为空");
        AssertUtil.notBlank(dto.getDefinitionKey(), "流程定义标识不能为空");
        AssertUtil.notBlank(dto.getDefinitionName(), "流程定义名称不能为空");
        AssertUtil.notNull(dto.getCategoryId(), "流程分类不能为空");

        // Check duplicate definition key
        QueryWrapper<FlowDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq("definition_key", dto.getDefinitionKey());
        Long count = definitionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("流程定义标识已存在");
        }

        FlowDefinition definition = new FlowDefinition();
        BeanUtils.copyProperties(dto, definition);

        // Set defaults
        definition.setVersion(1);
        definition.setStatus(0); // Draft

        definitionMapper.insert(definition);
        log.info("Created flow definition: {} (key={})", definition.getDefinitionName(), definition.getDefinitionKey());
        return definition.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDefinition(Long definitionId, FlowDefinitionDTO dto) {
        AssertUtil.notNull(definitionId, "流程定义ID不能为空");
        AssertUtil.notNull(dto, "流程定义信息不能为空");

        FlowDefinition definition = definitionMapper.selectById(definitionId);
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());

        // Check duplicate definition key (exclude self)
        if (dto.getDefinitionKey() != null && !dto.getDefinitionKey().equals(definition.getDefinitionKey())) {
            QueryWrapper<FlowDefinition> wrapper = new QueryWrapper<>();
            wrapper.eq("definition_key", dto.getDefinitionKey());
            wrapper.ne("id", definitionId);
            Long count = definitionMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("流程定义标识已存在");
            }
        }

        BeanUtils.copyProperties(dto, definition, "id", "version", "status", "deployTime",
                "createTime", "createBy", "tenantId", "deleted");
        definitionMapper.updateById(definition);
        log.info("Updated flow definition: {}", definitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDefinition(Long definitionId) {
        AssertUtil.notNull(definitionId, "流程定义ID不能为空");
        FlowDefinition definition = definitionMapper.selectById(definitionId);
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());

        // If deployed, suspend the Flowable process definition first
        if (definition.getStatus() != null && definition.getStatus() == 1) {
            try {
                List<org.flowable.engine.repository.ProcessDefinition> processDefs = repositoryService
                        .createProcessDefinitionQuery()
                        .processDefinitionKey(definition.getDefinitionKey())
                        .list();
                for (org.flowable.engine.repository.ProcessDefinition pd : processDefs) {
                    repositoryService.suspendProcessDefinitionById(pd.getId(), true, null);
                }
            } catch (Exception e) {
                log.warn("Failed to suspend Flowable process definition for key={}", definition.getDefinitionKey(), e);
            }
        }

        // Soft delete
        definitionMapper.deleteById(definitionId);
        log.info("Deleted flow definition: {}", definitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deploy(FlowDeployDTO dto) {
        AssertUtil.notNull(dto, "部署信息不能为空");
        AssertUtil.notNull(dto.getDefinitionId(), "流程定义ID不能为空");
        AssertUtil.notBlank(dto.getBpmnXml(), "BPMN XML不能为空");

        FlowDefinition definition = definitionMapper.selectById(dto.getDefinitionId());
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());

        // Deploy to Flowable engine
        String deploymentName = definition.getDefinitionKey() + "_v" + definition.getVersion();
        Deployment deployment = repositoryService.createDeployment()
                .name(deploymentName)
                .addString(definition.getDefinitionKey() + ".bpmn20.xml", dto.getBpmnXml())
                .deploy();

        log.info("Deployed BPMN to Flowable: deploymentId={}, key={}", deployment.getId(), definition.getDefinitionKey());

        // Update definition in DB
        definition.setBpmnXml(dto.getBpmnXml());
        definition.setStatus(1); // Active
        definition.setDeployTime(LocalDateTime.now());
        definitionMapper.updateById(definition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspend(Long definitionId) {
        AssertUtil.notNull(definitionId, "流程定义ID不能为空");
        FlowDefinition definition = definitionMapper.selectById(definitionId);
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());

        if (definition.getStatus() == null || definition.getStatus() != 1) {
            throw new BizException("只有已激活的流程定义才能挂起");
        }

        // Suspend in Flowable
        List<ProcessDefinition> processDefs = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(definition.getDefinitionKey())
                .active()
                .list();
        for (ProcessDefinition pd : processDefs) {
            repositoryService.suspendProcessDefinitionById(pd.getId(), true, null);
        }

        // Update status in DB
        definition.setStatus(2); // Suspended
        definitionMapper.updateById(definition);
        log.info("Suspended flow definition: {}", definitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activate(Long definitionId) {
        AssertUtil.notNull(definitionId, "流程定义ID不能为空");
        FlowDefinition definition = definitionMapper.selectById(definitionId);
        AssertUtil.notNull(definition, ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getCode(),
                ResultCodeEnum.WORKFLOW_DEF_NOT_FOUND.getMessage());

        if (definition.getStatus() == null || definition.getStatus() != 2) {
            throw new BizException("只有已挂起的流程定义才能激活");
        }

        // Activate in Flowable
        List<ProcessDefinition> processDefs = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(definition.getDefinitionKey())
                .suspended()
                .list();
        for (ProcessDefinition pd : processDefs) {
            repositoryService.activateProcessDefinitionById(pd.getId(), true, null);
        }

        // Update status in DB
        definition.setStatus(1); // Active
        definitionMapper.updateById(definition);
        log.info("Activated flow definition: {}", definitionId);
    }

    @Override
    public FlowDefinitionVO getDefinitionByKey(String key) {
        AssertUtil.notBlank(key, "流程定义标识不能为空");
        QueryWrapper<FlowDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq("definition_key", key);
        wrapper.orderByDesc("version");
        wrapper.last("LIMIT 1");
        FlowDefinition definition = definitionMapper.selectOne(wrapper);
        if (definition == null) {
            throw new NotFoundException("流程定义不存在: " + key);
        }
        return convertToVO(definition);
    }

    @Override
    public PageResult<FlowDefinitionVO> listDefinitions(Integer pageNum, Integer pageSize,
                                                         Long categoryId, Integer status) {
        Page<FlowDefinition> page = new Page<>(pageNum, pageSize);
        QueryWrapper<FlowDefinition> wrapper = new QueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("create_time");
        Page<FlowDefinition> result = definitionMapper.selectPage(page, wrapper);

        List<FlowDefinitionVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), voList, pageNum, pageSize);
    }

    /**
     * Convert entity to VO
     */
    private FlowDefinitionVO convertToVO(FlowDefinition definition) {
        FlowDefinitionVO vo = new FlowDefinitionVO();
        BeanUtils.copyProperties(definition, vo);

        // Load category name
        if (definition.getCategoryId() != null) {
            FlowCategory category = categoryMapper.selectById(definition.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }

        return vo;
    }
}
