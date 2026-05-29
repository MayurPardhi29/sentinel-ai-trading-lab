package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.*;
import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BacktestServiceTest {

    @Test
    void shouldReturnZeroTradesWhenNoSignalsDetected() {

        TradingStrategy strategy =
                mock(TradingStrategy.class);

        when(strategy.requiredCandles(any()))
                .thenReturn(1);

        when(strategy.analyze(any(), any()))
                .thenReturn(
                        new StrategyResult(
                                "EMA",
                                false,
                                0
                        )
                );

        CandleData c1 = candle("100");
        CandleData c2 = candle("110");
        CandleData c3 = candle("120");

        BacktestService service =
                new BacktestService();

        BacktestResult result =
                service.runBacktest(
                        strategy,
                        List.of(c1, c2, c3),
                        new StrategyRequest()
                );

        assertEquals(0, result.getTrades());
        assertEquals(0, result.getWins());
        assertEquals(0, result.getLosses());
    }

    @Test
    void shouldCreateWinningTrade() {

        TradingStrategy strategy =
                mock(TradingStrategy.class);

        when(strategy.requiredCandles(any()))
                .thenReturn(1);

        when(strategy.analyze(any(), any()))
                .thenReturn(
                        new StrategyResult(
                                "EMA",
                                true,
                                90
                        )
                );

        CandleData c1 = candle("100");
        CandleData c2 = candle("110");
        CandleData c3 = candle("120");

        BacktestService service =
                new BacktestService();

        BacktestResult result =
                service.runBacktest(
                        strategy,
                        List.of(c1, c2, c3),
                        new StrategyRequest()
                );

        assertTrue(result.getTrades() > 0);
        assertTrue(result.getWins() > 0);
    }

    private CandleData candle(String close) {

        CandleData candle =
                new CandleData();

        candle.setDatetime("2025-01-01");
        candle.setOpen(close);
        candle.setHigh(close);
        candle.setLow(close);
        candle.setClose(close);
        candle.setVolume("1000");

        return candle;
    }
}
