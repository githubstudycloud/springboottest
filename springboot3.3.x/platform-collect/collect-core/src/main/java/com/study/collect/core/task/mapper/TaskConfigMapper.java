package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface TaskConfigMapper {
    void insert(TaskConfig config);
    void update(TaskConfig config);
    TaskConfig selectById(Long id);
    TaskConfig selectByCode(String taskCode);
    List<TaskConfig> selectEnabled();
    void updateStatus(@Param("taskCode") String taskCode, @Param("status") Integer status);
    void deleteByCode(String taskCode);
    List<TaskConfig> selectPage(@Param("taskName") String taskName,
                                @Param("status") Integer status,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
    long countTotal(@Param("taskName") String taskName,
                    @Param("status") Integer status);
}