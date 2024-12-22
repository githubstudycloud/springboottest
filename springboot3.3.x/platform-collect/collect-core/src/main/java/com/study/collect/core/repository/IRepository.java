package com.study.collect.core.repository;

public interface IRepository<T, ID> {
    /**
     * 保存数据
     */
    T save(T entity);

    /**
     * 根据ID查询
     */
    T findById(ID id);

    /**
     * 删除数据
     */
    void delete(ID id);
}