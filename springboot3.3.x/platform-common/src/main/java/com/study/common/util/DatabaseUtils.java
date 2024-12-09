package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 数据库工具类
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final ThreadLocal<String> DATASOURCE_HOLDER = new ThreadLocal<>();

    public static String getDataSource() {
        return DATASOURCE_HOLDER.get();
    }

    public static void setDataSource(String dataSource) {
        DATASOURCE_HOLDER.set(dataSource);
    }

    public static void clearDataSource() {
        DATASOURCE_HOLDER.remove();
    }

    public static <T> T executeWithDataSource(String dataSource, Supplier<T> supplier) {
        try {
            setDataSource(dataSource);
            return supplier.get();
        } finally {
            clearDataSource();
        }
    }

    public static void executeWithDataSource(String dataSource, Runnable runnable) {
        try {
            setDataSource(dataSource);
            runnable.run();
        } finally {
            clearDataSource();
        }
    }
}
