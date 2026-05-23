package com.sentinel.strategy_engine.strategy;

import com.sentinel.strategy_engine.dto.CandleData;
import com.sentinel.strategy_engine.dto.StrategyRequest;
import com.sentinel.strategy_engine.dto.StrategyResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FVGStrategy
        implements TradingStrategy {

    @Override
    public StrategyResult analyze(
            List<CandleData> candles,
            StrategyRequest request
    ) {

        if (candles.size() < 3) {

            return new StrategyResult(
                    "fvg",
                    false,
                    0
            );
        }

        CandleData c1 =
                candles.get(
                        candles.size() - 3
                );

        CandleData c3 =
                candles.get(
                        candles.size() - 1
                );

        double high1 =
                Double.parseDouble(
                        c1.getHigh()
                );

        double low3 =
                Double.parseDouble(
                        c3.getLow()
                );

        boolean detected =

                low3 >
                        high1;

        double confidence = 0;

        if (
                detected
        ) {

            confidence =

                    (
                            (
                                    low3 -
                                            high1
                            )

                                    /

                                    high1

                    )

                            *

                            100;

            confidence =
                    Math.min(
                            confidence,
                            95
                    );
        }

        return new StrategyResult(
                "fvg",
                detected,
                confidence
        );
    }

    @Override
    public String getStrategyName() {

        return "fvg";

    }

    @Override
    public int requiredCandles(StrategyRequest request) {

        return 3;

    }
}