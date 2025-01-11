package com.example.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQWorkerConfig {

    public static final String SUPERBET_WORKER_QUEUE = "superbetWorkerQueue";
    public static final String UNIBET_WORKER_QUEUE = "unibetWorkerQueue";

    @Bean
    public Queue superbetWorkerQueue() {
        return new Queue(SUPERBET_WORKER_QUEUE, false);
    }

    @Bean
    public Queue unibetWorkerQueue() {
        return new Queue(UNIBET_WORKER_QUEUE, false);
    }
}
