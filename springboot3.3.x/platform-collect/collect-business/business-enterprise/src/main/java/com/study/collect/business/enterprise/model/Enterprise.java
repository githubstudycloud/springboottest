package com.study.collect.business.enterprise.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "enterprise")
public class Enterprise {
    @Id
    private String id;
    private String name;
    private String code;
    private String address;
    private String contact;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String version;
}