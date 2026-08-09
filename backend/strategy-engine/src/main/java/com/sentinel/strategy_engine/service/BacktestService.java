package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.*;
import com.sentinel.strategy_engine.strategy.TradingStrategy;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BacktestService {

    public BacktestResult runBacktest(TradingStrategy strategy, List<CandleData> candles, StrategyRequest request) {

        int lookback = strategy.requiredCandles(request);
        double stopLossPct = request.getStopLossPct() / 100.0;
        double takeProfitPct = request.getTakeProfitPct() / 100.0;
        int maxHoldingPeriod = request.getMaxHoldingPeriod();
        double initialCapital = request.getInitialCapital();

        double capital = initialCapital;
        double peakEquity = capital;
        double maxDrawdownPct = 0;

        int wins = 0;
        int losses = 0;
        double grossProfit = 0;
        double grossLoss = 0;

        List<Double> tradeReturns = new ArrayList<>();
        List<TradeHistory> history = new ArrayList<>();
        List<EquityPoint> equityCurve = new ArrayList<>();

        if (candles.isEmpty() || lookback >= candles.size()) {
            return emptyResult(strategy.getStrategyName(), initialCapital);
        }

        equityCurve.add(new EquityPoint(candles.get(lookback).getDatetime(), round2(capital)));

        int i = lookback;

        while (i < candles.size() - 1) {

            List<CandleData> candleHistory = candles.subList(0, i);
            StrategyResult signal = strategy.analyze(candleHistory, request);

            if (!signal.isDetected()) {
                i++;
                continue;
            }

            CandleData entryCandle = candles.get(i);
            double entryPrice = parse(entryCandle.getClose());
            double stopPrice = entryPrice * (1 - stopLossPct);
            double targetPrice = entryPrice * (1 + takeProfitPct);

            double exitPrice = entryPrice;
            String exitReason = "TIME";
            String exitDate = entryCandle.getDatetime();
            int exitIndex = i;

            int endIndex = Math.min(i + maxHoldingPeriod, candles.size() - 1);

            for (int j = i + 1; j <= endIndex; j++) {
                CandleData bar = candles.get(j);
                double high = parse(bar.getHigh());
                double low = parse(bar.getLow());

                boolean hitStop = low <= stopPrice;
                boolean hitTarget = high >= targetPrice;

                exitDate = bar.getDatetime();
                exitIndex = j;

                if (hitStop) {
                    exitPrice = stopPrice;
                    exitReason = "STOP_LOSS";
                    break;
                }

                if (hitTarget) {
                    exitPrice = targetPrice;
                    exitReason = "TAKE_PROFIT";
                    break;
                }

                exitPrice = parse(bar.getClose());
            }

            double tradeReturnPct = (exitPrice - entryPrice) / entryPrice;
            double pnl = capital * tradeReturnPct;
            capital += pnl;

            tradeReturns.add(tradeReturnPct);

            if (pnl >= 0) {
                wins++;
                grossProfit += pnl;
            } else {
                losses++;
                grossLoss += Math.abs(pnl);
            }

            peakEquity = Math.max(peakEquity, capital);
            if (peakEquity > 0) {
                double drawdown = (peakEquity - capital) / peakEquity * 100.0;
                maxDrawdownPct = Math.max(maxDrawdownPct, drawdown);
            }

            equityCurve.add(new EquityPoint(exitDate, round2(capital)));

            history.add(new TradeHistory(
                    entryCandle.getDatetime(),
                    exitDate,
                    "BUY",
                    pnl >= 0 ? "WIN" : "LOSS",
                    exitReason,
                    round2(entryPrice),
                    round2(exitPrice),
                    round2(tradeReturnPct * 100)
            ));

            i = exitIndex + 1; // don't open a new trade while this one is still "live"
        }

        int trades = wins + losses;
        double winRate = trades == 0 ? 0 : (wins * 100.0) / trades;

        double profitFactor;
        if (grossLoss == 0) {
            profitFactor = grossProfit > 0 ? 99.99 : 0; // capped, avoids serializing Infinity
        } else {
            profitFactor = grossProfit / grossLoss;
        }

        double totalReturnPct = ((capital - initialCapital) / initialCapital) * 100.0;
        double averageWin = wins == 0 ? 0 : grossProfit / wins;
        double averageLoss = losses == 0 ? 0 : grossLoss / losses;
        double winFraction = trades == 0 ? 0 : winRate / 100.0;
        double expectancy = trades == 0 ? 0 : (winFraction * averageWin) - ((1 - winFraction) * averageLoss);

        double sharpeRatio = calculateSharpeRatio(tradeReturns);
        double benchmarkReturnPct = calculateBuyAndHoldReturn(candles, lookback);

        return new BacktestResult(
                strategy.getStrategyName(),
                trades,
                wins,
                losses,
                round2(winRate),
                round2(profitFactor),
                round2(totalReturnPct),
                round2(maxDrawdownPct),
                round2(sharpeRatio),
                round2(averageWin),
                round2(averageLoss),
                round2(expectancy),
                round2(benchmarkReturnPct),
                history,
                equityCurve
        );
    }

    private double calculateSharpeRatio(List<Double> tradeReturns) {
        if (tradeReturns.size() < 2) {
            return 0;
        }

        double mean = tradeReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double variance = tradeReturns.stream()
                .mapToDouble(r -> Math.pow(r - mean, 2))
                .sum() / (tradeReturns.size() - 1);

        double stdDev = Math.sqrt(variance);

        if (stdDev == 0) {
            return 0;
        }
        return (mean / stdDev) * Math.sqrt(252);
    }

    private double calculateBuyAndHoldReturn(List<CandleData> candles, int lookback) {
        if (candles.size() <= lookback + 1) {
            return 0;
        }

        double startPrice = parse(candles.get(lookback).getClose());
        double endPrice = parse(candles.get(candles.size() - 1).getClose());

        return ((endPrice - startPrice) / startPrice) * 100.0;
    }

    private BacktestResult emptyResult(String strategyName, double initialCapital) {
        return new BacktestResult(
                strategyName, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0,
                new ArrayList<>(), new ArrayList<>()
        );
    }

    private double parse(String value) {
        return Double.parseDouble(value);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
