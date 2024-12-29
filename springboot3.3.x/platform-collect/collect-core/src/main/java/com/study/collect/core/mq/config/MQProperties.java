package com.study.collect.core.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列配置属性类
 * 对应配置文件中 collect.mq 前缀的配置项
 */
@Data
@ConfigurationProperties(prefix = "collect.mq")
public class MQProperties {

    /**
     * RabbitMQ相关配置
     */
    private RabbitMQ rabbit = new RabbitMQ();

    @Data
    public static class RabbitMQ {
        /**
         * 服务器地址
         */
        private String host;

        /**
         * 服务器端口
         */
        private Integer port;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 虚拟主机
         */
        private String virtualHost = "/";

        /**
         * 任务队列配置
         */
        private Queue task = new Queue();

        /**
         * 结果队列配置
         */
        private Queue result = new Queue();

        /**
         * 队列配置类
         */
        @Data
        public static class Queue {
            /**
             * 交换机名称
             */
            private String exchange;

            /**
             * 队列名称
             */
            private String queue;

            /**
             * 路由键
             */
            private String routingKey;

            /**
             * 是否持久化
             */
            private boolean durable = true;

            /**
             * 是否自动删除
             */
            private boolean autoDelete = false;
        }
    }
}