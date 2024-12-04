CREATE TABLE collect_data
(
    id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    device_id    VARCHAR(50) NOT NULL COMMENT '设备ID',
    device_name  VARCHAR(100) COMMENT '设备名称',
    temperature DOUBLE COMMENT '温度',
    humidity DOUBLE COMMENT '湿度',
    location     VARCHAR(200) COMMENT '位置信息',
    collect_time DATETIME    NOT NULL COMMENT '采集时间',
    create_time  DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY          idx_device_time (device_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集表';