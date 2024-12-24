-- 任务配置表
CREATE TABLE `task_config` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `task_code` varchar(50) NOT NULL COMMENT '任务编码',
                               `task_name` varchar(100) NOT NULL COMMENT '任务名称',
                               `task_handler` varchar(100) NOT NULL COMMENT '任务处理器',
                               `task_param` text COMMENT '任务参数(JSON格式)',
                               `cron_expr` varchar(50) DEFAULT NULL COMMENT 'cron表达式',
                               `shard_total` int DEFAULT '1' COMMENT '分片总数',
                               `retry_times` int DEFAULT '0' COMMENT '重试次数',
                               `retry_interval` int DEFAULT '0' COMMENT '重试间隔(秒)',
                               `timeout` int DEFAULT '0' COMMENT '超时时间(秒)',
                               `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
                               `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_task_code` (`task_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务配置表';

-- 任务实例表
CREATE TABLE `task_instance` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `instance_id` varchar(50) NOT NULL COMMENT '实例ID',
                                 `task_code` varchar(50) NOT NULL COMMENT '任务编码',
                                 `shard_index` int DEFAULT NULL COMMENT '分片索引',
                                 `shard_total` int DEFAULT NULL COMMENT '分片总数',
                                 `shard_param` text COMMENT '分片参数',
                                 `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态:0-初始,1-执行中,2-成功,3-失败',
                                 `error_msg` text COMMENT '错误信息',
                                 `host_name` varchar(100) DEFAULT NULL COMMENT '执行机器',
                                 `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                 `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_instance_id` (`instance_id`),
                                 KEY `idx_task_code` (`task_code`),
                                 KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实例表';

-- 任务日志表
CREATE TABLE `task_log` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `instance_id` varchar(50) NOT NULL COMMENT '实例ID',
                            `task_code` varchar(50) NOT NULL COMMENT '任务编码',
                            `log_type` tinyint NOT NULL COMMENT '日志类型:1-开始,2-心跳,3-进度,4-结果,5-错误',
                            `log_content` text COMMENT '日志内容',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_instance_id` (`instance_id`),
                            KEY `idx_task_code` (`task_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志表';