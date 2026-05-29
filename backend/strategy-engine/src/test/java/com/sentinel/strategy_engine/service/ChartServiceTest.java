package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChartServiceTest {

    @Test
    void shouldBuildChartCandles() {

        ChartService service =
                new ChartService();

        StrategyRequest request =
                new StrategyRequest();

        request.setFast(9);
        request.setSlow(21);

        CandleData c1 = new CandleData();
        c1.setDatetime("2025-01-01");
        c1.setOpen("100");
        c1.setHigh("105");
        c1.setLow("95");
        c1.setClose("100");
        c1.setVolume("1000");

        CandleData c2 = new CandleData();
        c2.setDatetime("2025-01-02");
        c2.setOpen("110");
        c2.setHigh("115");
        c2.setLow("105");
        c2.setClose("110");
        c2.setVolume("1200");

        List<ChartCandle> result =
                service.build(
                        List.of(c1, c2),
                        request
                );

        assertEquals(
                2,
                result.size()
        );
    }

    @Test
    void shouldPopulateEmaValues() {

        ChartService service =
                new ChartService();

        StrategyRequest request =
                new StrategyRequest();

        request.setFast(9);
        request.setSlow(21);

        CandleData c1 = new CandleData();
        c1.setDatetime("2025-01-01");
        c1.setOpen("100");
        c1.setHigh("105");
        c1.setLow("95");
        c1.setClose("100");
        c1.setVolume("1000");

        CandleData c2 = new CandleData();
        c2.setDatetime("2025-01-02");
        c2.setOpen("110");
        c2.setHigh("115");
        c2.setLow("105");
        c2.setClose("110");
        c2.setVolume("1200");

        List<ChartCandle> result =
                service.build(
                        List.of(c1, c2),
                        request
                );

        assertNotNull(
                result.get(1).getEmaFast()
        );

        assertNotNull(
                result.get(1).getEmaSlow()
        );
    }
}