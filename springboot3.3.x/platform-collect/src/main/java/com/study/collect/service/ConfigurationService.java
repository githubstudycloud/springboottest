package com.study.collect.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigurationService {

    private static final String CONFIG_KEY_PREFIX = "collect:config:";
    private final Map<String, ConfigChangeListener> listeners = new ConcurrentHashMap<>();
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void setConfig(String key, Object value) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        String oldValue = getConfigValue(key);

        redisTemplate.opsForValue().set(redisKey, objectMapper.valueToTree(value).toString());

        // 通知配置变更
        notifyConfigChange(key, oldValue, value);
    }

    public <T> Optional<T> getConfig(String key, Class<T> type) {
        String value = getConfigValue(key);
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(value, type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getConfigValue(String key) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(redisKey);
        return value != null ? value.toString() : null;
    }

    public void deleteConfig(String key) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        String oldValue = getConfigValue(key);

        redisTemplate.delete(redisKey);

        // 通知配置删除
        notifyConfigChange(key, oldValue, null);
    }

    public void addListener(String key, ConfigChangeListener listener) {
        listeners.put(key, listener);
    }

    public void removeListener(String key) {
        listeners.remove(key);
    }

    private void notifyConfigChange(String key, String oldValue, Object newValue) {
        ConfigChangeListener listener = listeners.get(key);
        if (listener != null) {
            ConfigChangeEvent event = new ConfigChangeEvent(key, oldValue, newValue);
            listener.onConfigChange(event);
        }
    }

    public interface ConfigChangeListener {
        void onConfigChange(ConfigChangeEvent event);
    }

    @Data
    public static class ConfigChangeEvent {
        private final String key;
        private final String oldValue;
        private final Object newValue;

        public ConfigChangeEvent(String key, String oldValue, Object newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}