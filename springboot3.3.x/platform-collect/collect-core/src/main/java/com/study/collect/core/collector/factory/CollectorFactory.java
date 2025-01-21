package com.study.collect.core.collector.factory;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.manager.CollectorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorFactory {

    private final CollectorManager collectorManager;

    /**
     * 创建或获取采集器实例
     * 首先尝试从CollectorManager获取已注册的采集器
     * 如果找不到对应的采集器，抛出异常
     */
    public <T, R> ICollector<T, R> createCollector(String type) {
        // 验证参数
        if (!StringUtils.hasText(type)) {
            throw new IllegalArgumentException("采集器类型不能为空");
        }

        try {
            // 从CollectorManager获取已注册的采集器
            return collectorManager.getCollector(type);
        } catch (IllegalStateException e) {
            log.error("创建采集器失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的采集器类型: " + type);
        }
    }

    /**
     * 检查是否支持指定类型的采集器
     */
    public boolean supportsCollectorType(String type) {
        return collectorManager.hasCollector(type);
    }

    /**
     * 获取所有支持的采集器类型
     */
    public Set<String> getSupportedCollectorTypes() {
        return collectorManager.getCollectorTypes();
    }
}