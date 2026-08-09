package com.sentinel.strategy_engine.ai;

import com.sentinel.strategy_engine.dto.BacktestResult;
import com.sentinel.strategy_engine.dto.SentinelConsensus;

public interface InsightGenerator {

    String generate(String symbol, BacktestResult backtest, SentinelConsensus consensus);
}
