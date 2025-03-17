# AI接口适配器服务部署指南

本文档提供了如何停止、启动和部署AI接口适配器服务的详细说明，包括使用Docker和非Docker方式。

## 非Docker部署

### 安装依赖

```bash
pip install -r requirements.txt
```

### 配置环境变量

1. 复制示例环境文件:
```bash
cp .env.example .env
```

2. 编辑.env文件，填入以下配置:
   - `THIRD_PARTY_API_KEY`: 你的第三方API密钥
   - `THIRD_PARTY_API_URL`: 第三方API的URL
   - `ENVIRONMENT`: 设置为`test`或`production`
   - `PORT`: 服务端口号(默认为5000)

### 启动服务

#### 开发环境

```bash
python app.py
```

#### 生产环境

使用Gunicorn启动(建议用于生产环境):

```bash
gunicorn --bind 0.0.0.0:5000 app:app
```

可以添加更多Gunicorn参数优化性能:

```bash
gunicorn --bind 0.0.0.0:5000 --workers 4 --threads 2 app:app
```

### 停止服务

- 如果是直接运行Python脚本，按`Ctrl+C`停止
- 如果使用Gunicorn作为后台服务，可以使用:

```bash
# 查找进程ID
ps aux | grep gunicorn

# 停止进程
kill <进程ID>

# 或强制停止
kill -9 <进程ID>
```

### 设置为系统服务(使用systemd)

1. 创建服务文件:

```bash
sudo nano /etc/systemd/system/ai-adapter.service
```

2. 添加以下内容:

```
[Unit]
Description=AI接口适配器服务
After=network.target

[Service]
User=<你的用户名>
WorkingDirectory=/path/to/app
Environment="PATH=/path/to/venv/bin"
EnvironmentFile=/path/to/app/.env
ExecStart=/path/to/venv/bin/gunicorn --workers 4 --bind 0.0.0.0:5000 app:app
Restart=always

[Install]
WantedBy=multi-user.target
```

3. 启用并启动服务:

```bash
sudo systemctl enable ai-adapter
sudo systemctl start ai-adapter
```

4. 管理服务:

```bash
# 查看状态
sudo systemctl status ai-adapter

# 停止服务
sudo systemctl stop ai-adapter

# 重启服务
sudo systemctl restart ai-adapter
```

## Docker部署

### 构建Docker镜像

```bash
docker build -t ai-adapter:latest .
```

### 运行Docker容器(测试环境)

```bash
docker run -d \
  --name ai-adapter \
  -p 5000:5000 \
  -e THIRD_PARTY_API_KEY=your_api_key \
  -e THIRD_PARTY_API_URL=https://api.thirdparty.ai/generate \
  -e ENVIRONMENT=test \
  ai-adapter:latest
```

### 运行Docker容器(生产环境)

```bash
docker run -d \
  --name ai-adapter \
  -p 5000:5000 \
  -e THIRD_PARTY_API_KEY=your_api_key \
  -e THIRD_PARTY_API_URL=https://api.thirdparty.ai/generate \
  -e ENVIRONMENT=production \
  ai-adapter:latest
```

### 停止和管理Docker容器

```bash
# 查看运行中的容器
docker ps

# 停止容器
docker stop ai-adapter

# 启动已存在的容器
docker start ai-adapter

# 删除容器
docker rm ai-adapter

# 查看容器日志
docker logs ai-adapter

# 实时查看日志
docker logs -f ai-adapter
```

### 使用Docker Compose(推荐生产环境)

1. 创建`docker-compose.yml`文件:

```yaml
version: '3'

services:
  ai-adapter:
    build: .
    ports:
      - "5000:5000"
    environment:
      - THIRD_PARTY_API_KEY=your_api_key
      - THIRD_PARTY_API_URL=https://api.thirdparty.ai/generate
      - ENVIRONMENT=production
    restart: always
```

2. 启动服务:

```bash
docker-compose up -d
```

3. 停止服务:

```bash
docker-compose down
```

## 使用NGINX作为反向代理

为生产环境添加NGINX作为反向代理是推荐的做法，可以提供SSL终止、负载均衡等功能。

1. 安装NGINX:

```bash
sudo apt update
sudo apt install nginx
```

2. 创建NGINX配置:

```bash
sudo nano /etc/nginx/sites-available/ai-adapter
```

3. 添加以下内容:

```
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:5000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

4. 启用站点配置:

```bash
sudo ln -s /etc/nginx/sites-available/ai-adapter /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## 监控和日志

- 使用Docker: `docker logs -f ai-adapter`
- 使用systemd: `sudo journalctl -u ai-adapter -f`
- 应用日志将输出到标准输出和标准错误

## 健康检查和监控

考虑添加一个健康检查端点并设置监控:

```python
@app.route("/health", methods=["GET"])
def health_check():
    return jsonify({"status": "ok", "environment": os.getenv("ENVIRONMENT", "unknown")})
```

## 更新和维护

### 非Docker更新

1. 拉取最新代码
2. 安装新的依赖(如有)
3. 重启服务

### Docker更新

1. 拉取最新代码
2. 重新构建镜像
3. 停止并删除旧容器
4. 启动新容器

```bash
git pull
docker build -t ai-adapter:latest .
docker stop ai-adapter
docker rm ai-adapter
# 使用上述命令启动新容器
```

或使用Docker Compose:

```bash
git pull
docker-compose down
docker-compose build
docker-compose up -d
```
