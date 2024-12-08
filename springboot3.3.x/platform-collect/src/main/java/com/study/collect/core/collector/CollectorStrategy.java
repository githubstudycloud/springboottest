package com.study.collect.core.collector;

import com.study.collect.annotation.CollectorFor;
import com.study.collect.enums.CollectorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CollectorStrategy {
    private final Map<CollectorType, Collector> collectorMap;

    public CollectorStrategy(List<Collector> collectors) {
        collectorMap = collectors.stream()
                .filter(collector -> collector.getClass().isAnnotationPresent(CollectorFor.class))
                .collect(Collectors.toMap(
                        collector -> collector.getClass().getAnnotation(CollectorFor.class).value(),
                        collector -> collector
                ));
    }

    public Collector getCollector(CollectorType type) {
        return collectorMap.getOrDefault(type, collectorMap.get(CollectorType.DEFAULT));
    }
}