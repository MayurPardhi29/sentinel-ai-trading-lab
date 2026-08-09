package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.SentinelResult;
import com.sentinel.strategy_engine.dto.StrategyResult;
import org.springframework.stereotype.Service;

@Service
public class SentinelService {

    public SentinelResult score(
            StrategyResult fvg,
            StrategyResult ema){

        double finalScore = (fvg.getConfidence() * 0.6) +
                (fvg.getConfidence() * 0.4);

        String recommendation;

        if(finalScore >= 75)
            recommendation =
                    "HIGH CONFIDENCE";

        else if(finalScore >= 55)
            recommendation =
                    "WATCH";

        else if(finalScore >= 35)
            recommendation =
                    "LOW CONFIDENCE";

        else
            recommendation =
                    "NO TRADE";

        return new SentinelResult(
                fvg,
                ema,
                Math.round(finalScore),
                recommendation
        );
    }
}
