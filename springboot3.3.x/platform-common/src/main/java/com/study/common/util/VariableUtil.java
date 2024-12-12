package com.study.common.util;

import com.study.common.service.VariableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 变量处理工具类
 */
public class VariableUtil {
    private static final Logger logger = LoggerFactory.getLogger(VariableUtil.class);
    private static final String VARIABLE_PREFIX = "${";
    private static final String VARIABLE_SUFFIX = "}";

    private static final Map<String, VariableProvider> providers = new ConcurrentHashMap<>();

    /**
     * 注册变量提供者
     */
    public static void registerProvider(String providerName, VariableProvider provider) {
        providers.put(providerName, provider);
        logger.info("Registered variable provider: {}", providerName);
    }

    /**
     * 解析变量
     */
    public static String resolveVariable(String text) {
        if (text == null || !text.contains(VARIABLE_PREFIX)) {
            return text;
        }

        String result = text;
        int startIndex;
        int endIndex;

        while ((startIndex = result.indexOf(VARIABLE_PREFIX)) >= 0
                && (endIndex = result.indexOf(VARIABLE_SUFFIX, startIndex)) >= 0) {

            String variable = result.substring(startIndex + VARIABLE_PREFIX.length(), endIndex);
            String[] parts = variable.split(":", 2);
            String providerName = parts[0];
            String key = parts.length > 1 ? parts[1] : "";

            VariableProvider provider = providers.get(providerName);
            if (provider != null) {
                String value = provider.getVariableValue(key);
                if (value != null) {
                    result = result.substring(0, startIndex) + value
                            + result.substring(endIndex + VARIABLE_SUFFIX.length());
                }
            } else {
                logger.warn("Variable provider not found: {}", providerName);
            }
        }

        return result;
    }

    /**
     * 解析Map中的所有变量
     */
    public static Map<String, String> resolveVariables(Map<String, String> map) {
        Map<String, String> result = new ConcurrentHashMap<>();
        map.forEach((k, v) -> result.put(k, resolveVariable(v)));
        return result;
    }
}

