package com.sentinel.strategy_engine.dto;

/**
 * A single point on the backtest equity curve — account value at a point in time.
 * Used by the frontend to render performance vs. time (and vs. benchmark).
 */
public class EquityPoint {

    private String date;
    private double equity;

    public EquityPoint(String date, double equity) {
        this.date = date;
        this.equity = equity;
    }

    public String getDate() {
        return date;
    }

    public double getEquity() {
        return equity;
    }
}
