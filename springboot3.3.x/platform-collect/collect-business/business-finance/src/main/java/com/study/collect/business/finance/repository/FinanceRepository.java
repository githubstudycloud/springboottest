package com.study.collect.business.finance.repository;


import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.repository.IRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FinanceRepository extends IRepository<FinanceData, String>, MongoRepository<FinanceData, String> {
    FinanceData findByCode(String code);
}
