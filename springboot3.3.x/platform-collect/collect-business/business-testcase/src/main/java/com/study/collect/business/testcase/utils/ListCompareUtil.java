package com.study.collect.business.testcase.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ListCompareUtil {

    /**
     * 比较两个列表，找出在B中有但在A中没有的元素
     */
    public static <T> List<T> findMissingInA(List<T> listA, List<T> listB) {
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>(listB);
        }

        Set<T> setA = new HashSet<>(listA);
        return listB.stream()
                .filter(item -> !setA.contains(item))
                .collect(Collectors.toList());
    }

    /**
     * 根据指定字段比较两个列表，找出在B中有但在A中没有的元素
     */
    public static <T, R> List<T> findMissingInA(List<T> listA, List<T> listB,
                                                Function<T, R> keyExtractor) {
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>(listB);
        }

        Set<R> keysA = listA.stream()
                .map(keyExtractor)
                .collect(Collectors.toSet());

        return listB.stream()
                .filter(item -> !keysA.contains(keyExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 找出两个列表的交集
     */
    public static <T> List<T> findIntersection(List<T> listA, List<T> listB) {
        if (CollectionUtils.isEmpty(listA) || CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }

        Set<T> setB = new HashSet<>(listB);
        return listA.stream()
                .filter(setB::contains)
                .collect(Collectors.toList());
    }

    /**
     * 根据指定字段找出两个列表的交集
     */
    public static <T, R> List<T> findIntersection(List<T> listA, List<T> listB,
                                                  Function<T, R> keyExtractor) {
        if (CollectionUtils.isEmpty(listA) || CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }

        Set<R> keysB = listB.stream()
                .map(keyExtractor)
                .collect(Collectors.toSet());

        return listA.stream()
                .filter(item -> keysB.contains(keyExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 找出两个列表的差集（在A中但不在B中的元素）
     */
    public static <T> List<T> findDifference(List<T> listA, List<T> listB) {
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>(listA);
        }

        Set<T> setB = new HashSet<>(listB);
        return listA.stream()
                .filter(item -> !setB.contains(item))
                .collect(Collectors.toList());
    }

    /**
     * 根据指定字段找出两个列表的差集
     */
    public static <T, R> List<T> findDifference(List<T> listA, List<T> listB,
                                                Function<T, R> keyExtractor) {
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>(listA);
        }

        Set<R> keysB = listB.stream()
                .map(keyExtractor)
                .collect(Collectors.toSet());

        return listA.stream()
                .filter(item -> !keysB.contains(keyExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 分页处理列表
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}

