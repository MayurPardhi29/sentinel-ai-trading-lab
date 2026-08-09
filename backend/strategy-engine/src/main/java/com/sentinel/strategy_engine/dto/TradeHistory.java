package com.sentinel.strategy_engine.dto;

public class TradeHistory {

    private String date;
    private String signal;
    private String result;

    private String entryDate;
    private String exitDate;
    private String exitReason;  
    private double entryPrice;
    private double exitPrice;
    private double pnlPercent;

    public TradeHistory(String entryDate, String exitDate, String signal, String result,
                         String exitReason, double entryPrice, double exitPrice, double pnlPercent) {
        this.date = entryDate;
        this.entryDate = entryDate;
        this.exitDate = exitDate;
        this.signal = signal;
        this.result = result;
        this.exitReason = exitReason;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.pnlPercent = pnlPercent;
    }

    public String getDate() {
        return date;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getExitDate() {
        return exitDate;
    }

    public String getSignal() {
        return signal;
    }

    public String getResult() {
        return result;
    }

    public String getExitReason() {
        return exitReason;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public double getPnlPercent() {
        return pnlPercent;
    }
}
