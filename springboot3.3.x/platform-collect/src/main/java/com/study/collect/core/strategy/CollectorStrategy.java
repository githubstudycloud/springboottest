package com.study.collect.core.strategy;

import com.study.collect.annotation.CollectorFor;
import com.study.collect.core.collector.Collector;
import com.study.collect.enums.CollectorType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CollectorStrategy {
    private static final Logger logger = LoggerFactory.getLogger(CollectorStrategy.class);
    private final Map<CollectorType, Collector> collectorMap;

    public CollectorStrategy(List<Collector> collectors) {
        logger.info("Initializing CollectorStrategy with collectors: {}",
                collectors.stream().map(c -> c.getClass().getSimpleName()).collect(Collectors.toList()));

        collectorMap = collectors.stream()
                .filter(collector -> collector.getClass().isAnnotationPresent(CollectorFor.class))
                .collect(Collectors.toMap(
                        collector -> collector.getClass().getAnnotation(CollectorFor.class).value(),
                        collector -> collector
                ));

        logger.info("Initialized collector mappings: {}",
                collectorMap.entrySet().stream()
                        .map(e -> e.getKey() + " -> " + e.getValue().getClass().getSimpleName())
                        .collect(Collectors.toList()));
    }

    @PostConstruct
    public void init() {
        logger.info("=== Collector Strategy Initialization ===");
        logger.info("Available collector types: {}", CollectorType.values());
        logger.info("Registered collectors: {}",
                collectorMap.entrySet().stream()
                        .map(e -> e.getKey() + " -> " + e.getValue().getClass().getSimpleName())
                        .collect(Collectors.toList()));
        logger.info("=======================================");
    }

    public Collector getCollector(CollectorType type) {
        Collector collector = collectorMap.get(type);
        logger.debug("Getting collector for type {}, found: {}",
                type, collector != null ? collector.getClass().getSimpleName() : "null");
        return collector;
    }
}