package com.sentinel.strategy_engine.controller;

import com.sentinel.strategy_engine.client.MarketDataClient;
import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import com.sentinel.strategy_engine.dto.StrategyResult;
import com.sentinel.strategy_engine.service.FVGService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StrategyController {

    private final MarketDataClient marketDataClient;

    private final FVGService fvgService;

    public StrategyController(
            MarketDataClient marketDataClient,
            FVGService fvgService) {

        this.marketDataClient = marketDataClient;
        this.fvgService = fvgService;
    }

    @GetMapping("/api/strategy/fvg/{symbol}")
    public StrategyResult detectFVG(
            @PathVariable String symbol) {

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol);

        return fvgService.detectBullishFVG(
                response.getValues()
        );
    }
}