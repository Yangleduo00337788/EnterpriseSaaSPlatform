package com.flowx.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface MsgNotificationMapper extends BaseMapper<MsgNotification> {

    /**
     * Count unread notifications for a user
     *
     * @param userId user ID
     * @return unread count
     */
    int countUnread(@Param("userId") Long userId);
}
