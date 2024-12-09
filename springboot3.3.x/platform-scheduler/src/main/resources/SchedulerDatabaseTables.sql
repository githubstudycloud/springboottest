-- 任务信息表
CREATE TABLE schedule_job_info
(
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    job_name        VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group       VARCHAR(100) NOT NULL COMMENT '任务分组',
    job_class       VARCHAR(255) NOT NULL COMMENT '任务类',
    cron_expression VARCHAR(100) NOT NULL COMMENT 'cron表达式',
    parameter       TEXT COMMENT '任务参数（JSON格式）',
    description     VARCHAR(500) COMMENT '任务描述',
    concurrent      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否允许并发执行：0-否，1-是',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '任务状态：0-暂停，1-运行',
    next_fire_time  DATETIME COMMENT '下次执行时间',
    prev_fire_time  DATETIME COMMENT '上次执行时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_key (job_name, job_group),
    KEY             idx_status (status),
    KEY             idx_next_fire_time (next_fire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务信息表';

-- 任务执行日志表
CREATE TABLE schedule_job_log
(
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    job_id         BIGINT COMMENT '任务ID',
    job_name       VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group      VARCHAR(100) NOT NULL COMMENT '任务分组',
    job_class      VARCHAR(255) NOT NULL COMMENT '任务类',
    parameter      TEXT COMMENT '执行参数',
    message        VARCHAR(500) COMMENT '日志信息',
    status         TINYINT      NOT NULL COMMENT '执行状态：0-失败，1-成功',
    exception_info TEXT COMMENT '异常信息',
    start_time     DATETIME     NOT NULL COMMENT '开始时间',
    end_time       DATETIME COMMENT '结束时间',
    duration       BIGINT COMMENT '执行时长(毫秒)',
    server_ip      VARCHAR(50) COMMENT '执行服务器IP',
    PRIMARY KEY (id),
    KEY            idx_job_name (job_name),
    KEY            idx_job_group (job_group),
    KEY            idx_start_time (start_time),
    KEY            idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行日志表';