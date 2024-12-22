package com.study.collect.business.enterprise.repository;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.repository.IRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EnterpriseRepository extends IRepository<Enterprise, String>, MongoRepository<Enterprise, String> {
    Enterprise findByCode(String code);
}
