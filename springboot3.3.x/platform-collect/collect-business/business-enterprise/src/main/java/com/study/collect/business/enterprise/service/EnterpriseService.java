package com.study.collect.business.enterprise.service;

import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.processor.EnterpriseProcessor;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseCollector collector;
    private final EnterpriseProcessor processor;
    private final EnterpriseRepository repository;

    public Enterprise collectAndProcess(String code) {
        // 1. 采集数据
        Enterprise enterprise = collector.collect(code);
        if (enterprise == null) {
            return null;
        }

        // 2. 处理数据
        enterprise = processor.process(enterprise);

        // 3. 保存数据
        return repository.save(enterprise);
    }
}
