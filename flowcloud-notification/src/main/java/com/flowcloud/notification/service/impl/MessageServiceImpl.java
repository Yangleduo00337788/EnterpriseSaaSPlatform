package com.flowcloud.notification.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.notification.entity.SysMessage;
import com.flowcloud.notification.mapper.SysMessageMapper;
import com.flowcloud.notification.service.MessageService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final SysMessageMapper messageMapper;

    @Override
    public void sendMessage(Long tenantId, Long userId, String title, String content, String bizType, Long bizId) {
        SysMessage message = new SysMessage();
        message.setTenantId(tenantId);
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType("system");
        message.setBizType(bizType);
        message.setBizId(bizId);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }

    @Override
    public PageResult<SysMessage> pageMessages(int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(SysMessage::getTenantId).eq(TenantContext.getTenantId())
                .and(SysMessage::getUserId).eq(TenantContext.getUserId());
        query.orderBy(SysMessage::getCreateTime, false);
        Page<SysMessage> page = messageMapper.paginate(pageNum, pageSize, query);
        List<SysMessage> records = page.getRecords();
        return PageResult.of(records, page.getTotalRow(), pageNum, pageSize);
    }

    @Override
    public void markRead(Long id) {
        SysMessage message = messageMapper.selectOneById(id);
        if (message != null
                && message.getTenantId().equals(TenantContext.getTenantId())
                && message.getUserId().equals(TenantContext.getUserId())) {
            message.setIsRead(1);
            messageMapper.update(message);
        }
    }

    @Override
    public void markAllRead() {
        List<SysMessage> unread = messageMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysMessage::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysMessage::getUserId).eq(TenantContext.getUserId())
                        .and(SysMessage::getIsRead).eq(0));
        for (SysMessage message : unread) {
            message.setIsRead(1);
            messageMapper.update(message);
        }
    }

    @Override
    public void markBatchRead(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<SysMessage> messages = messageMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysMessage::getId).in(ids)
                        .and(SysMessage::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysMessage::getUserId).eq(TenantContext.getUserId())
                        .and(SysMessage::getIsRead).eq(0));
        for (SysMessage message : messages) {
            message.setIsRead(1);
            messageMapper.update(message);
        }
    }

    @Override
    public long countUnread() {
        return messageMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(SysMessage::getTenantId).eq(TenantContext.getTenantId())
                        .and(SysMessage::getUserId).eq(TenantContext.getUserId())
                        .and(SysMessage::getIsRead).eq(0));
    }
}
