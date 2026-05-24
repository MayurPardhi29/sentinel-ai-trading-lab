package com.sentinel.strategy_engine.dto;

public class ChartCandle {

    private String datetime;

    private double open;

    private double high;

    private double low;

    private double close;

    private double volume;

    private Double emaFast;

    private Double emaSlow;

    private boolean signal;

    private String event;

    private String signalType;

    public ChartCandle(
            String datetime,
            double open,
            double high,
            double low,
            double close,
            double volume,
            Double emaFast,
            Double emaSlow,
            boolean signal,
            String event,
            String signalType
    ) {

        this.datetime = datetime;

        this.open = open;

        this.high = high;

        this.low = low;

        this.close = close;

        this.volume = volume;

        this.emaFast = emaFast;

        this.emaSlow = emaSlow;

        this.signal = signal;

        this.event = event;
    }

    public String getDatetime() {
        return datetime;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public double getVolume() {
        return volume;
    }

    public Double getEmaFast() {
        return emaFast;
    }

    public Double getEmaSlow() {
        return emaSlow;
    }

    public boolean isSignal() {
        return signal;
    }

    public String getEvent(){
        return event;
    }
}