package com.example.demo.superbet;

import com.example.demo.MatchDetail;
import com.example.demo.config.RabbitMQConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("superbet")
@Component
public class SuperbetConsumer {

    private final ObjectMapper objectMapper;

    public SuperbetConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.SUPERBET_QUEUE)
    public void processSuperbetData(String message) {
        try {
            MatchDetail matchDetails = objectMapper.readValue(message, new TypeReference<>() {
            });
            System.out.println(matchDetails.getMatchName());
        } catch (Exception e) {
            System.err.println("Error processing Superbet data: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.UNIBET_RESPONSE_QUEUE)
    public void processUnibetResponse(String message) {
        try {
            MatchDetail matchDetails = objectMapper.readValue(message, new TypeReference<>() {
            });
            System.out.println("Unibet: " + matchDetails.getMatchName());
        } catch (Exception e) {
            System.err.println("Error processing Unibet response: " + e.getMessage());
        }
    }
}

