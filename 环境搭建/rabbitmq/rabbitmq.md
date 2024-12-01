这个错误是因为 Docker 挂载卷时遇到了问题。让我们修改 Docker Compose 配置，先创建必要的文件和目录。

1. 首先创建必要的目录和文件：

```bash
# 创建rabbitmq配置目录
mkdir -p rabbitmq/config

# 创建enabled_plugins文件
echo "[rabbitmq_management,rabbitmq_delayed_message_exchange]." > rabbitmq/config/enabled_plugins

# 创建definitions.json文件
touch rabbitmq/config/definitions.json
```

2. 然后修改 docker-compose 配置：

```yaml
version: '3.8'

services:
  rabbitmq:
    image: rabbitmq:3.12-management
    container_name: rabbitmq
    environment:
      - RABBITMQ_DEFAULT_USER=admin
      - RABBITMQ_DEFAULT_PASS=123456
    ports:
      - "5672:5672"   # AMQP 端口
      - "15672:15672" # 管理界面端口
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
      - ./rabbitmq/config:/etc/rabbitmq/conf.d:ro
    networks:
      - app_network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "rabbitmqctl", "status"]
      interval: 30s
      timeout: 10s
      retries: 5

volumes:
  rabbitmq_data:
    driver: local

networks:
  app_network:
    driver: bridge

```

3. 创建 definitions.json 文件的内容：

```json
{
    "rabbit_version": "3.12.0",
    "rabbitmq_version": "3.12.0",
    "users": [
        {
            "name": "admin",
            "password_hash": "123456",
            "hashing_algorithm": "rabbit_password_hashing_sha256",
            "tags": ["administrator"]
        }
    ],
    "vhosts": [
        {
            "name": "/"
        }
    ],
    "permissions": [
        {
            "user": "admin",
            "vhost": "/",
            "configure": ".*",
            "write": ".*",
            "read": ".*"
        }
    ],
    "exchanges": [
        {
            "name": "crawler.exchange",
            "vhost": "/",
            "type": "topic",
            "durable": true,
            "auto_delete": false,
            "internal": false,
            "arguments": {}
        }
    ],
    "queues": [
        {
            "name": "crawler.task.queue",
            "vhost": "/",
            "durable": true,
            "auto_delete": false,
            "arguments": {}
        },
        {
            "name": "crawler.retry.queue",
            "vhost": "/",
            "durable": true,
            "auto_delete": false,
            "arguments": {
                "x-dead-letter-exchange": "crawler.exchange",
                "x-dead-letter-routing-key": "crawler.task",
                "x-message-ttl": 30000
            }
        }
    ],
    "bindings": [
        {
            "source": "crawler.exchange",
            "vhost": "/",
            "destination": "crawler.task.queue",
            "destination_type": "queue",
            "routing_key": "crawler.task",
            "arguments": {}
        },
        {
            "source": "crawler.exchange",
            "vhost": "/",
            "destination": "crawler.retry.queue",
            "destination_type": "queue",
            "routing_key": "crawler.retry",
            "arguments": {}
        }
    ]
}

```

现在执行以下命令启动：

```bash
# 停止并删除现有容器（如果存在）
docker compose -f docker-compose-rabbitmq.yml down

# 删除现有卷（如果需要清理）
docker volume rm rabbitmq_rabbitmq_data

# 启动新的容器
docker compose -f docker-compose-rabbitmq.yml up -d
```

这个配置的主要改进：

1. 简化了卷挂载配置
2. 使用了更安全的配置目录挂载方式
3. 添加了健康检查
4. 移除了可能导致问题的插件配置方式

启动后，你可以通过以下方式验证：

1. 检查容器状态：
```bash
docker ps
```

2. 检查容器日志：
```bash
docker logs rabbitmq
```

3. 访问管理界面：
    - URL: http://localhost:15672
    - 用户名: admin
    - 密码: 123456

如果仍然遇到问题，可以检查日志获取详细的错误信息：
```bash
docker logs rabbitmq
```