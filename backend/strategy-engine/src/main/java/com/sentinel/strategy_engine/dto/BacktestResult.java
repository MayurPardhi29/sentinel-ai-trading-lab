package com.sentinel.strategy_engine.dto;

import com.sentinel.strategy_engine.strategy.TradingStrategy;

import java.util.List;

public class BacktestResult {

    private String strategy;

    private int trades;

    private int wins;

    private int losses;

    private double winRate;

    private double profitFactor;

    private List<TradeHistory> history;

    public BacktestResult(
            String strategy,
            int trades,
            int wins,
            int losses,
            double winRate,
            double profitFactor,
            List<TradeHistory> history) {

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

        this.history = history;
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

    public List<TradeHistory> getHistory(){
        return history;
    }
}