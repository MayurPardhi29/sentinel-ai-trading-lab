package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.*;
import com.sentinel.strategy_engine.strategy.TradingStrategy;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BacktestService {

    public BacktestResult runBacktest(

            TradingStrategy strategy,

            List<CandleData> candles,

            StrategyRequest request

    ) {

        int wins = 0;

        int losses = 0;

        int trades = 0;

        List<TradeHistory> history =
                new ArrayList<>();

        int lookback =

                strategy
                        .requiredCandles(
                                request
                        );

        for (

                int i = lookback;

                i < candles.size() - 1;

                i++

        ) {

            List<CandleData> candleHistory =

                    candles.subList(
                            0,
                            i
                    );

            StrategyResult signal =

                    strategy
                            .analyze(

                                    candleHistory,

                                    request

                            );

            if (

                    signal
                            .isDetected()

            ) {

                trades++;

                CandleData current =

                        candles.get(
                                i
                        );

                CandleData next =

                        candles.get(
                                i + 1
                        );

                double entry =

                        Double.parseDouble(

                                current
                                        .getClose()

                        );

                double exit =

                        Double.parseDouble(

                                next
                                        .getClose()

                        );

                if (

                        exit
                                >

                                entry

                ) {

                    wins++;

                    history.add(

                            new TradeHistory(

                                    current
                                            .getDatetime(),

                                    "BUY",

                                    "WIN"

                            )

                    );

                }

                else {

                    losses++;

                    history.add(

                            new TradeHistory(

                                    current
                                            .getDatetime(),

                                    "BUY",

                                    "LOSS"

                            )

                    );

                }

            }

        }

        double winRate =

                trades == 0

                        ?

                        0

                        :

                        (

                                wins
                                        *

                                        100.0

                        )

                                /

                                trades;

        double profitFactor =

                losses == 0

                        ?

                        wins

                        :

                        (

                                double

                                )

                                wins

                                /

                                losses;

        return new BacktestResult(

                strategy
                        .getStrategyName(),

                trades,

                wins,

                losses,

                Math.round(
                        winRate
                ),

                Math.round(
                        profitFactor
                                *
                                100
                )

                        /

                        100.0,

                history

        );

    }

}