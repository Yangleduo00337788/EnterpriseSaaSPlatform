package com.flowx.message.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.common.util.SecurityUtil;
import com.flowx.message.dto.MsgNotificationQueryDTO;
import com.flowx.message.service.MsgNotificationService;
import com.flowx.message.vo.MsgNotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * In-app notification management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/message/notification")
@RequiredArgsConstructor
public class MsgNotificationController {

    private final MsgNotificationService notificationService;

    /**
     * Get current user's notifications with pagination
     *
     * @param queryDTO query parameters
     * @return paginated notification list
     */
    @GetMapping("/list")
    public R<PageResult<MsgNotificationVO>> getMyNotifications(MsgNotificationQueryDTO queryDTO) {
        // Set current user ID
        Long currentUserId = SecurityUtil.getUserId();
        queryDTO.setUserId(currentUserId);

        PageResult<MsgNotificationVO> result = notificationService.getMyNotifications(queryDTO);
        return R.ok(result);
    }

    /**
     * Mark a notification as read
     *
     * @param id notification ID
     * @return success response
     */
    @PutMapping("/{id}/read")
    public R<Void> markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return R.ok();
    }

    /**
     * Mark all notifications as read for current user
     *
     * @return success response
     */
    @PutMapping("/read-all")
    public R<Void> markAllAsRead() {
        Long currentUserId = SecurityUtil.getUserId();
        notificationService.markAllAsRead(currentUserId);
        return R.ok();
    }

    /**
     * Get unread notification count for current user
     *
     * @return unread count
     */
    @GetMapping("/unread-count")
    public R<Integer> getUnreadCount() {
        Long currentUserId = SecurityUtil.getUserId();
        int count = notificationService.getUnreadCount(currentUserId);
        return R.ok(count);
    }
}