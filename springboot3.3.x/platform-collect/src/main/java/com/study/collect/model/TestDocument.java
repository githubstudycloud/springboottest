package com.study.collect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
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
