package com.sentinel.strategy_engine.dto;

public class BacktestResult {

    private String strategy;

    private int trades;

    private int wins;

    private int losses;

    private double winRate;

    private double profitFactor;

    public BacktestResult(
            String strategy,
            int trades,
            int wins,
            int losses,
            double winRate,
            double profitFactor) {

        this.strategy =
                strategy;

        this.trades =
                trades;

        this.wins =
                wins;

        this.losses =
                losses;

        this.winRate =
                winRate;

        this.profitFactor =
                profitFactor;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getTrades() {
        return trades;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public double getWinRate() {
        return winRate;
    }

    public double getProfitFactor() {
        return profitFactor;
    }
}