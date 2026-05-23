package com.sentinel.strategy_engine.strategy;

import com.sentinel.strategy_engine.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EMAStrategy
        implements TradingStrategy {

    @Override
    public StrategyResult analyze(

            List<CandleData> candles,

            StrategyRequest request

    ) {

        int fast =
                request.getFast();

        int slow =
                request.getSlow();

        if (
                candles.size()
                        <
                        slow + 2
        ) {

            return new StrategyResult(
                    "ema",
                    false,
                    0
            );
        }

        double currentFast =

                calculateEMA(
                        candles,
                        fast
                );

        double currentSlow =

                calculateEMA(
                        candles,
                        slow
                );

        boolean bullishTrend =

                currentFast
                        >
                        currentSlow;

        double confidence =

                Math.abs(
                        currentFast
                                -
                                currentSlow
                )

                        *

                        10;

        confidence =
                Math.min(
                        confidence,
                        95
                );

        return new StrategyResult(

                "ema",

                bullishTrend,

                bullishTrend

                        ?

                        confidence

                        :

                        0
        );
    }

    private double calculateEMA(

            List<CandleData> candles,

            int period

    ) {

        double multiplier =

                2.0
                        /

                        (
                                period + 1
                        );

        double ema =

                Double.parseDouble(
                        candles
                                .get(0)
                                .getClose()
                );

        for (

                int i = 1;

                i < candles.size();

                i++

        ) {

            double close =

                    Double.parseDouble(

                            candles
                                    .get(i)
                                    .getClose()

                    );

            ema =

                    (
                            close
                                    -
                                    ema
                    )

                            *

                            multiplier

                            +

                            ema;

        }

        return ema;
    }

    @Override
    public String getStrategyName() {

        return "ema";

    }

    @Override
    public int requiredCandles(StrategyRequest request) {

        return request.getSlow();

    }
}