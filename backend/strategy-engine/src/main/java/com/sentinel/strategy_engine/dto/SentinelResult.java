package com.sentinel.strategy_engine.dto;

public class SentinelResult {

    private StrategyResult fvg;
    private StrategyResult ema;
    private double finalScore;
    private String recommendation;

    // Same reasoning as SentinelConsensus - a score/recommendation with no timeframe
    // attached to it doesn't tell you what to actually do with it.
    private String interval;
    private String entryHint;

    public SentinelResult(StrategyResult fvg, StrategyResult ema, double finalScore, String recommendation,
                           String interval, String entryHint) {
        this.fvg = fvg;
        this.ema = ema;
        this.finalScore = finalScore;
        this.recommendation = recommendation;
        this.interval = interval;
        this.entryHint = entryHint;
    }

    public StrategyResult getFvg() {
        return fvg;
    }

    public StrategyResult getEma() {
        return ema;
    }

    public double getFinalScore() {
        return finalScore;
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
