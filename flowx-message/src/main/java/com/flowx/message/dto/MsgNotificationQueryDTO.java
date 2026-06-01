package com.flowx.message.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Notification query DTO for paginated search
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MsgNotificationQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * User ID filter
     */
    private Long userId;

    /**
     * Read status filter: 0=unread, 1=read
     */
    private Integer readStatus;

    /**
     * Message type filter: 1=notice, 2=alert, 3=todo
     */
    private Integer msgType;
}
