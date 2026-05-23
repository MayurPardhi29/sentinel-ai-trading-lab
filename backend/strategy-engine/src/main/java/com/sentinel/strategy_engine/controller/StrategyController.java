package com.sentinel.strategy_engine.controller;

import com.sentinel.strategy_engine.client.MarketDataClient;
import com.sentinel.strategy_engine.dto.*;
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
                response.getValues(),
                StrategyRequest
                        .defaults()
        );
    }

    @GetMapping("/sentinel/{symbol}")
    public SentinelResult sentinel(@PathVariable String symbol){

        HistoricalCandleResponse response =
                marketDataClient
                        .getHistoricalData(symbol);

        StrategyResult fvg = strategyResolver
                .getStrategy("fvg")
                .analyze(response.getValues(),

                        StrategyRequest
                                .defaults());

        StrategyResult ema = strategyResolver
                .getStrategy("ema")
                .analyze(response.getValues(),

                        StrategyRequest
                                .defaults());

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
                        response.getValues(),

                StrategyRequest
                        .defaults());
    }

    @PostMapping(
            "/playground/{strategy}/{symbol}"
    )

    public StrategyResult playground(

            @PathVariable
            String strategy,

            @PathVariable
            String symbol,

            @RequestBody
            StrategyRequest request

    ){

        HistoricalCandleResponse response=

                marketDataClient
                        .getHistoricalData(
                                symbol
                        );

        return strategyResolver
                .getStrategy(
                        strategy
                )

                .analyze(

                        response
                                .getValues(),

                        request

                );

    }
}