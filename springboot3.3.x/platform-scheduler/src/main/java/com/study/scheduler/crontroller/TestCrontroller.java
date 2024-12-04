package com.study.scheduler.crontroller;

import com.study.scheduler.utils.HttpClientUtil;

public class TestCrontroller {
    public static void main(String[] args) {
        try {
            String response = HttpClientUtil.doGet("https://your-nginx-server/api");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
