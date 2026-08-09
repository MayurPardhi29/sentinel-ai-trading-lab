package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.SentinelConsensus;
import com.sentinel.strategy_engine.dto.StrategyResult;
import com.sentinel.strategy_engine.util.IntervalFormatter;
import org.springframework.stereotype.Service;

@Service
public class ConsensusService {

    public SentinelConsensus build(StrategyResult ema, StrategyResult fvg, String interval) {

        String emaSignal = ema.isDetected() ? "BUY" : "WAIT";
        String fvgSignal = fvg.isDetected() ? "BUY" : "WAIT";

        int agreement;
        String recommendation;
        boolean actionable;

        if (ema.isDetected() && fvg.isDetected()) {
            agreement = 100;
            recommendation = "STRONG BUY";
            actionable = true;
        } else if (ema.isDetected() != fvg.isDetected()) {
            agreement = 50;
            recommendation = "WAIT";
            actionable = false;
        } else {
            agreement = 0;
            recommendation = "NO SIGNAL";
            actionable = false;
        }

        String entryHint = IntervalFormatter.entryHint(interval, actionable);

        return new SentinelConsensus(emaSignal, fvgSignal, agreement, recommendation, interval, entryHint);
    }

    public SentinelConsensus build(StrategyResult ema, StrategyResult fvg) {
        return build(ema, fvg, "1day");
    }
}
