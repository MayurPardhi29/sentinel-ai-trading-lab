package com.sentinel.strategy_engine.strategy;

import com.sentinel.strategy_engine.dto.CandleData;
import com.sentinel.strategy_engine.dto.StrategyResult;
import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FVGStrategy implements TradingStrategy {

    @Override
    public StrategyResult analyze(
            List<CandleData> candles) {

        for (int i = 0; i < candles.size() - 2; i++) {

            CandleData candle1 = candles.get(i);
            CandleData candle3 = candles.get(i + 2);

            double candle1High =
                    Double.parseDouble(candle1.getHigh());

            double candle3Low =
                    Double.parseDouble(candle3.getLow());

            if (candle1High < candle3Low) {

                double gap =
                        candle3Low - candle1High;

                double confidence =
                        Math.min(gap * 10, 95);

                return new StrategyResult(
                        getStrategyName(),
                        true,
                        confidence
                );
            }
        }

        return new StrategyResult(
                getStrategyName(),
                false,
                0
        );
    }

    @Override
    public String getStrategyName() {
        return "fvg";
    }
}