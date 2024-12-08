package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "tree_collect_tasks")
public class TreeCollectTask {
    @Id
    private String id;
    private String projectId;
    private TaskStatus status;
    private int totalCount;
    private int currentCount;
    private List<String> errors;
    private Date startTime;
    private Date endTime;
    private Date createTime;
}
