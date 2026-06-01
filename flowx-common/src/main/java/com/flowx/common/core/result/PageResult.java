package com.flowx.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Paginated response result
 *
 * @param <T> data type
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total record count
     */
    private long total;

    /**
     * Data list
     */
    private List<T> list;

    /**
     * Current page number
     */
    private int pageNum;

    /**
     * Page size
     */
    private int pageSize;

    /**
     * Create empty page result
     *
     * @param <T> data type
     * @return empty page result
     */
    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .total(0)
                .list(Collections.emptyList())
                .pageNum(1)
                .pageSize(10)
                .build();
    }

    /**
     * Create page result
     *
     * @param total    total count
     * @param list     data list
     * @param pageNum  page number
     * @param pageSize page size
     * @param <T>      data type
     * @return page result
     */
    public static <T> PageResult<T> of(long total, List<T> list, int pageNum, int pageSize) {
        return PageResult.<T>builder()
                .total(total)
                .list(list)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
    }

    /**
     * Get total pages
     *
     * @return total pages
     */
    public long getTotalPages() {
        if (pageSize <= 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }
}
