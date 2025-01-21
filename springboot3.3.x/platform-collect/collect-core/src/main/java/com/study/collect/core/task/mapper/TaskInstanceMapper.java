package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskInstanceMapper {
    void insert(TaskInstance instance);

    void updateStatus(@Param("instanceId") String instanceId,
                      @Param("status") Integer status,
                      @Param("errorMsg") String errorMsg);

    void updateEndTime(@Param("instanceId") String instanceId,
                       @Param("endTime") LocalDateTime endTime);

    TaskInstance selectById(Long id);

    TaskInstance selectByInstanceId(String instanceId);

    List<TaskInstance> selectRunning();

    List<TaskInstance> selectByTaskCode(@Param("taskCode") String taskCode,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    List<TaskInstance> selectTimeout(@Param("timeoutMinutes") int timeoutMinutes);

    List<TaskInstance> selectByHostName(String hostName);

    int countByStatus(@Param("taskCode") String taskCode,
                      @Param("status") Integer status);

    int cleanHistoryData(@Param("daysBefore") int daysBefore);
}