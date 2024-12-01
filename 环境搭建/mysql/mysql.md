我来帮你构建MySql5.7的Docker容器。首先创建Docker配置和启动脚本。



```bash
#!/bin/bash

# 创建必要的目录结构
mkdir -p mysql/conf mysql/data mysql/init

# 创建MySQL配置文件
cat > mysql/conf/my.cnf << EOF
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4'
skip-character-set-client-handshake=true
max_allowed_packet=64M
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

# 基础配置
port=3306
user=mysql
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
pid-file=/var/run/mysqld/mysqld.pid

# 连接配置
max_connections=1000
max_connect_errors=2000
wait_timeout=600
interactive_timeout=600

# InnoDB配置
innodb_buffer_pool_size=1G
innodb_log_file_size=256M
innodb_log_buffer_size=64M
innodb_flush_log_at_trx_commit=2
innodb_flush_method=O_DIRECT

# 慢查询配置
slow_query_log=1
slow_query_log_file=/var/lib/mysql/slow.log
long_query_time=2

[client]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4
EOF

# 创建docker-compose.yml文件
cat > docker-compose.yml << EOF
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
-- 创建测试用户
CREATE USER 'test'@'%' IDENTIFIED BY '123456';
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

# 停止并删除已存在的容器
docker-compose down

# 启动新容器
docker-compose up -d

# 等待MySQL启动
echo "Waiting for MySQL to start..."
sleep 10

# 检查MySQL是否正常运行
docker exec mysql5.7 mysqladmin -uroot -p123456 ping

if [ $? -eq 0 ]; then
    echo "MySQL is running successfully!"
    echo "You can connect to MySQL using:"
    echo "Host: localhost"
    echo "Port: 3306"
    echo "Username: root"
    echo "Password: 123456"
    echo "Database: test"
else
    echo "MySQL failed to start properly!"
fi
EOF

# 创建停止脚本
cat > stop.sh << EOF
#!/bin/bash
docker-compose down
EOF

# 设置脚本执行权限
chmod +x start.sh stop.sh

# 提供使用说明
echo "MySQL Docker环境已配置完成！"
echo "目录结构："
echo "  ├── docker-compose.yml    # Docker编排文件"
echo "  ├── start.sh             # 启动脚本"
echo "  ├── stop.sh              # 停止脚本"
echo "  └── mysql                # MySQL相关文件"
echo "      ├── conf             # 配置文件"
echo "      ├── data             # 数据文件"
echo "      └── init             # 初始化脚本"
echo ""
echo "使用方法："
echo "1. 启动MySQL: ./start.sh"
echo "2. 停止MySQL: ./stop.sh"
echo ""
echo "默认配置："
echo "- 端口: 3306"
echo "- 用户名: root"
echo "- 密码: 123456"
echo "- 数据库: test"

```

使用方法：

1. 创建一个新目录用于MySQL Docker环境：
```bash
mkdir mysql-docker
cd mysql-docker
```

2. 将上面的脚本内容保存为setup.sh并执行：
```bash
chmod +x setup.sh
./setup.sh
```

3. 启动MySQL：
```bash
./start.sh
```

4. 验证连接：
```bash
mysql -h127.0.0.1 -uroot -p123456
```

主要特点：

1. 使用MySQL 5.7官方镜像
2. 配置了合适的字符集(utf8mb4)
3. 配置了常用的性能参数
4. 持久化数据到本地目录
5. 包含了初始化数据库和用户的脚本
6. 提供了方便的启动和停止脚本
7. 采用docker-compose进行容器编排
8. 配置了桥接网络

注意事项：

1. 确保Docker和docker-compose已安装
2. 默认密码是123456，生产环境请修改
3. 数据会持久化到mysql/data目录
4. 可以通过修改my.cnf调整配置
5. 可以在init.sql中添加更多初始化脚本
6. 如果3306端口被占用，可以修改docker-compose.yml中的端口映射

要停止服务：
```bash
./stop.sh
```

要清理数据（慎用）：
```bash
rm -rf mysql/data/*
```

这个配置适合开发和测试环境使用，如果用于生产环境，建议：
1. 修改默认密码
2. 根据服务器配置调整性能参数
3. 配置备份策略
4. 配置更严格的安全策略