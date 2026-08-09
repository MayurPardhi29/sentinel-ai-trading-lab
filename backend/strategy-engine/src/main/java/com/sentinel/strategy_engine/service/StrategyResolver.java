package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyResolver {

    private final List<TradingStrategy> strategies;

    public StrategyResolver(
            List<TradingStrategy> strategies){
        this.strategies = strategies;
    }

    public TradingStrategy getStrategy(
            String strategyName){
        return strategies
                .stream()
                .filter(s ->
                        s.getStrategyName()
                                .equalsIgnoreCase(strategyName))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Startegy Not Found"
                        ));
    }


}
