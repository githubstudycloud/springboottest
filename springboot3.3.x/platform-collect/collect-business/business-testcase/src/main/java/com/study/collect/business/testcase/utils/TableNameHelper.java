package com.study.collect.business.testcase.common.utils;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 表名处理工具类
 */
@Slf4j
public class TableNameHelper {

    private static final Map<String, String> TABLE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final int MAX_TABLE_NAME_LENGTH = 64;

    /**
     * 生成完整表名
     * @param rootNode 根节点
     * @return 完整表名
     */
    public static String getTableName(String rootNode) {
        Assert.hasText(rootNode, "RootNode must not be empty");

        return TABLE_NAME_CACHE.computeIfAbsent(rootNode, key -> {
            String tableName = CollectionConstants.Collection.URI_COLLECTION_PREFIX + "_" + key;
            validateTableName(tableName);
            return tableName;
        });
    }

    /**
     * 获取rootNode
     * @param uri URI
     * @return rootNode
     */
    public static String extractRootNode(String uri) {
        Assert.hasText(uri, "URI must not be empty");

        int firstSlash = uri.indexOf('/');
        if (firstSlash == -1) {
            return uri;
        }
        return uri.substring(0, firstSlash);
    }

    /**
     * 验证表名是否合法
     */
    private static void validateTableName(String tableName) {
        if (!TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        if (tableName.length() > MAX_TABLE_NAME_LENGTH) {
            throw new IllegalArgumentException("Table name too long: " + tableName);
        }
    }

    /**
     * 检查URI是否属于指定表
     * @param uri URI
     * @param tableName 表名
     * @return 是否属于
     */
    public static boolean isUriMatchTable(String uri, String tableName) {
        String rootNode = extractRootNode(uri);
        String expectedTableName = getTableName(rootNode);
        return expectedTableName.equals(tableName);
    }

    /**
     * 清除表名缓存
     */
    public static void clearCache() {
        TABLE_NAME_CACHE.clear();
    }
}