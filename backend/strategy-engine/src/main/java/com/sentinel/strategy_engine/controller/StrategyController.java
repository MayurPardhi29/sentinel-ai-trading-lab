package com.sentinel.strategy_engine.controller;

import com.sentinel.strategy_engine.client.MarketDataClient;
import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StrategyController {

    private final MarketDataClient marketDataClient;

    public StrategyController(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    @GetMapping("/api/strategy/history/{symbol}")
    public HistoricalCandleResponse getHistoricalData(
            @PathVariable String symbol) {

        return marketDataClient.getHistoricalData(symbol);
    }
}