package com.study.collect.mapper;

import com.study.collect.entity.TaskLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface TaskLogMapper {
    @Insert("""
                INSERT INTO task_logs (
                    task_id, status, message, response_code,
                    execution_time, create_time
                ) VALUES (
                    #{taskId}, #{status}, #{message}, #{responseCode},
                    #{executionTime}, #{createTime}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskLog log);

    @Select("SELECT * FROM task_logs WHERE task_id = #{taskId} ORDER BY create_time DESC")
    List<TaskLog> findByTaskId(String taskId);

    @Select("""
                SELECT * FROM task_logs 
                WHERE create_time BETWEEN #{startTime} AND #{endTime}
                ORDER BY create_time DESC
            """)
    List<TaskLog> findByTimeRange(Date startTime, Date endTime);
}