package com.sentinel.strategy_engine.dto;

public class StrategyRequest {

    private int fast = 9;
    private int slow = 21;
    private String interval = "1day";

    private double stopLossPct = 2.0;      // exit if price drops 2% from entry
    private double takeProfitPct = 4.0;    // exit if price rises 4% from entry (2:1 reward:risk)
    private int maxHoldingPeriod = 10;     // force-close after N candles if neither hits
    private double initialCapital = 10_000.0;

    private int candleCount = 300;

    public StrategyRequest() {
    }

    public StrategyRequest(int fast, int slow) {
        this.fast = fast;
        this.slow = slow;
    }

    public static StrategyRequest defaults() {
        return new StrategyRequest(9, 21);
    }

    public int getFast() {
        if (fast <= 0) {
            return 9;
        }
        return fast;
    }

    public int getSlow() {
        if (slow <= 0) {
            return 21;
        }
        return slow;
    }

    public void setFast(int fast) {
        this.fast = fast;
    }

    public void setSlow(int slow) {
        this.slow = slow;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public double getStopLossPct() {
        return stopLossPct <= 0 ? 2.0 : stopLossPct;
    }

    public void setStopLossPct(double stopLossPct) {
        this.stopLossPct = stopLossPct;
    }

    public double getTakeProfitPct() {
        return takeProfitPct <= 0 ? 4.0 : takeProfitPct;
    }

    public void setTakeProfitPct(double takeProfitPct) {
        this.takeProfitPct = takeProfitPct;
    }

    public int getMaxHoldingPeriod() {
        return maxHoldingPeriod <= 0 ? 10 : maxHoldingPeriod;
    }

    public void setMaxHoldingPeriod(int maxHoldingPeriod) {
        this.maxHoldingPeriod = maxHoldingPeriod;
    }

    public double getInitialCapital() {
        return initialCapital <= 0 ? 10_000.0 : initialCapital;
    }

    public void setInitialCapital(double initialCapital) {
        this.initialCapital = initialCapital;
    }

    public int getCandleCount() {
        if (candleCount <= 0) {
            return 300;
        }
        // Guard against an unreasonably large ask from a client; the provider
        // enforces its own cap too, but fail fast here with a sane ceiling.
        return Math.min(candleCount, 500);
    }

    public void setCandleCount(int candleCount) {
        this.candleCount = candleCount;
    }
}
