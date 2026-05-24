package com.sentinel.strategy_engine.dto;

public class TradeHistory {

    private String date;

    private String signal;

    private String result;

    public TradeHistory(

            String date,

            String signal,

            String result

    ){

        this.date=
                date;

        this.signal=
                signal;

        this.result=
                result;

    }

    public String getDate(){

        return date;

    }

    public String getSignal(){

        return signal;

    }

    public String getResult(){

        return result;

    }

}