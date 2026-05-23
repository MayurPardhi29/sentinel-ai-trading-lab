package com.sentinel.strategy_engine.dto;

public class StrategyRequest {

    private int fast = 9;

    private int slow = 21;

    public StrategyRequest() {
    }

    public StrategyRequest(
            int fast,
            int slow
    ) {

        this.fast = fast;

        this.slow = slow;
    }

    public static StrategyRequest defaults() {

        return new StrategyRequest(
                9,
                21
        );
    }

    public int getFast() {

        if(
                fast <= 0
        ){

            return 9;

        }

        return fast;
    }

    public int getSlow() {

        if(
                slow <= 0
        ){

            return 21;

        }

        return slow;
    }

    public void setFast(
            int fast
    ) {

        this.fast =
                fast;

    }

    public void setSlow(
            int slow
    ) {

        this.slow =
                slow;

    }
}