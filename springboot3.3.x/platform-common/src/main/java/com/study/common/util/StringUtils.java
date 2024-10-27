package com.study.common.util;

/**
 * 字符串工具类
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String toSnakeCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '_') {
                nextUpper = true;
            } else {
                result.append(nextUpper ? Character.toUpperCase(ch) : ch);
                nextUpper = false;
            }
        }
        return result.toString();
    }
}
