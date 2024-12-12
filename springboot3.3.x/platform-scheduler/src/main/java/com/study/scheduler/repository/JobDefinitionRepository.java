package com.study.scheduler.repository;

import com.study.scheduler.model.job.JobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDefinitionRepository extends JpaRepository<JobDefinition, String> {
    // 可以添加自定义查询方法
}
