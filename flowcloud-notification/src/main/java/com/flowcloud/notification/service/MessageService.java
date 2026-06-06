package com.flowcloud.notification.service;

import com.flowcloud.common.result.PageResult;
import com.flowcloud.notification.entity.SysMessage;

public interface MessageService {

    void sendMessage(Long tenantId, Long userId, String title, String content, String bizType, Long bizId);

    PageResult<SysMessage> pageMessages(int pageNum, int pageSize);

    void markRead(Long id);

    void markAllRead();

    void markBatchRead(java.util.List<Long> ids);

    long countUnread();
}
