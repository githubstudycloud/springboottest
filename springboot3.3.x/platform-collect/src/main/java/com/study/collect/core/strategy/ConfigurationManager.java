package com.study.collect.core.strategy;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.naming.ConfigurationException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigurationManager {

    private final ConfigRepository configRepository;
    private final ConfigValidator configValidator;
    private final ConfigEncryptor configEncryptor;
    private final ConfigChangeNotifier configChangeNotifier;
    private final MetricsCollector metricsCollector;

    // 本地配置缓存
    private final Cache<String, ConfigurationItem> configCache;

    /**
     * 加载配置
     */
    public ConfigurationItem loadConfiguration(String key) {
        Timer.Sample timer = metricsCollector.startTimer("config_load");

        try {
            // 1. 从本地缓存获取
            ConfigurationItem cachedConfig = configCache.getIfPresent(key);
            if (cachedConfig != null && !isExpired(cachedConfig)) {
                return cachedConfig;
            }

            // 2. 从存储加载
            ConfigurationItem config = configRepository.load(key);
            if (config == null) {
                return null;
            }

            // 3. 解密敏感配置
            decryptSensitiveConfig(config);

            // 4. 更新缓存
            configCache.put(key, config);

            return config;

        } catch (Exception e) {
            log.error("Load configuration failed: {}", key, e);
            throw new ConfigurationException(
                    "Failed to load configuration", e);
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 更新配置
     */
    public void updateConfiguration(
            ConfigurationItem config) {
        String key = config.getKey();
        Timer.Sample timer = metricsCollector.startTimer("config_update");

        try {
            // 1. 验证配置
            configValidator.validate(config);

            // 2. 加密敏感配置
            encryptSensitiveConfig(config);

            // 3. 保存配置
            configRepository.save(config);

            // 4. 清除本地缓存
            configCache.invalidate(key);

            // 5. 发送变更通知
            notifyConfigChange(config);

        } catch (Exception e) {
            log.error("Update configuration failed: {}", key, e);
            throw new ConfigurationException(
                    "Failed to update configuration", e);
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 配置热更新
     */
    @EventListener(ConfigChangeEvent.class)
    public void handleConfigChange(ConfigChangeEvent event) {
        String key = event.getKey();
        log.info("Handling configuration change: {}", key);

        try {
            // 1. 验证变更事件
            if (!isValidConfigChange(event)) {
                return;
            }

            // 2. 重新加载配置
            ConfigurationItem newConfig = loadConfiguration(key);

            // 3. 通知相关组件
            notifyComponents(key, newConfig);

            // 4. 记录变更
            logConfigChange(event);

        } catch (Exception e) {
            log.error("Handle config change failed: {}", key, e);
        }
    }

    /**
     * 敏感配置解密
     */
    private void decryptSensitiveConfig(ConfigurationItem config) {
        if (!config.containsSensitiveData()) {
            return;
        }

        try {
            Map<String, Object> decryptedValues = new HashMap<>();

            for (Map.Entry<String, Object> entry :
                    config.getValues().entrySet()) {
                if (isSensitiveField(entry.getKey())) {
                    Object decryptedValue = configEncryptor
                            .decrypt(entry.getValue().toString());
                    decryptedValues.put(entry.getKey(), decryptedValue);
                } else {
                    decryptedValues.put(
                            entry.getKey(), entry.getValue());
                }
            }

            config.setValues(decryptedValues);

        } catch (Exception e) {
            log.error("Decrypt sensitive config failed", e);
            throw new ConfigurationException(
                    "Failed to decrypt sensitive configuration", e);
        }
    }

    /**
     * 通知配置变更
     */
    private void notifyConfigChange(ConfigurationItem config) {
        ConfigChangeEvent event = ConfigChangeEvent.builder()
                .key(config.getKey())
                .type(ConfigChangeType.UPDATE)
                .timestamp(LocalDateTime.now())
                .version(config.getVersion())
                .build();

        configChangeNotifier.notify(event);
    }

    /**
     * 检查配置是否过期
     */
    private boolean isExpired(ConfigurationItem config) {
        if (config.getTtl() <= 0) {
            return false;
        }

        return Duration.between(
                config.getUpdateTime(),
                LocalDateTime.now()
        ).toSeconds() > config.getTtl();
    }
}