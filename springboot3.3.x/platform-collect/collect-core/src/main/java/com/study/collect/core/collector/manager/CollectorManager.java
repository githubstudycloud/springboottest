package com.study.collect.core.collector.manager;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CollectorManager {

    private final Map<String, ICollector<?, ?>> collectors = new ConcurrentHashMap<>();

    @Autowired
    public void registerCollectors(Map<String, Object> beans) {
        beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Collector.class))
                .forEach(bean -> {
                    Collector annotation = bean.getClass().getAnnotation(Collector.class);
                    if (annotation.enabled()) {
                        ICollector<?, ?> collector = (ICollector<?, ?>) bean;
                        collectors.put(collector.getType(), collector);
                        log.info("注册采集器: type={}, class={}",
                                collector.getType(), collector.getClass().getName());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T, R> ICollector<T, R> getCollector(String type) {
        ICollector<?, ?> collector = collectors.get(type);
        if (collector == null) {
            throw new IllegalArgumentException("未找到采集器: " + type);
        }
        return (ICollector<T, R>) collector;
    }
}
