package com.study.collect.business.testcase.model.response.parse;

import java.io.IOException;

/**
 * HTTP响应解析器接口
 */
public interface HttpResponseParser<T> {

    /**
     * 解析HTTP响应
     * @param response 响应字符串
     * @return 解析后的结果
     * @throws IOException 解析异常
     */
    T parse(String response) throws IOException;

    /**
     * 从错误响应中提取错误信息
     * @param errorResponse 错误响应
     * @return 错误信息
     */
    default String parseError(String errorResponse) {
        try {
            return errorResponse;
        } catch (Exception e) {
            return "Failed to parse error response";
        }
    }
}