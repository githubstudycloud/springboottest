package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "test_mongo")
public class MongoTestEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}
