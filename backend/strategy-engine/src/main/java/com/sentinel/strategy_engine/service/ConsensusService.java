package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.SentinelConsensus;
import com.sentinel.strategy_engine.dto.StrategyResult;
import org.springframework.stereotype.Service;

@Service
public class ConsensusService {

    public SentinelConsensus build(
            StrategyResult ema,
            StrategyResult fvg
    ){
        String emaSignal = ema.isDetected() ? "BUY" : "WAIT";

        String fvgSignal = fvg.isDetected() ? "BUY" : "WAIT";

        int agreement = 0;

        if(emaSignal.equals(fvgSignal)){
            agreement = 100;
        }else{
            agreement = 50;
        }

        String recommendation =
                agreement >= 100 ? "STRONG BUY" : "WAIT";

        return new SentinelConsensus(
                emaSignal,
                fvgSignal,
                agreement,
                recommendation
        );
    }
}
