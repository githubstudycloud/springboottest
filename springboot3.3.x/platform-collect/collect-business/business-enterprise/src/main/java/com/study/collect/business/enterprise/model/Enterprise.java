package com.study.collect.business.enterprise.model;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.entity.VersionEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "enterprise")
public class Enterprise extends VersionEntity {
    @Id
    private String id;

    private String code;           // 企业编码
    private String name;          // 企业名称
    private String address;       // 企业地址
    private String contact;       // 联系人
    private String phone;         // 联系电话
    private String industry;      // 所属行业
    private BigDecimal regCapital; // 注册资本
    private String regAuthority;   // 注册机构
    private LocalDate estDate;     // 成立日期

//    private LocalDateTime createTime;  // 创建时间
//    private LocalDateTime updateTime;  // 更新时间
//    private String version;       // 数据版本
//    private Boolean deleted;      // 是否删除
}