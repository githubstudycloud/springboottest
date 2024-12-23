package com.study.collect.core.task.splitter;

import com.study.collect.core.task.CollectTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// 1. 任务分片接口
public interface TaskSplitter {
    List<CollectTask> split(CollectTask task, int shardCount);
}

