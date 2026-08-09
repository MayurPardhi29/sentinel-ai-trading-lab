package com.sentinel.market_data_service.cache;

import com.sentinel.market_data_service.dto.HistoricalCandleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MarketDataCache {

    private final Map<String, CacheEntry<HistoricalCandleResponse>> store = new ConcurrentHashMap<>();

    @Value("${market-data.cache-ttl-seconds:60}")
    private long ttlSeconds = 60;

    public HistoricalCandleResponse get(String symbol, String interval, int outputSize) {
        CacheEntry<HistoricalCandleResponse> entry = store.get(key(symbol, interval, outputSize));

        if (entry == null || entry.isExpired()) {
            return null;
        }

        return entry.getValue();
    }

    public void put(String symbol, String interval, int outputSize, HistoricalCandleResponse response) {
        store.put(key(symbol, interval, outputSize), new CacheEntry<>(response, ttlSeconds * 1000));
    }

    private String key(String symbol, String interval, int outputSize) {
        return symbol.toUpperCase() + ":" + interval + ":" + outputSize;
    }
}
