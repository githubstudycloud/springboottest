package com.study.collect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "test_collection")
public class TestDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}
