package com.sentinel.strategy_engine.controller;

import com.sentinel.strategy_engine.client.MarketDataClient;
import com.sentinel.strategy_engine.dto.BacktestResult;
import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import com.sentinel.strategy_engine.dto.SentinelResult;
import com.sentinel.strategy_engine.dto.StrategyResult;
import com.sentinel.strategy_engine.service.BacktestService;
import com.sentinel.strategy_engine.service.SentinelService;
import com.sentinel.strategy_engine.service.StrategyResolver;
import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    private final MarketDataClient marketDataClient;

    private final StrategyResolver strategyResolver;

    private final SentinelService sentinelService;

    private final BacktestService backtestService;

    public StrategyController(
            MarketDataClient marketDataClient,
            StrategyResolver strategyResolver,
            SentinelService sentinelService,
            BacktestService backtestService) {

        this.marketDataClient = marketDataClient;

        this.strategyResolver = strategyResolver;

        this.sentinelService = sentinelService;

        this.backtestService = backtestService;
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

    @GetMapping("/sentinel/{symbol}")
    public SentinelResult sentinel(@PathVariable String symbol){

        HistoricalCandleResponse response =
                marketDataClient
                        .getHistoricalData(symbol);

        StrategyResult fvg = strategyResolver
                .getStrategy("fvg")
                .analyze(response.getValues());

        StrategyResult ema = strategyResolver
                .getStrategy("ema")
                .analyze(response.getValues());

        return sentinelService
                .score(
                        fvg,
                        ema
                );
    }

    @GetMapping("/backtest/{strategy}/{symbol}")
    public BacktestResult backtest(@PathVariable String strategy,
            @PathVariable String symbol) {

        HistoricalCandleResponse response = marketDataClient
                        .getHistoricalData(symbol);

        return backtestService.runBacktest(
                        strategyResolver.getStrategy(strategy),
                        response.getValues());
    }
}