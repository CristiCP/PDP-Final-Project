package com.example.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SUPERBET_QUEUE = "superbetQueue";
    public static final String UNIBET_QUEUE = "unibetQueue";
    public static final String UNIBET_RESPONSE_QUEUE = "unibetResponseQueue";

    @Bean
    public Queue superbetQueue() {
        return new Queue(SUPERBET_QUEUE, false);
    }

    @Bean
    public Queue unibetQueue() {
        return new Queue(UNIBET_QUEUE, false);
    }

    @Bean
    public Queue unibetResponseQueue() {
        return new Queue(UNIBET_RESPONSE_QUEUE, false);
    }
}

