-- 初始化测试数据库和表

-- 创建公共库
CREATE DATABASE IF NOT EXISTS public_db;
USE public_db;

-- 系统配置表
CREATE TABLE IF NOT EXISTS tb_system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(1000) NOT NULL,
    description VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1:有效,0:无效'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入测试数据
INSERT INTO tb_system_config(config_key, config_value, description) VALUES
('fluxcore.version', '1.0.0', 'FluxCore版本号'),
('fluxcore.supported.formats', 'json,xml,csv', '支持的数据格式'),
('fluxcore.default.charset', 'UTF-8', '默认字符集');

-- 创建采集库
CREATE DATABASE IF NOT EXISTS collection_db;
USE collection_db;

-- 数据采集表
CREATE TABLE IF NOT EXISTS tb_source_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_type VARCHAR(50) NOT NULL COMMENT '数据源类型',
    source_content TEXT NOT NULL COMMENT '数据内容',
    content_format VARCHAR(20) NOT NULL COMMENT '内容格式(json/xml/csv等)',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    source_location VARCHAR(255) COMMENT '数据来源位置',
    status VARCHAR(20) NOT NULL COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集表';

-- 插入测试数据
INSERT INTO tb_source_data(source_type, source_content, content_format, collect_time, source_location, status) VALUES
('API', '{"id":1,"name":"Test Data","value":100}', 'json', NOW(), 'api.example.com', 'COLLECTED'),
('FILE', '<data><id>2</id><name>XML Data</name><value>200</value></data>', 'xml', NOW(), '/data/files/sample.xml', 'COLLECTED');

-- 创建业务库1
CREATE DATABASE IF NOT EXISTS biz_db1;
USE biz_db1;

-- 业务数据表
CREATE TABLE IF NOT EXISTS tb_data_entity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_code VARCHAR(50) NOT NULL COMMENT '数据编码',
    data_name VARCHAR(100) NOT NULL COMMENT '数据名称',
    data_content TEXT NOT NULL COMMENT '数据内容',
    data_format VARCHAR(20) NOT NULL COMMENT '数据格式',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    source_db VARCHAR(50) COMMENT '数据源库'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务数据表';

-- 插入测试数据
INSERT INTO tb_data_entity(data_code, data_name, data_content, data_format, create_time, update_time, source_db) VALUES
('SD1', 'API_1', '{"id":1,"name":"Test Data","value":100}', 'JSON', NOW(), NOW(), 'db1'),
('SD2', 'FILE_2', '{"data":{"id":2,"name":"XML Data","value":200}}', 'JSON', NOW(), NOW(), 'db1');

-- 创建业务库2
CREATE DATABASE IF NOT EXISTS biz_db2;
USE biz_db2;

-- 业务数据表
CREATE TABLE IF NOT EXISTS tb_data_entity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_code VARCHAR(50) NOT NULL COMMENT '数据编码',
    data_name VARCHAR(100) NOT NULL COMMENT '数据名称',
    data_content TEXT NOT NULL COMMENT '数据内容',
    data_format VARCHAR(20) NOT NULL COMMENT '数据格式',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    source_db VARCHAR(50) COMMENT '数据源库'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务数据表';

-- 插入测试数据
INSERT INTO tb_data_entity(data_code, data_name, data_content, data_format, create_time, update_time, source_db) VALUES
('SD3', 'TEST_3', '{"items":[{"name":"Item 1","price":10.5},{"name":"Item 2","price":20.75}]}', 'JSON', NOW(), NOW(), 'db2');

-- 创建业务库3
CREATE DATABASE IF NOT EXISTS biz_db3;
USE biz_db3;

-- 业务数据表
CREATE TABLE IF NOT EXISTS tb_data_entity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_code VARCHAR(50) NOT NULL COMMENT '数据编码',
    data_name VARCHAR(100) NOT NULL COMMENT '数据名称',
    data_content TEXT NOT NULL COMMENT '数据内容',
    data_format VARCHAR(20) NOT NULL COMMENT '数据格式',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    source_db VARCHAR(50) COMMENT '数据源库'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务数据表';
