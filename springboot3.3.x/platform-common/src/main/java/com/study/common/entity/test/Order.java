package com.study.common.entity.test;

import lombok.Data;

@Data
public class Order {

    private String orderId;
    private Integer amount;

    public Order() {
    }
    // 构造方法、getter、setter等

    public Order(String orderId, Integer amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}