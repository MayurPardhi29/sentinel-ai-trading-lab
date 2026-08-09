package com.sentinel.strategy_engine.dto;

public class SentinelConsensus {

    private String ema;
    private String fvg;
    private int agreement;
    private String recommendation;
    private String interval;
    private String entryHint;

    public SentinelConsensus(String ema, String fvg, int agreement, String recommendation,
                              String interval, String entryHint) {
        this.ema = ema;
        this.fvg = fvg;
        this.agreement = agreement;
        this.recommendation = recommendation;
        this.interval = interval;
        this.entryHint = entryHint;
    }

    public String getEma() {
        return ema;
    }

    public String getFvg() {
        return fvg;
    }

    public int getAgreement() {
        return agreement;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getInterval() {
        return interval;
    }

    public String getEntryHint() {
        return entryHint;
    }
}
