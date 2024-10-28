package com.study.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

/**
 * JSON工具类 - 加强版
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            // 添加Java8时间模块
            .addModule(new JavaTimeModule())
            // 设置时区
            .defaultTimeZone(TimeZone.getDefault())
            // 禁用DateTime时间戳
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // 忽略未知属性
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 空对象不报错
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // 启用清理机制，防止JSON注入
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build();

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object object) {
        try {
            return object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Convert object to json failed", e);
            return null;
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Parse json to object failed", e);
            return null;
        }
    }

    /**
     * JSON字符串转复杂对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("Parse json to complex object failed", e);
            return null;
        }
    }

    /**
     * 对象转字节数组
     */
    public static byte[] toBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("Convert object to bytes failed", e);
            return new byte[0];
        }
    }

    /**
     * 字节数组转对象
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            logger.error("Parse bytes to object failed", e);
            return null;
        }
    }

    /**
     * 判断是否为有效的JSON字符串
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 格式化JSON字符串
     */
    public static String formatJson(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Format json failed", e);
            return json;
        }
    }

    /**
     * 对象深度克隆
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T object) {
        try {
            if (object == null) {
                return null;
            }
            return (T) objectMapper.readValue(
                    objectMapper.writeValueAsString(object),
                    object.getClass()
            );
        } catch (JsonProcessingException e) {
            logger.error("Deep copy object failed", e);
            return null;
        }
    }
}