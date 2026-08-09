package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.SentinelResult;
import com.sentinel.strategy_engine.dto.StrategyResult;
import com.sentinel.strategy_engine.util.IntervalFormatter;
import org.springframework.stereotype.Service;

@Service
public class SentinelService {

    public SentinelResult score(StrategyResult fvg, StrategyResult ema, String interval) {

        double finalScore = (fvg.getConfidence() * 0.6) + (ema.getConfidence() * 0.4);

        String recommendation;
        boolean actionable;

        if (finalScore >= 75) {
            recommendation = "HIGH CONFIDENCE";
            actionable = true;
        } else if (finalScore >= 55) {
            recommendation = "WATCH";
            actionable = false;
        } else if (finalScore >= 35) {
            recommendation = "LOW CONFIDENCE";
            actionable = false;
        } else {
            recommendation = "NO TRADE";
            actionable = false;
        }

        String entryHint = IntervalFormatter.entryHint(interval, actionable);

        return new SentinelResult(fvg, ema, Math.round(finalScore), recommendation, interval, entryHint);
    }

    public SentinelResult score(StrategyResult fvg, StrategyResult ema) {
        return score(fvg, ema, "1day");
    }
}
