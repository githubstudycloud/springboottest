package com.study.collect.core.collector.manager;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CollectorManager {

    private final Map<String, ICollector<?, ?>> collectors = new ConcurrentHashMap<>();
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        registerCollectors();
    }

    /**
     * 扫描并注册所有带有@Collector注解的采集器
     */
    private void registerCollectors() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Collector.class);
        beans.values().forEach(bean -> {
            Collector annotation = bean.getClass().getAnnotation(Collector.class);
            if (null != annotation && annotation.enabled()) {
                ICollector<?, ?> collector = (ICollector<?, ?>) bean;
                registerCollector(collector.getType(), collector);
            }
        });
    }

    /**
     * 注册单个采集器
     */
    public void registerCollector(String type, ICollector<?, ?> collector) {
        if (collectors.containsKey(type)) {
            throw new IllegalStateException("采集器类型已存在: " + type);
        }
        collectors.put(type, collector);
        log.info("注册采集器: type={}, class={}", type, collector.getClass().getName());
    }

    /**
     * 检查采集器是否已注册
     */
    public boolean hasCollector(String type) {
        return collectors.containsKey(type);
    }

    /**
     * 获取已注册的采集器
     * 如果采集器未注册，抛出异常
     */
    @SuppressWarnings("unchecked")
    public <T, R> ICollector<T, R> getCollector(String type) {
        ICollector<?, ?> collector = collectors.get(type);
        if (collector == null) {
            throw new IllegalStateException("采集器未注册: " + type);
        }
        return (ICollector<T, R>) collector;
    }

    /**
     * 获取所有已注册的采集器类型
     */
    public Set<String> getCollectorTypes() {
        return Collections.unmodifiableSet(collectors.keySet());
    }
}