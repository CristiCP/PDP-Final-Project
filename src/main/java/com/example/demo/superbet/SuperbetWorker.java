package com.example.demo.superbet;

import com.example.demo.MatchDetail;
import com.example.demo.config.RabbitMQConfig;
import com.example.demo.config.RabbitMQWorkerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("superbet")
public class SuperbetWorker {

    @Autowired
    private SuperbetService superbetService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQWorkerConfig.SUPERBET_WORKER_QUEUE, containerFactory = "superbetListenerContainerFactory")
    public void processMatch(String eventId) {
        try {
            String workerName = Thread.currentThread().getName();
            System.out.println("Superbet Worker [" + workerName + "] is processing Match ID: " + eventId);

            String matchContent = superbetService.getMatchContent(eventId);
            MatchDetail matchDetail = superbetService.processMatch(matchContent);

            String message = objectMapper.writeValueAsString(matchDetail);
            rabbitTemplate.convertAndSend(RabbitMQConfig.SUPERBET_QUEUE, message);
        } catch (Exception e) {
            System.err.println("Error processing Superbet Match ID " + eventId + ": " + e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
