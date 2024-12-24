-- 任务配置表
CREATE TABLE task_config (
id bigint NOT NULL AUTO_INCREMENT,
task_id varchar(64) NOT NULL COMMENT '任务ID',
task_name varchar(64) NOT NULL COMMENT '任务名称',
task_handler varchar(64) NOT NULL COMMENT '任务处理器',
cron_expression varchar(64) COMMENT 'cron表达式',
props json COMMENT '任务属性',
status tinyint NOT NULL COMMENT '状态:0-禁用,1-启用',
create_time datetime NOT NULL,
update_time datetime NOT NULL,
PRIMARY KEY (id),
UNIQUE KEY uk_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务配置表';