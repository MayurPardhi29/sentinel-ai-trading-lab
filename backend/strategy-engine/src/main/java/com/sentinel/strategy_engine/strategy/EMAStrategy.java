package com.sentinel.strategy_engine.strategy;

import com.sentinel.strategy_engine.dto.CandleData;
import com.sentinel.strategy_engine.dto.StrategyResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EMAStrategy
        implements TradingStrategy {

    @Override
    public StrategyResult analyze(
            List<CandleData> candles
    ) {

        if (
                candles.size() < 21
        ) {

            return new StrategyResult(
                    "ema",
                    false,
                    0
            );
        }

        double ema9 =
                calculateEMA(
                        candles,
                        9
                );

        double ema21 =
                calculateEMA(
                        candles,
                        21
                );

        boolean bullish =

                ema9 >
                        ema21;

        double confidence =

                Math.abs(
                        ema9 -
                                ema21
                )

                        *

                        5;

        confidence =
                Math.min(
                        confidence,
                        95
                );

        return new StrategyResult(

                "ema",

                bullish,

                bullish

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

                2.0 /

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
    public int requiredCandles() {

        return 21;

    }
}