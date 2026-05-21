package com.sentinel.strategy_engine.strategy;

import com.sentinel.strategy_engine.dto.CandleData;
import com.sentinel.strategy_engine.dto.StrategyResult;

import java.util.List;

public interface TradingStrategy {

    StrategyResult analyze(List<CandleData> candles);

    String getStrategyName();
}
