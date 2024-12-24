package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskLogMapper {

    void insert(TaskLog log);

    void batchInsert(@Param("logs") List<TaskLog> logs);

    List<TaskLog> selectByInstanceId(@Param("instanceId") String instanceId);

    List<TaskLog> selectByTaskCode(@Param("taskCode") String taskCode,
                                   @Param("logType") Integer logType,
                                   @Param("limit") Integer limit);
}