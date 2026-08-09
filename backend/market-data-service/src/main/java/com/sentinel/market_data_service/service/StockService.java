package com.sentinel.market_data_service.service;

import com.sentinel.market_data_service.dto.HistoricalCandleResponse;
import com.sentinel.market_data_service.provider.StockDataProvider;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockDataProvider stockDataProvider;

    public StockService(StockDataProvider stockDataProvider) {
        this.stockDataProvider = stockDataProvider;
    }

    public String getStockQuote(String symbol) {
        return stockDataProvider.fetchQuote(symbol);
    }

    public HistoricalCandleResponse getHistoricalData(

            String symbol,

            String interval

    ){

        return stockDataProvider
                .fetchHistoricalData(

                        symbol,

                        interval

                );

    }
}
