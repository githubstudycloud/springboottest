package com.study.collect.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * 基础仓储接口
 */
public interface BaseRepository<T, ID> {
    /**
     * 保存实体
     */
    T save(T entity);

    /**
     * 批量保存
     */
    List<T> saveAll(Iterable<T> entities);

    /**
     * 根据ID删除
     */
    void deleteById(ID id);

    /**
     * 根据ID查询
     */
    Optional<T> findById(ID id);

    /**
     * 查询所有
     */
    List<T> findAll();
}

