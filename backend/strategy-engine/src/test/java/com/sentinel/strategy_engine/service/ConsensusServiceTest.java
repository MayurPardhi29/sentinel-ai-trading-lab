package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.SentinelConsensus;
import com.sentinel.strategy_engine.dto.StrategyResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsensusServiceTest {

    @Test
    void shouldReturnStrongBuyWhenBothSignalsDetected() {

        ConsensusService service =
                new ConsensusService();

        StrategyResult ema =
                new StrategyResult(
                        "EMA",
                        true,
                        90
                );

        StrategyResult fvg =
                new StrategyResult(
                        "FVG",
                        true,
                        80
                );

        SentinelConsensus result =
                service.build(
                        ema,
                        fvg
                );

        assertEquals(
                "STRONG BUY",
                result.getRecommendation()
        );

        assertEquals(
                100,
                result.getAgreement()
        );
    }

    @Test
    void shouldReturnWaitWhenSignalsDoNotMatch() {

        ConsensusService service =
                new ConsensusService();

        StrategyResult ema =
                new StrategyResult(
                        "EMA",
                        true,
                        90
                );

        StrategyResult fvg =
                new StrategyResult(
                        "FVG",
                        false,
                        10
                );

        SentinelConsensus result =
                service.build(
                        ema,
                        fvg
                );

        assertEquals(
                "WAIT",
                result.getRecommendation()
        );

        assertEquals(
                50,
                result.getAgreement()
        );
    }
}