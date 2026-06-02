package com.flowx.file.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * File query DTO for paginated search
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileQueryDTO extends PageQuery {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * File name filter (fuzzy match)
     */
    private String fileName;

    /**
     * File type filter
     */
    private String fileType;

    /**
     * Upload user ID filter
     */
    private Long uploadUserId;
}
