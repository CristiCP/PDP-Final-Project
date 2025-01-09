package com.example.demo.unibet;

import com.example.demo.MatchDetail;
import com.example.demo.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("unibet")
@Component
public class UnibetConsumer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UnibetConsumer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.UNIBET_QUEUE)
    public void processUnibetData(String message) {
        try {
            MatchDetail matchDetails = objectMapper.readValue(message, MatchDetail.class);
            String response = objectMapper.writeValueAsString(matchDetails);
            System.out.println(matchDetails.getMatchName());
            rabbitTemplate.convertAndSend(RabbitMQConfig.UNIBET_RESPONSE_QUEUE, response);
        } catch (Exception e) {
            System.err.println("Error processing Unibet data: " + e.getMessage());
        }
    }
}

