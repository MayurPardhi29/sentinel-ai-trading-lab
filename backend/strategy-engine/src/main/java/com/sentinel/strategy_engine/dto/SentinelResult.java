package com.sentinel.strategy_engine.dto;

public class SentinelResult {

    private StrategyResult fvg;

    private StrategyResult ema;

    private double finalScore;

    private String recommendation;

    public SentinelResult(
            StrategyResult fvg,
            StrategyResult ema,
            double finalScore,
            String recommendation) {

        this.fvg = fvg;
        this.ema = ema;
        this.finalScore = finalScore;
        this.recommendation = recommendation;
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
}