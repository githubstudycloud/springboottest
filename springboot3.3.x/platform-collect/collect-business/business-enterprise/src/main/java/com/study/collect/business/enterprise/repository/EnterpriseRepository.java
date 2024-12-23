package com.study.collect.business.enterprise.repository;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EnterpriseRepository extends IRepository<Enterprise, String>, MongoRepository<Enterprise, String> {
    Enterprise findByCode(String code);

    // 继承基础的版本方法
    List<Enterprise> findByVersion(String version);

    @Query("")
        // MongoDB查询
    List<Enterprise> findIncrementalData(String version);
}
