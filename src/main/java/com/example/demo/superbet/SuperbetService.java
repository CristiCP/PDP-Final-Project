package com.example.demo.superbet;

import com.example.demo.MatchDetail;
import com.example.demo.MatchProcessor;
import com.example.demo.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Profile("superbet")
@Service
public class SuperbetService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "https://production-superbet-offer-ro.freetls.fastly.net/";
    private final OkHttpClient client;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final MatchProcessor matchProcessor;

    public SuperbetService(MatchProcessor matchProcessor) {
        this.matchProcessor = matchProcessor;
        this.client = new OkHttpClient();
    }

    private String executeGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "insomnia/10.3.0")
                .build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response.code());
        }
        return response.body().string();
    }

    public void fetchAndSendLiveMatches() throws Exception {
        String url = BASE_URL + "v2/ro-RO/events/by-date?currentStatus=active&offerState=live&startDate=2024-12-22+00:00:00";
        String content = executeGetRequest(url);
        matchProcessor.processSuperbetMatches(content);
    }

    public void fetchAndProcessMatchDetails() {
        if (matchProcessor.getSuperbetEventIds().isEmpty()) {
            return;
        }
        fetchAndProcessMatchDetails(matchProcessor.getSuperbetEventIds());
    }

    public void fetchAndProcessMatchDetails(List<String> matchIds) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (String matchId : matchIds) {
            executor.submit(() -> {
                try {
                    String content = getMatchContent(matchId);
                    processMatch(content);
                } catch (Throwable e) {
                    System.err.println("Error processing match ID " + matchId + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error waiting for tasks to complete: " + e.getMessage());
        }
    }

    private String getMatchContent(String matchId) throws Throwable {
        String url = BASE_URL + "v2/ro-RO/events/" + matchId;
        return executeGetRequest(url);
    }

    private void processMatch(String jsonResponse) throws IOException {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode matchData = rootNode.get("data").get(0);

        int eventId = matchData.get("eventId").asInt();
        String matchName = matchData.get("matchName").asText();

        List<String> bets = new ArrayList<>();
        for (JsonNode bet : matchData.get("odds")) {
            String betDetails = bet.get("marketName").asText() + ": " + bet.get("info").asText() + " @ " + bet.get("price").asDouble();
            bets.add(betDetails);
        }

        MatchDetail matchDetail = new MatchDetail(eventId, matchName, bets);
        String message = objectMapper.writeValueAsString(matchDetail);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SUPERBET_QUEUE, message);
    }
}


