package com.sentinel.strategy_engine.dto;

public class StrategyResult {

    private String pattern;
    private boolean detected;
    private double confidence;

    public StrategyResult() {
    }

    public StrategyResult(
            String pattern,
            boolean detected,
            double confidence) {

        this.pattern = pattern;
        this.detected = detected;
        this.confidence = confidence;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isDetected() {
        return detected;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
