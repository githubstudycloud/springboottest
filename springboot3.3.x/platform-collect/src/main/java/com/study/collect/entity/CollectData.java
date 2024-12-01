package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "collect_data")
public class CollectData {
    @Id
    private String id;

    private Long mysqlId; // MySQL中的ID
    private String deviceId;
    private String deviceName;
    private Double temperature;
    private Double humidity;
    private String location;
    private Date collectTime;
    private Date createTime;
}