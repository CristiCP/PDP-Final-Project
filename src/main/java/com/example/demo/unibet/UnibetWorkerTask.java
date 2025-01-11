package com.example.demo.unibet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UnibetWorkerTask {

    private String eventId;
    private String name;

    public UnibetWorkerTask() {
    }

    @JsonCreator
    public UnibetWorkerTask(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("name") String name) {
        this.eventId = eventId;
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UnibetWorkerTask{" +
                "eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
