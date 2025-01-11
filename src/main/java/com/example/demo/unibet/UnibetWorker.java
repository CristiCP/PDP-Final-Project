package com.example.demo.unibet;

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
@Profile("unibet")
public class UnibetWorker {

    @Autowired
    private UnibetService unibetService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitMQWorkerConfig.UNIBET_WORKER_QUEUE, containerFactory = "unibetListenerContainerFactory")
    public void processTask(String message) {
        try {
            UnibetWorkerTask task = objectMapper.readValue(message, UnibetWorkerTask.class);
            String workerName = Thread.currentThread().getName();
            System.out.println("Unibet Worker [" + workerName + "] is processing Task: " + message);

            String matchContent = unibetService.getLiveMatchContent(Integer.parseInt(task.getEventId()));
            MatchDetail matchDetail = unibetService.processMatch(task.getEventId(),task.getName(), matchContent);

            if (matchDetail != null) {
                String processedMessage = objectMapper.writeValueAsString(matchDetail);
                rabbitTemplate.convertAndSend(RabbitMQConfig.UNIBET_RESPONSE_QUEUE, processedMessage);
            }
        } catch (Exception e) {
            System.err.println("Error processing Unibet task: " + e.getMessage());
        }
    }
}
