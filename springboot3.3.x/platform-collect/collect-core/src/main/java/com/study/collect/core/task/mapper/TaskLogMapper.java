package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskLogMapper {
    void insert(TaskLog log);
    void batchInsert(@Param("logs") List<TaskLog> logs);
    List<TaskLog> selectByInstanceId(String instanceId);
    List<TaskLog> selectByTaskCode(@Param("taskCode") String taskCode,
                                   @Param("logType") Integer logType,
                                   @Param("limit") Integer limit);
    List<TaskLog> selectLatestErrors(@Param("limit") int limit);
    int countLogs(@Param("taskCode") String taskCode,
                  @Param("logType") Integer logType);
    int cleanHistoryLogs(@Param("daysBefore") int daysBefore);
    void deleteByInstanceId(String instanceId);
}