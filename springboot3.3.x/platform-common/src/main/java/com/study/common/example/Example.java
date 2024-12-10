package com.study.common.example;

import com.study.common.entity.test.Order;
import com.study.common.entity.test.User;
import com.study.common.util.BeanSortUtils;

import java.util.Arrays;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        // 示例1：排序User对象
        List<User> users = Arrays.asList(
                new User(3L, "Alice"),
                new User(1L, "Bob"),
                new User(4L, "Charlie"),
                new User(2L, "David")
        );
        List<Long> userIds = Arrays.asList(1L, 2L, 3L, 4L);

        // 原地排序
        BeanSortUtils.sortByIds(users, userIds, User::getId);

        // 或者创建新的排序列表
        List<User> sortedUsers = BeanSortUtils.sortedByIds(users, userIds, User::getId);

        // 示例2：排序Order对象
        List<Order> orders = Arrays.asList(
                new Order("order3", 100),
                new Order("order1", 200),
                new Order("order2", 300)
        );
        List<String> orderIds = Arrays.asList("order1", "order2", "order3");

        // 使用方法引用
        BeanSortUtils.sortByIds(orders, orderIds, Order::getOrderId);

        // 或使用lambda表达式
        BeanSortUtils.sortByIds(orders, orderIds, order -> order.getOrderId());
    }
}

