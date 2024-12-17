package com.study.collect.common.utils.common;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 */
public final class StringUtils {
    private StringUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(separator));
    }
}