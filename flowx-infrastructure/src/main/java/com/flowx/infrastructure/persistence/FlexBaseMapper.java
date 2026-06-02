package com.flowx.infrastructure.persistence;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * {@link BaseMapper} extension with MyBatis-Plus compatible method aliases
 * for incremental migration to MyBatis-Flex.
 */
public interface FlexBaseMapper<T> extends BaseMapper<T> {

    default long selectCount(QueryWrapper queryWrapper) {
        return selectCountByQuery(queryWrapper);
    }

    default List<T> selectList(QueryWrapper queryWrapper) {
        return selectListByQuery(queryWrapper);
    }

    default T selectOne(QueryWrapper queryWrapper) {
        return selectOneByQuery(queryWrapper);
    }

    default int updateById(T entity) {
        return update(entity);
    }
}
