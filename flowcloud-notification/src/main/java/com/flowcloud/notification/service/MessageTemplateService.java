package com.flowcloud.notification.service;

import com.flowcloud.common.event.ApprovalEvent;
import com.flowcloud.notification.dto.MessageTemplateDTO;
import com.flowcloud.notification.entity.SysMessageTemplate;

import java.util.List;

public interface MessageTemplateService {

    List<SysMessageTemplate> listAll();

    SysMessageTemplate getById(Long id);

    void create(MessageTemplateDTO dto);

    void update(MessageTemplateDTO dto);

    void delete(Long id);

    String renderTitle(ApprovalEvent event);

    String renderContent(ApprovalEvent event);
}
