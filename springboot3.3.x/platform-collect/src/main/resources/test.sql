CREATE TABLE test_table
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    create_time DATETIME     NOT NULL,
    update_time DATETIME     NOT NULL,
    INDEX       idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';