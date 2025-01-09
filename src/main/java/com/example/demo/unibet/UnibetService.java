package com.example.demo.unibet;

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

@Profile("unibet")
@Service
public class UnibetService {

    private static final String BASE_URL = "https://eu1.offering-api.kambicdn.com/offering/v2018/ubro/";
    private final OkHttpClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final MatchProcessor matchProcessor;

    public UnibetService(MatchProcessor matchProcessor) {
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
        String url = BASE_URL + "listView/all/all/all/all/in-play.json?lang=ro_RO&market=RO&client_id=2&channel_id=1&ncid=1734916013028&useCombined=true";
        String content = executeGetRequest(url);

        matchProcessor.processUnibetMatches(content);
    }

    public void fetchAndProcessMatchDetails() {
        if (matchProcessor.getUnibetEvents().isEmpty()) {
            return;
        }
        fetchAndProcessMatchDetails(matchProcessor.getUnibetEvents());
    }

    public String getLiveMatchContent(int matchId) throws IOException {
        String url = BASE_URL + "betoffer/event/" + matchId + ".json?lang=ro_RO&market=RO&ncid=1734917551";
        return executeGetRequest(url);
    }

    public void fetchAndProcessMatchDetails(List<MatchProcessor.UnibetEvent> events) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (MatchProcessor.UnibetEvent event : events) {
            executor.submit(() -> {
                try {
                    String matchContent = getLiveMatchContent(Integer.parseInt(event.getId()));
                    MatchDetail matchDetail = processMatch(event.getId(), event.getName(), matchContent);
                    if (matchDetail != null) {
                        rabbitTemplate.convertAndSend(RabbitMQConfig.UNIBET_RESPONSE_QUEUE, objectMapper.writeValueAsString(matchDetail));
                    }
                } catch (Exception e) {
                    System.err.println("Error processing match ID " + event.getId() + ": " + e.getMessage());
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

    private MatchDetail processMatch(String id, String name, String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode betOffers = rootNode.get("betOffers");

            if (betOffers == null || !betOffers.isArray()) {
                System.out.println("No bet offers found for event ID " + id);
                return null;
            }

            List<String> bets = new ArrayList<>();
            for (JsonNode betOffer : betOffers) {
                JsonNode criterion = betOffer.get("criterion");
                JsonNode outcomes = betOffer.get("outcomes");

                if (criterion == null || outcomes == null || !outcomes.isArray()) {
                    continue;
                }

                String criterionLabel = criterion.get("label").asText();
                for (JsonNode outcome : outcomes) {
                    String outcomeLabel = outcome.get("label").asText();
                    double odds = outcome.get("odds").asDouble() / 1000.0;
                    bets.add(criterionLabel + ": " + outcomeLabel + " @ " + odds);
                }
            }
            MatchDetail matchDetail = new MatchDetail(Integer.parseInt(id), name, bets);
            return matchDetail;
        } catch (Exception e) {
            System.err.println("Error parsing live match content for event ID " + id + ": " + e.getMessage());
            return null;
        }
    }
}


