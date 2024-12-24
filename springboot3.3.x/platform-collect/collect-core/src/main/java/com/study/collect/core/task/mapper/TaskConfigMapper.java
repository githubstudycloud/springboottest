package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskConfigMapper {

    void insert(TaskConfig config);

    void update(TaskConfig config);

    TaskConfig selectById(@Param("id") Long id);

    TaskConfig selectByCode(@Param("taskCode") String taskCode);

    List<TaskConfig> selectEnabled();

    void updateStatus(@Param("taskCode") String taskCode, @Param("status") Integer status);
}