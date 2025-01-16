package com.study.collect.business.testcase.common.utils;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

import java.util.List;
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
     * 生成带版本的表名
     */
    public static String getVersionedTableName(String rootNode, String version) {
        Assert.hasText(rootNode, "RootNode must not be empty");
        Assert.hasText(version, "Version must not be empty");

        String cacheKey = rootNode + "_" + version;
        return TABLE_NAME_CACHE.computeIfAbsent(cacheKey, key -> {
            String tableName = CollectionConstants.Collection.URI_COLLECTION_PREFIX +
                    "_" + rootNode +
                    "_" + version;
            validateTableName(tableName);
            return tableName;
        });
    }

    /**
     * 从URI中提取rootNode
     */
    public static String extractRootNode(String uri) {
        Assert.hasText(uri, "URI must not be empty");
        int firstSlash = uri.indexOf('/');
        return firstSlash == -1 ? uri : uri.substring(0, firstSlash);
    }

    /**
     * 生成URI哈希值
     */
    public static String generateUriHash(String uri) {
        Assert.hasText(uri, "URI must not be empty");
        return DigestUtils.sha256Hex(uri);
    }

    /**
     * 生成完整的文档ID
     */
    public static String generateDocumentId(String uri, String version) {
        Assert.hasText(uri, "URI must not be empty");
        return version == null ?
                DigestUtils.sha256Hex(uri) :
                DigestUtils.sha256Hex(uri + "_" + version);
    }

    /**
     * 检查URI是否属于指定表
     */
    public static boolean isUriMatchTable(String uri, String tableName) {
        String rootNode = extractRootNode(uri);
        String expectedTableName = getTableName(rootNode);
        return expectedTableName.equals(tableName);
    }

    /**
     * 解析表名中的rootNode
     */
    public static String extractRootNodeFromTableName(String tableName) {
        Assert.hasText(tableName, "Table name must not be empty");
        String prefix = CollectionConstants.Collection.URI_COLLECTION_PREFIX + "_";
        if (!tableName.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid table name format: " + tableName);
        }
        String remaining = tableName.substring(prefix.length());
        int versionSeparator = remaining.indexOf('_');
        return versionSeparator == -1 ? remaining : remaining.substring(0, versionSeparator);
    }

    /**
     * 验证表名是否合法
     */
    private static void validateTableName(String tableName) {
        if (!TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new IllegalArgumentException("Invalid table name characters: " + tableName);
        }
        if (tableName.length() > MAX_TABLE_NAME_LENGTH) {
            throw new IllegalArgumentException("Table name too long: " + tableName);
        }
    }

    /**
     * 清除表名缓存
     */
    public static void clearCache() {
        TABLE_NAME_CACHE.clear();
    }

    /**
     * 预热表名缓存
     */
    public static void warmupCache(List<String> rootNodes) {
        for (String rootNode : rootNodes) {
            getTableName(rootNode);
        }
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return TABLE_NAME_CACHE.size();
    }

    /**
     * 验证表名格式
     */
    public static boolean isValidTableName(String tableName) {
        return TABLE_NAME_PATTERN.matcher(tableName).matches() &&
                tableName.length() <= MAX_TABLE_NAME_LENGTH;
    }
}