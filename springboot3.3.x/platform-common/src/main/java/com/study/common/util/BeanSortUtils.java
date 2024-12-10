package com.study.common.util;

import com.study.common.entity.UnmatchedStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Bean列表排序工具类
 */
public class BeanSortUtils {
    /**
     * 根据指定的id列表顺序对Bean列表进行排序
     *
     * @param beans      要排序的Bean列表
     * @param ids        id顺序列表
     * @param idFunction 从Bean中获取id的函数
     * @param <T>        Bean类型
     * @param <U>        id类型
     */
    public static <T, U> void sortByIds(List<T> beans, List<U> ids, Function<T, U> idFunction) {
        if (beans == null || ids == null || idFunction == null) {
            return;
        }

        // 创建id到位置的映射
        Map<U, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            idxMap.put(ids.get(i), i);
        }

        // 根据映射排序
        beans.sort((b1, b2) -> {
            Integer idx1 = idxMap.get(idFunction.apply(b1));
            Integer idx2 = idxMap.get(idFunction.apply(b2));

            // 如果两个id都存在于ids列表中，按位置排序
            if (idx1 != null && idx2 != null) {
                return idx1.compareTo(idx2);
            }
            // 如果id不在ids列表中，放到最后
            if (idx1 == null && idx2 == null) {
                return 0;
            }
            return idx1 == null ? 1 : -1;
        });
    }

    /**
     * 根据指定的id列表顺序对Bean列表进行排序，并返回新的已排序列表
     *
     * @param beans      要排序的Bean列表
     * @param ids        id顺序列表
     * @param idFunction 从Bean中获取id的函数
     * @param <T>        Bean类型
     * @param <U>        id类型
     * @return 排序后的新列表
     */
    public static <T, U> List<T> sortedByIds(List<T> beans, List<U> ids, Function<T, U> idFunction) {
        if (beans == null) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(beans);
        sortByIds(result, ids, idFunction);
        return result;
    }

    /**
     * 根据指定的id列表顺序对Bean列表进行排序，并指定未匹配项的处理策略
     */
    public static <T, U> void sortByIds(List<T> beans, List<U> ids,
                                        Function<T, U> idFunction, UnmatchedStrategy unmatchedStrategy) {
        if (beans == null || ids == null || idFunction == null) {
            return;
        }

        Map<U, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            idxMap.put(ids.get(i), i);
        }

        beans.sort((b1, b2) -> {
            Integer idx1 = idxMap.get(idFunction.apply(b1));
            Integer idx2 = idxMap.get(idFunction.apply(b2));

            if (idx1 != null && idx2 != null) {
                return idx1.compareTo(idx2);
            }

            return switch (unmatchedStrategy) {
                case KEEP_AT_END -> idx1 == null ? 1 : -1;
                case KEEP_AT_START -> idx1 == null ? -1 : 1;
                case KEEP_ORIGINAL -> 0;
            };
        });
    }
}