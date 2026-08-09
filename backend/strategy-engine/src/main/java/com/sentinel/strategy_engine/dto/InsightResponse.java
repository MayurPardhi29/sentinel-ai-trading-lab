package com.sentinel.strategy_engine.dto;

public class InsightResponse {

    private String summary;

    private String source;

    public InsightResponse(String summary, String source) {
        this.summary = summary;
        this.source = source;
    }

    public String getSummary() {
        return summary;
    }

    public String getSource() {
        return source;
    }
}
