package com.sentinel.market_data_service.service;

import com.sentinel.market_data_service.cache.MarketDataCache;
import com.sentinel.market_data_service.dto.HistoricalCandleResponse;
import com.sentinel.market_data_service.provider.StockDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockDataProvider stockDataProvider;
    private final MarketDataCache cache;

    @Value("${twelvedata.default-outputsize:300}")
    private int defaultOutputSize = 300;

    public StockService(StockDataProvider stockDataProvider, MarketDataCache cache) {
        this.stockDataProvider = stockDataProvider;
        this.cache = cache;
    }

    public String getStockQuote(String symbol) {
        return stockDataProvider.fetchQuote(symbol);
    }

    public HistoricalCandleResponse getHistoricalData(String symbol, String interval) {
        return getHistoricalData(symbol, interval, defaultOutputSize);
    }

    public HistoricalCandleResponse getHistoricalData(String symbol, String interval, int outputSize) {

        int size = outputSize <= 0 ? defaultOutputSize : outputSize;

        HistoricalCandleResponse cached = cache.get(symbol, interval, size);
        if (cached != null) {
            return cached;
        }

        HistoricalCandleResponse response = stockDataProvider.fetchHistoricalData(symbol, interval, size);

        if (response != null && response.getValues() != null && !response.getValues().isEmpty()) {
            cache.put(symbol, interval, size, response);
        }

        return response;
    }
}
