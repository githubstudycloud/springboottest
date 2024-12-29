package com.study.collect.common.model;

import lombok.Data;

import java.util.List;

@Data
public class Response<T> {
    private String code;
    private String message;
    private T data;

    private List<String> errors;  // 添加错误详情字段

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode("200");
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(String code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> error(String code, String message, List<String> errors) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setErrors(errors);
        return response;
    }
}