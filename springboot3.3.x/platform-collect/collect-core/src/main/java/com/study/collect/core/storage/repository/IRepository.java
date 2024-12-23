package com.study.collect.core.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface IRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
    /**
     * 根据业务编码查询
     */
    T findByCode(String code);

    /**
     * 批量更新状态
     */
    void updateStatus(ID id, String status);

    /**
     * 统计状态数量
     */
    long countByStatus(String status);

    /**
     * 软删除
     */
    void softDelete(ID id);
}