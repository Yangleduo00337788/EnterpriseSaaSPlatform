package com.flowx.message.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.message.dto.MsgNotificationQueryDTO;
import com.flowx.message.dto.SendNotificationDTO;
import com.flowx.message.vo.MsgNotificationVO;

/**
 * In-app notification service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface MsgNotificationService {

    /**
     * Send (create) an in-app notification
     *
     * @param dto notification data
     */
    void sendNotification(SendNotificationDTO dto);

    /**
     * Get paginated notifications for current user
     *
     * @param queryDTO query parameters
     * @return paginated notification list
     */
    PageResult<MsgNotificationVO> getMyNotifications(MsgNotificationQueryDTO queryDTO);

    /**
     * Mark a single notification as read
     *
     * @param notificationId notification ID
     */
    void markAsRead(Long notificationId);

    /**
     * Mark all notifications as read for a user
     *
     * @param userId user ID
     */
    void markAllAsRead(Long userId);

    /**
     * Get unread notification count for a user
     *
     * @param userId user ID
     * @return unread count
     */
    int getUnreadCount(Long userId);
}
