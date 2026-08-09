package com.sentinel.strategy_engine.controller;

import com.sentinel.strategy_engine.client.MarketDataClient;
import com.sentinel.strategy_engine.dto.*;
import com.sentinel.strategy_engine.service.*;
import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    private final MarketDataClient marketDataClient;
    private final StrategyResolver strategyResolver;
    private final SentinelService sentinelService;
    private final BacktestService backtestService;
    private final ChartService chartService;
    private final ConsensusService consensusService;

    private final InsightService insightService;

    public StrategyController(
            MarketDataClient marketDataClient,
            StrategyResolver strategyResolver,
            SentinelService sentinelService,
            BacktestService backtestService,
            ChartService chartService,
            ConsensusService consensusService,
            InsightService insightService) {

        this.marketDataClient = marketDataClient;
        this.strategyResolver = strategyResolver;
        this.sentinelService = sentinelService;
        this.backtestService = backtestService;
        this.chartService = chartService;
        this.consensusService = consensusService;
        this.insightService = insightService;
    }

    @GetMapping("/{strategy}/{symbol}")
    public StrategyResult analyze(@PathVariable String strategy, @PathVariable String symbol) {

        StrategyRequest defaults = StrategyRequest.defaults();

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, "1day", defaults.getCandleCount());

        TradingStrategy selected = strategyResolver.getStrategy(strategy);

        return selected.analyze(response.getValues(), defaults);
    }

    @GetMapping("/sentinel/{symbol}")
    public SentinelResult sentinel(@PathVariable String symbol) {

        StrategyRequest defaults = StrategyRequest.defaults();

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, "1day", defaults.getCandleCount());

        StrategyResult fvg = strategyResolver.getStrategy("fvg").analyze(response.getValues(), defaults);
        StrategyResult ema = strategyResolver.getStrategy("ema").analyze(response.getValues(), defaults);

        return sentinelService.score(fvg, ema, "1day");
    }

    @PostMapping("/backtest/{strategy}/{symbol}")
    public BacktestResult backtest(@PathVariable String strategy, @PathVariable String symbol,
                                   @RequestBody StrategyRequest request) {

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, request.getInterval(), request.getCandleCount());

        return backtestService.runBacktest(strategyResolver.getStrategy(strategy), response.getValues(), request);
    }

    @PostMapping("/playground/{strategy}/{symbol}")
    public StrategyResult playground(@PathVariable String strategy, @PathVariable String symbol,
                                     @RequestBody StrategyRequest request) {

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, request.getInterval(), request.getCandleCount());

        return strategyResolver.getStrategy(strategy).analyze(response.getValues(), request);
    }

    @GetMapping("/candles/{symbol}")
    public HistoricalCandleResponse candles(@PathVariable String symbol) {

        StrategyRequest defaults = StrategyRequest.defaults();

        return marketDataClient.getHistoricalData(symbol, "1day", defaults.getCandleCount());
    }

    @PostMapping("/chart/{symbol}")
    public List<ChartCandle> chart(@PathVariable String symbol, @RequestBody StrategyRequest request) {

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, request.getInterval(), request.getCandleCount());

        return chartService.build(response.getValues(), request);
    }

    @PostMapping("/consensus/{symbol}")
    public SentinelConsensus consensus(@PathVariable String symbol, @RequestBody StrategyRequest request) {

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, request.getInterval(), request.getCandleCount());

        StrategyResult ema = strategyResolver.getStrategy("ema").analyze(response.getValues(), request);
        StrategyResult fvg = strategyResolver.getStrategy("fvg").analyze(response.getValues(), request);

        return consensusService.build(ema, fvg, request.getInterval());
    }

    @PostMapping("/insight/{strategy}/{symbol}")
    public InsightResponse insight(@PathVariable String strategy, @PathVariable String symbol,
                                   @RequestBody StrategyRequest request) {

        HistoricalCandleResponse response =
                marketDataClient.getHistoricalData(symbol, request.getInterval(), request.getCandleCount());

        BacktestResult backtestResult =
                backtestService.runBacktest(strategyResolver.getStrategy(strategy), response.getValues(), request);

        StrategyResult ema = strategyResolver.getStrategy("ema").analyze(response.getValues(), request);
        StrategyResult fvg = strategyResolver.getStrategy("fvg").analyze(response.getValues(), request);
        SentinelConsensus consensus = consensusService.build(ema, fvg, request.getInterval());

        return insightService.generate(symbol, backtestResult, consensus);
    }
}
