#!/bin/bash

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then 
    echo "Please run as root"
    exit 1
fi

# 检查docker compose命令
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo "Error: Neither docker-compose nor docker compose is available"
    exit 1
fi

echo "Using command: $DOCKER_COMPOSE"

# 创建必要的目录结构
mkdir -p mysql/conf mysql/data mysql/init

# 创建MySQL配置文件
cat > mysql/conf/my.cnf << EOF
[mysqld]
# 基本配置
user=mysql
datadir=/var/lib/mysql
port=3306
bind-address=0.0.0.0

# 字符集配置
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4'

# 连接配置
max_connections=1000
max_connect_errors=2000

# InnoDB配置
innodb_buffer_pool_size=1G
innodb_log_file_size=256M
innodb_log_buffer_size=64M
innodb_flush_log_at_trx_commit=2

# 日志配置
log-error=/var/lib/mysql/error.log
slow_query_log=1
slow_query_log_file=/var/lib/mysql/slow.log
long_query_time=2

[client]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4
EOF

# 创建docker-compose.yaml文件
cat > docker-compose.yaml << EOF
version: '3'
services:
  mysql:
    image: mysql:5.7
    container_name: mysql5.7
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=123456
      - MYSQL_DATABASE=test
      - TZ=Asia/Shanghai
      - MYSQL_ROOT_HOST=%
    volumes:
      - ./mysql/data:/var/lib/mysql
      - ./mysql/conf/my.cnf:/etc/mysql/my.cnf
      - ./mysql/init:/docker-entrypoint-initdb.d
    command: 
      --default-authentication-plugin=mysql_native_password
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_unicode_ci
    restart: always
    networks:
      - mysql-network

networks:
  mysql-network:
    driver: bridge
EOF

# 创建初始化SQL脚本
cat > mysql/init/init.sql << EOF
-- 确保root用户可以从任何主机连接
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

-- 创建测试用户
CREATE USER IF NOT EXISTS 'test'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON test.* TO 'test'@'%';
FLUSH PRIVILEGES;

-- 创建测试数据库和表
USE test;

-- 测试表
CREATE TABLE IF NOT EXISTS test_table (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';

-- 插入测试数据
INSERT INTO test_table (name) VALUES ('test1'), ('test2');
EOF

# 创建启动脚本
cat > start.sh << EOF
#!/bin/bash

# 检查docker compose命令
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo "Error: Neither docker-compose nor docker compose is available"
    exit 1
fi

# 停止并删除已存在的容器
\$DOCKER_COMPOSE down

# 启动新容器
\$DOCKER_COMPOSE up -d

# 等待MySQL启动
echo "Waiting for MySQL to start..."
for i in {1..30}; do
    if docker exec mysql5.7 mysql -uroot -p123456 -e "SELECT 1;" &>/dev/null; then
        echo "MySQL is running successfully!"
        echo "You can connect to MySQL using:"
        echo "Host: localhost"
        echo "Port: 3306"
        echo "Username: root"
        echo "Password: 123456"
        echo "Database: test"
        exit 0
    fi
    echo "Waiting... \$i/30"
    sleep 2
done

echo "MySQL failed to start properly!"
docker logs mysql5.7
exit 1
EOF

# 创建停止脚本
cat > stop.sh << EOF
#!/bin/bash

# 检查docker compose命令
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo "Error: Neither docker-compose nor docker compose is available"
    exit 1
fi

\$DOCKER_COMPOSE down
EOF

# 创建清理脚本
cat > cleanup.sh << EOF
#!/bin/bash

# 停止并删除容器
./stop.sh

# 删除所有数据
rm -rf mysql/data/*

echo "All MySQL data has been cleaned up"
EOF

# 设置脚本执行权限
chmod +x start.sh stop.sh cleanup.sh

# 提供使用说明
echo "MySQL Docker环境配置完成！"
echo "目录结构："
echo "  ├── docker-compose.yaml   # Docker编排文件"
echo "  ├── start.sh             # 启动脚本"
echo "  ├── stop.sh              # 停止脚本"
echo "  ├── cleanup.sh           # 清理脚本"
echo "  └── mysql                # MySQL相关文件"
echo "      ├── conf             # 配置文件"
echo "      ├── data             # 数据文件"
echo "      └── init             # 初始化脚本"
echo ""
echo "使用方法："
echo "1. 启动MySQL: ./start.sh"
echo "2. 停止MySQL: ./stop.sh"
echo "3. 清理数据: ./cleanup.sh"
echo ""
echo "默认配置："
echo "- 端口: 3306"
echo "- 用户名: root"
echo "- 密码: 123456"
echo "- 数据库: test"

# 如果是第一次运行，直接启动MySQL
if [ ! -d "mysql/data/mysql" ]; then
    echo "First time setup, starting MySQL..."
    chmod +x start.sh
    ./start.sh
fi
