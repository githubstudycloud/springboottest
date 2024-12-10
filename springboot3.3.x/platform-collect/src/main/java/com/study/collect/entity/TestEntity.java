package com.study.collect.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TestEntity implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}

