import os
import threading
import time
from flask import Flask, request, jsonify
import requests
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

# 创建一个并发锁，用于测试环境
request_lock = threading.Lock()

app = Flask(__name__)

# 第三方API的配置
THIRD_PARTY_API_KEY = os.getenv("THIRD_PARTY_API_KEY")
THIRD_PARTY_API_URL = os.getenv("THIRD_PARTY_API_URL")

# OpenAI兼容的路由
@app.route("/v1/chat/completions", methods=["POST"])
def chat_completions():
    # 获取OpenAI格式的请求
    openai_request = request.json
    
    # 打印收到的OpenAI格式请求(调试用)
    print("OpenAI format request:", openai_request)
    
    # 转换为第三方API格式
    third_party_request = convert_to_third_party_format(openai_request)
    
    # 打印转换后的第三方格式请求(调试用)
    print("Third-party format request:", third_party_request)
    
    # 获取环境类型
    ENVIRONMENT = os.getenv("ENVIRONMENT", "test").lower()
    
    # 调用第三方API
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {THIRD_PARTY_API_KEY}"
    }
    
    # 根据环境处理并发
    if ENVIRONMENT == "test":
        # 测试环境使用锁防止并发
        with request_lock:
            app.logger.info("Test environment: Acquiring lock for API request")
            response = requests.post(
                THIRD_PARTY_API_URL,
                json=third_party_request,
                headers=headers
            )
            # 在测试环境中添加小延迟，确保请求完全处理
            time.sleep(0.5)
            app.logger.info("Test environment: Releasing lock")
    else:
        # 生产环境允许并发
        app.logger.info("Production environment: Allowing concurrent requests")
        response = requests.post(
            THIRD_PARTY_API_URL,
            json=third_party_request,
            headers=headers
        )
    
    # 转换为OpenAI格式的响应
    openai_response = convert_to_openai_format(response.json())
    
    # 打印转换后的OpenAI格式响应(调试用)
    print("OpenAI format response:", openai_response)
    
    return jsonify(openai_response)

def convert_to_third_party_format(openai_request):
    """
    将OpenAI格式的请求转换为第三方API格式
    
    典型的OpenAI请求格式:
    {
        "model": "gpt-3.5-turbo",
        "messages": [
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": "Hello!"}
        ],
        "temperature": 0.7,
        "max_tokens": 150
    }
    
    请根据第三方API的具体参数修改此函数
    """
    
    # 示例转换 (需要根据第三方API的实际参数进行调整)
    third_party_request = {
        "ai_model": openai_request.get("model", "default_model"),
        "conversation": [
            {
                "speaker_type": msg["role"],
                "message": msg["content"]
            }
            for msg in openai_request.get("messages", [])
        ],
        "settings": {
            "creativity": openai_request.get("temperature", 0.7),
            "max_length": openai_request.get("max_tokens", 150),
            "top_p": openai_request.get("top_p", 1.0),
            "presence_penalty": openai_request.get("presence_penalty", 0),
            "frequency_penalty": openai_request.get("frequency_penalty", 0)
        }
    }
    
    return third_party_request

def convert_to_openai_format(third_party_response):
    """
    将第三方API的响应转换为OpenAI格式
    
    典型的OpenAI响应格式:
    {
        "id": "chatcmpl-123",
        "object": "chat.completion",
        "created": 1677652288,
        "model": "gpt-3.5-turbo",
        "choices": [{
            "index": 0,
            "message": {
                "role": "assistant",
                "content": "Hello there! How can I assist you today?"
            },
            "finish_reason": "stop"
        }],
        "usage": {
            "prompt_tokens": 9,
            "completion_tokens": 12,
            "total_tokens": 21
        }
    }
    
    请根据第三方API的具体响应格式修改此函数
    """
    
    # 示例转换 (需要根据第三方API的实际响应格式进行调整)
    import time
    
    # 假设第三方响应格式如下:
    # {
    #     "response_id": "12345",
    #     "output": "Hello there! How can I assist you today?",
    #     "stop_reason": "complete",
    #     "token_metrics": {
    #         "input_count": 9,
    #         "output_count": 12
    #     }
    # }
    
    openai_response = {
        "id": third_party_response.get("response_id", f"chatcmpl-{int(time.time())}"),
        "object": "chat.completion",
        "created": int(time.time()),
        "model": "third-party-model",
        "choices": [
            {
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": third_party_response.get("output", "")
                },
                "finish_reason": map_finish_reason(third_party_response.get("stop_reason", ""))
            }
        ],
        "usage": {
            "prompt_tokens": third_party_response.get("token_metrics", {}).get("input_count", 0),
            "completion_tokens": third_party_response.get("token_metrics", {}).get("output_count", 0),
            "total_tokens": (
                third_party_response.get("token_metrics", {}).get("input_count", 0) +
                third_party_response.get("token_metrics", {}).get("output_count", 0)
            )
        }
    }
    
    return openai_response

def map_finish_reason(third_party_reason):
    """将第三方API的结束原因映射到OpenAI的格式"""
    mapping = {
        "complete": "stop",
        "length": "length",
        "content_filter": "content_filter",
        # 添加更多映射...
    }
    return mapping.get(third_party_reason, "stop")

# 其他OpenAI兼容的路由可以根据需要添加
# 例如：models列表、embeddings等

if __name__ == "__main__":
    port = int(os.getenv("PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=True)
