package com.sentinel.strategy_engine.controller;

import com.sentinel.strategy_engine.client.MarketDataClient;
import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import com.sentinel.strategy_engine.dto.StrategyResult;
import com.sentinel.strategy_engine.service.StrategyResolver;
import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    private final MarketDataClient marketDataClient;

    private final StrategyResolver strategyResolver;

    public StrategyController(
            MarketDataClient marketDataClient,
            StrategyResolver strategyResolver) {

        this.marketDataClient =
                marketDataClient;

        this.strategyResolver =
                strategyResolver;
    }

    @GetMapping("/{strategy}/{symbol}")
    public StrategyResult analyze(
            @PathVariable String strategy,

            @PathVariable String symbol) {

        HistoricalCandleResponse response =
                marketDataClient
                        .getHistoricalData(symbol);

        TradingStrategy selected =

                strategyResolver
                        .getStrategy(strategy);

        return selected.analyze(
                response.getValues()
        );
    }
}