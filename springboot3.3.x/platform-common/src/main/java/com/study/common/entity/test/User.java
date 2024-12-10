package com.study.common.entity.test;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;

    public User() {
    }

    // 构造方法、getter、setter等

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
