package com.study.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 集合工具类
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (isEmpty(list)) {
            return List.of();
        }
        int totalSize = list.size();
        int partitionCount = (totalSize + size - 1) / size;
        return IntStream.range(0, partitionCount)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, totalSize)))
                .collect(Collectors.toList());
    }
}
