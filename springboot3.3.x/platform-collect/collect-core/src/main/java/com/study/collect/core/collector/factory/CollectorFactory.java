package com.study.collect.core.collector.factory;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.manager.CollectorManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectorFactory {

    private final CollectorManager collectorManager;

    public <T, R> ICollector<T, R> createCollector(String type) {
        return collectorManager.getCollector(type);
    }
}