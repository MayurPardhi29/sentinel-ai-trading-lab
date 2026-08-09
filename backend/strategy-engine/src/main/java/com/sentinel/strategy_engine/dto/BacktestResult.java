package com.sentinel.strategy_engine.dto;

import java.util.List;

public class BacktestResult {

    private String strategy;
    private int trades;
    private int wins;
    private int losses;
    private double winRate;
    private double profitFactor;

    // New: the metrics that make a backtest result actually mean something.
    private double totalReturnPct;
    private double maxDrawdownPct;
    private double sharpeRatio;
    private double averageWin;
    private double averageLoss;
    private double expectancy;
    private double benchmarkReturnPct; // buy-and-hold over the same window, for comparison

    private List<TradeHistory> history;
    private List<EquityPoint> equityCurve;

    public BacktestResult(String strategy, int trades, int wins, int losses,
                           double winRate, double profitFactor,
                           double totalReturnPct, double maxDrawdownPct, double sharpeRatio,
                           double averageWin, double averageLoss, double expectancy,
                           double benchmarkReturnPct,
                           List<TradeHistory> history, List<EquityPoint> equityCurve) {

        this.strategy = strategy;
        this.trades = trades;
        this.wins = wins;
        this.losses = losses;
        this.winRate = winRate;
        this.profitFactor = profitFactor;
        this.totalReturnPct = totalReturnPct;
        this.maxDrawdownPct = maxDrawdownPct;
        this.sharpeRatio = sharpeRatio;
        this.averageWin = averageWin;
        this.averageLoss = averageLoss;
        this.expectancy = expectancy;
        this.benchmarkReturnPct = benchmarkReturnPct;
        this.history = history;
        this.equityCurve = equityCurve;
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

    public double getTotalReturnPct() {
        return totalReturnPct;
    }

    public double getMaxDrawdownPct() {
        return maxDrawdownPct;
    }

    public double getSharpeRatio() {
        return sharpeRatio;
    }

    public double getAverageWin() {
        return averageWin;
    }

    public double getAverageLoss() {
        return averageLoss;
    }

    public double getExpectancy() {
        return expectancy;
    }

    public double getBenchmarkReturnPct() {
        return benchmarkReturnPct;
    }

    public List<TradeHistory> getHistory() {
        return history;
    }

    public List<EquityPoint> getEquityCurve() {
        return equityCurve;
    }
}
