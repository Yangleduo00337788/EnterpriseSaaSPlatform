package com.flowx.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowx.common.core.result.PageResult;
import com.flowx.common.util.AssertUtil;
import com.flowx.message.convert.MsgNotificationConvert;
import com.flowx.message.dto.MsgNotificationQueryDTO;
import com.flowx.message.dto.SendNotificationDTO;
import com.flowx.message.entity.MsgNotification;
import com.flowx.message.mapper.MsgNotificationMapper;
import com.flowx.message.service.MsgNotificationService;
import com.flowx.message.vo.MsgNotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * In-app notification service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MsgNotificationServiceImpl implements MsgNotificationService {

    private final MsgNotificationMapper notificationMapper;
    private final MsgNotificationConvert notificationConvert;

    @Override
    public void sendNotification(SendNotificationDTO dto) {
        AssertUtil.notNull(dto, "通知数据不能为空");
        AssertUtil.notNull(dto.getUserId(), "用户ID不能为空");

        MsgNotification notification = notificationConvert.toEntity(dto);
        notification.setReadStatus(0); // unread
        notificationMapper.insert(notification);
        log.info("Sent in-app notification to user: {}", dto.getUserId());
    }

    @Override
    public PageResult<MsgNotificationVO> getMyNotifications(MsgNotificationQueryDTO queryDTO) {
        AssertUtil.notNull(queryDTO, "查询参数不能为空");
        AssertUtil.notNull(queryDTO.getUserId(), "用户ID不能为空");

        Page<MsgNotification> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        QueryWrapper<MsgNotification> wrapper = new QueryWrapper<>();

        wrapper.eq("user_id", queryDTO.getUserId());

        if (queryDTO.getReadStatus() != null) {
            wrapper.eq("read_status", queryDTO.getReadStatus());
        }
        if (queryDTO.getMsgType() != null) {
            wrapper.eq("msg_type", queryDTO.getMsgType());
        }

        wrapper.orderByDesc("create_time");

        Page<MsgNotification> notificationPage = notificationMapper.selectPage(page, wrapper);
        List<MsgNotificationVO> voList = notificationConvert.toVOList(notificationPage.getRecords());

        return PageResult.of(notificationPage.getTotal(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public void markAsRead(Long notificationId) {
        AssertUtil.notNull(notificationId, "通知ID不能为空");

        UpdateWrapper<MsgNotification> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", notificationId)
                .set("read_status", 1)
                .set("read_time", LocalDateTime.now());

        notificationMapper.update(null, wrapper);
        log.info("Marked notification as read: {}", notificationId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");

        UpdateWrapper<MsgNotification> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("read_status", 0)
                .set("read_status", 1)
                .set("read_time", LocalDateTime.now());

        notificationMapper.update(null, wrapper);
        log.info("Marked all notifications as read for user: {}", userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");
        return notificationMapper.countUnread(userId);
    }
}
