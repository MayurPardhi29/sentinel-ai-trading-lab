package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.CandleData;
import com.sentinel.strategy_engine.dto.StrategyResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FVGService {

    public StrategyResult detectBullishFVG(
            List<CandleData> candles){

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
                        "Bullish FVG",
                        true,
                        confidence
                );
            }
        }

        return new StrategyResult(
                "Bullish FVG",
                false,
                0
        );
    }
}
