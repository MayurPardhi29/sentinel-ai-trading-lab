package com.sentinel.strategy_engine.dto;

public class SentinelConsensus {

    private String ema;

    private String fvg;

    private int agreement;

    private String recommendation;

    public SentinelConsensus(

            String ema,

            String fvg,

            int agreement,

            String recommendation

    ){

        this.ema=
                ema;

        this.fvg=
                fvg;

        this.agreement=
                agreement;

        this.recommendation=
                recommendation;

    }

    public String getEma() {

        return ema;

    }

    public String getFvg() {

        return fvg;

    }

    public int getAgreement() {

        return agreement;

    }

    public String getRecommendation() {

        return recommendation;

    }

}