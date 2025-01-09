package com.example.demo;

import java.util.List;

public class MatchDetail {
    private int id;
    private String matchName;
    private List<String> bets;

    public MatchDetail(int id, String matchName, List<String> bets) {
        this.id = id;
        this.matchName = matchName;
        this.bets = bets;
    }

    public int getId() {
        return id;
    }

    public String getMatchName() {
        return matchName;
    }

    public List<String> getBets() {
        return bets;
    }

    @Override
    public String toString() {
        return "MatchDetail{" +
                "id=" + id +
                ", matchName='" + matchName + '\'' +
                ", bets=" + bets +
                '}';
    }
}