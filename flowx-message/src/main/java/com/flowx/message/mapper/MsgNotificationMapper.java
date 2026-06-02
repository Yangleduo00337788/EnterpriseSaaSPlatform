package com.flowx.message.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.message.entity.MsgNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * In-app notification mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface MsgNotificationMapper extends FlexBaseMapper<MsgNotification> {

    /**
     * Count unread notifications for a user
     *
     * @param userId user ID
     * @return unread count
     */
    int countUnread(@Param("userId") Long userId);
}
