package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.strategy.TradingStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StrategyResolverTest {

    @Test
    void shouldReturnStrategyWhenFound() {

        TradingStrategy strategy =
                mock(TradingStrategy.class);

        when(strategy.getStrategyName())
                .thenReturn("EMA");

        StrategyResolver resolver =
                new StrategyResolver(
                        List.of(strategy)
                );

        TradingStrategy result =
                resolver.getStrategy("EMA");

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenStrategyMissing(){

        StrategyResolver resolver = new StrategyResolver(
                List.of()
        );

        assertThrows(
                RuntimeException.class,
                () -> resolver.getStrategy("ABC")

        );
    }
}