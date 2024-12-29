package com.study.collect.core.storage.repository;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface IRepository<T extends BaseEntity> extends MongoRepository<T, String> {
    /**
     * 根据业务编码查询
     */
    T findByCode(String code);

    /**
     * 分页查询未删除的数据
     */
    Page<T> findByDeletedFalse(Pageable pageable);

    /**
     * 根据版本号查询数据
     */
    List<T> findByVersionCodeGreaterThan(String versionCode);

    /**
     * 软删除
     */
    void softDelete(String id);

    /**
     * 批量软删除
     */
    void softDelete(List<String> ids);

    /**
     * 更新状态
     */
    void updateStatus(String id, String status);
}