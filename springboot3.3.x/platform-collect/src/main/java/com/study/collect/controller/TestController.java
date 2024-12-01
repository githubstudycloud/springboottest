package com.study.collect.controller;

import com.study.collect.entity.MongoTestEntity;
import com.study.collect.entity.TestEntity;
import com.study.collect.service.TestService;
import com.study.common.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/mysql")
    public Result<TestEntity> testMySQL(@RequestParam String name, @RequestParam String description) {
        try {
            TestEntity result = testService.testMySQL(name, description);
            return Result.success(result);
        } catch (Exception e) {
            log.error("MySQL测试失败", e);
            return Result.error("MySQL测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/redis")
    public Result<Void> testRedis(@RequestParam String key, @RequestParam String value) {
        try {
            testService.testRedis(key, value);
            return Result.success();
        } catch (Exception e) {
            log.error("Redis测试失败", e);
            return Result.error("Redis测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/mongodb")
    public Result<MongoTestEntity> testMongoDB(@RequestParam String name, @RequestParam String description) {
        try {
            MongoTestEntity result = testService.testMongoDB(name, description);
            return Result.success(result);
        } catch (Exception e) {
            log.error("MongoDB测试失败", e);
            return Result.error("MongoDB测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/rabbitmq")
    public Result<Void> testRabbitMQ(@RequestParam String message) {
        try {
            testService.testRabbitMQ(message);
            return Result.success();
        } catch (Exception e) {
            log.error("RabbitMQ测试失败", e);
            return Result.error("RabbitMQ测试失败: " + e.getMessage());
        }
    }
}