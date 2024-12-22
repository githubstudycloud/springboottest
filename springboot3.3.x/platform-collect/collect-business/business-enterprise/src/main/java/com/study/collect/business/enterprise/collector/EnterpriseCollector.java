package com.study.collect.business.enterprise.collector;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import com.study.collect.core.annotation.Collector;
import com.study.collect.core.collector.ICollector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Collector(type = "enterprise")
@Component
@RequiredArgsConstructor
public class EnterpriseCollector implements ICollector<String, Enterprise> {

    private final EnterpriseRepository repository;

    @Override
    public Enterprise collect(String code) {
        return repository.findByCode(code);
    }

    @Override
    public String getType() {
        return "enterprise";
    }
}
