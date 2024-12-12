-- 任务定义表
CREATE TABLE job_definition (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                                name VARCHAR(255) NOT NULL COMMENT '任务名称',
                                description TEXT COMMENT '任务描述',
                                type VARCHAR(50) NOT NULL COMMENT '任务类型（SCHEDULED/HTTP/CUSTOM）',
                                cron_expression VARCHAR(255) COMMENT 'Cron表达式',
                                retry_policy JSON COMMENT '重试策略，存储为JSON',
                                status VARCHAR(20) NOT NULL COMMENT '任务状态',
                                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '任务定义表';

-- 任务执行记录表
CREATE TABLE job_execution (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                               job_id BIGINT NOT NULL COMMENT '关联任务定义表',
                               trace_id VARCHAR(255) NOT NULL COMMENT '追踪ID',
                               status VARCHAR(20) NOT NULL COMMENT '执行状态',
                               start_time TIMESTAMP COMMENT '开始时间',
                               end_time TIMESTAMP COMMENT '结束时间',
                               duration BIGINT COMMENT '执行时长(毫秒)',
                               result TEXT COMMENT '执行结果',
                               error TEXT COMMENT '错误信息',
                               node_info VARCHAR(255) COMMENT '执行节点信息',
                               retry_count INT DEFAULT 0 COMMENT '重试次数',
                               create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                               update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
                               FOREIGN KEY (job_id) REFERENCES job_definition(id)
) COMMENT '任务执行记录表';

-- 指标记录表
CREATE TABLE metrics_record (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                                metric_name VARCHAR(255) NOT NULL COMMENT '指标名称',
                                value DOUBLE NOT NULL COMMENT '指标值',
                                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
                                job_id BIGINT COMMENT '关联任务（可选）',
                                FOREIGN KEY (job_id) REFERENCES job_definition(id)
) COMMENT '指标记录表';

-- 告警记录表
CREATE TABLE alert_record (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                              alert_level VARCHAR(20) NOT NULL COMMENT '告警级别',
                              message TEXT NOT NULL COMMENT '告警信息',
                              job_id BIGINT COMMENT '关联任务（可选）',
                              create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '告警生成时间',
                              FOREIGN KEY (job_id) REFERENCES job_definition(id)
) COMMENT '告警记录表';
