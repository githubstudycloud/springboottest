//package com.study.collect.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfig {
//
//    private static final String EXCHANGE_NAME = "test.exchange";
//    private static final String QUEUE_NAME = "test.queue";
//    private static final String ROUTING_KEY = "test.message";
//
//    @Bean
//    public DirectExchange testExchange() {
//        return new DirectExchange(EXCHANGE_NAME);
//    }
//
//    @Bean
//    public Queue testQueue() {
//        return new Queue(QUEUE_NAME);
//    }
//
//    @Bean
//    public Binding testBinding(Queue testQueue, DirectExchange testExchange) {
//        return BindingBuilder.bind(testQueue)
//                .to(testExchange)
//                .with(ROUTING_KEY);
//    }
//}