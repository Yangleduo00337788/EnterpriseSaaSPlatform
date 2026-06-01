package com.flowx.common.core.page;

import lombok.Data;

import java.io.Serializable;

/**
 * Page query base class
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Page number, default 1
     */
    private Integer pageNum = 1;

    /**
     * Page size, default 10
     */
    private Integer pageSize = 10;

    /**
     * Order by field
     */
    private String orderBy;

    /**
     * Ascending order flag, default true
     */
    private Boolean isAsc = true;

    /**
     * Get offset for SQL limit
     *
     * @return offset
     */
    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }

    /**
     * Get order by clause
     *
     * @return order by clause or null
     */
    public String getOrderByClause() {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return null;
        }
        String direction = Boolean.TRUE.equals(isAsc) ? "ASC" : "DESC";
        return orderBy + " " + direction;
    }
}
