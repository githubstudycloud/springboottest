package com.study.collect.service;

import com.study.collect.entity.MongoTestEntity;
import com.study.collect.entity.TestEntity;
import com.study.collect.mapper.TestMySQLMapper;
import com.study.collect.repository.TestMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TestService {

    private static final String REDIS_KEY_PREFIX = "test:entity:";
    private static final String EXCHANGE_NAME = "test.exchange";
    private static final String ROUTING_KEY = "test.message";
    @Autowired
    private TestMySQLMapper mysqlMapper;
    @Autowired
    private TestMongoRepository mongoRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // MySQL测试方法
    @Transactional
    public TestEntity testMySQL(String name, String description) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

        mysqlMapper.insert(entity);
        log.info("MySQL插入数据成功: {}", entity);

        // 测试查询
        TestEntity found = mysqlMapper.findById(entity.getId());
        log.info("MySQL查询数据: {}", found);

        // 测试更新
        found.setDescription(description + " - updated");
        found.setUpdateTime(new Date());
        mysqlMapper.update(found);
        log.info("MySQL更新数据成功");

        // 测试删除
        mysqlMapper.deleteById(found.getId());
        log.info("MySQL删除数据成功");

        return found;
    }

    // Redis测试方法
    public void testRedis(String key, String value) {
        String fullKey = REDIS_KEY_PREFIX + key;

        // 测试写入
        redisTemplate.opsForValue().set(fullKey, value, 1, TimeUnit.HOURS);
        log.info("Redis写入数据成功: {} = {}", fullKey, value);

        // 测试读取
        Object stored = redisTemplate.opsForValue().get(fullKey);
        log.info("Redis读取数据: {} = {}", fullKey, stored);

        // 测试删除
        Boolean deleted = redisTemplate.delete(fullKey);
        log.info("Redis删除数据: {}", deleted);
    }

    // MongoDB测试方法
    public MongoTestEntity testMongoDB(String name, String description) {
        MongoTestEntity entity = new MongoTestEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

        // 测试插入
        MongoTestEntity saved = mongoRepository.save(entity);
        log.info("MongoDB插入数据成功: {}", saved);

        // 测试查询
        MongoTestEntity found = mongoRepository.findById(saved.getId()).orElse(null);
        log.info("MongoDB查询数据: {}", found);

        // 测试更新
        found.setDescription(description + " - updated");
        found.setUpdateTime(new Date());
        mongoRepository.save(found);
        log.info("MongoDB更新数据成功");

        // 测试删除
        mongoRepository.deleteById(found.getId());
        log.info("MongoDB删除数据成功");

        return found;
    }

    // RabbitMQ测试方法
    public void testRabbitMQ(String message) {
        // 发送消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
        log.info("RabbitMQ发送消息成功: {}", message);

        // 接收消息（这里为了测试，直接接收，实际应该使用监听器）
//        Object received = rabbitTemplate.receiveAndConvert(ROUTING_KEY);
        Object received = rabbitTemplate.receiveAndConvert("test.message");
        log.info("RabbitMQ接收消息: {}", received);
    }
}