package com.example.demo.unibet;

import com.example.demo.MatchDetail;
import com.example.demo.MatchProcessor;
import com.example.demo.config.RabbitMQWorkerConfig;
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

        for (MatchProcessor.UnibetEvent event : matchProcessor.getUnibetEvents()) {
            UnibetWorkerTask task = new UnibetWorkerTask(event.getId(), event.getName());
            try {
                String message = objectMapper.writeValueAsString(task);
                rabbitTemplate.convertAndSend(RabbitMQWorkerConfig.UNIBET_WORKER_QUEUE, message);
                System.out.println("Published Unibet Event to Worker Queue: " + task);
            } catch (Exception e) {
                System.err.println("Error sending Unibet task to worker queue: " + e.getMessage());
            }
        }
    }


    public String getLiveMatchContent(int matchId) throws IOException {
        String url = BASE_URL + "betoffer/event/" + matchId + ".json?lang=ro_RO&market=RO&ncid=1734917551";
        return executeGetRequest(url);
    }

    public MatchDetail processMatch(String id, String name, String jsonResponse) {
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
            return new MatchDetail(Integer.parseInt(id), name, bets);
        } catch (Exception e) {
            System.err.println("Error parsing live match content for event ID " + id + ": " + e.getMessage());
            return null;
        }
    }
}


