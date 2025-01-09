package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MatchProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> superbetEventIds = new ArrayList<>();
    private final List<UnibetEvent> unibetEvents = new ArrayList<>();

    public void processSuperbetMatches(String jsonResponse) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode data = rootNode.get("data");
        superbetEventIds.clear();
        for (JsonNode match : data) {
            String eventId = match.get("eventId").asText();
            synchronized (superbetEventIds) {
                superbetEventIds.add(eventId);
            }
        }
    }

    public void processUnibetMatches(String jsonResponse) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode events = rootNode.get("events");
        unibetEvents.clear();
        for (JsonNode eventNode : events) {
            JsonNode event = eventNode.get("event");
            if (event != null) {
                String eventId = event.get("id").asText();
                String eventName = event.get("name").asText();

                synchronized (unibetEvents) {
                    unibetEvents.add(new UnibetEvent(eventId, eventName));
                }
            }
        }
    }

    public List<String> getSuperbetEventIds() {
        return new ArrayList<>(superbetEventIds);
    }

    public List<UnibetEvent> getUnibetEvents() {
        return new ArrayList<>(unibetEvents);
    }

    public static class UnibetEvent {
        private final String id;
        private final String name;

        public UnibetEvent(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "UnibetEvent{id='" + id + "', name='" + name + "'}";
        }
    }
}
