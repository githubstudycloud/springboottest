package com.study.collect.common.utils.data;

// 数据校验工具

import com.study.collect.common.exception.data.ValidateException;
import com.study.collect.common.utils.common.StringUtils;

import java.util.Collection;

/**
 * 验证工具类
 */
public final class ValidateUtils {
    private ValidateUtils() {}

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ValidateException(message);
        }
    }

    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new ValidateException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidateException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ValidateException(message);
        }
    }
}