package com.sentinel.market_data_service.provider;

import com.sentinel.market_data_service.dto.HistoricalCandleResponse;

public interface StockDataProvider {

    String fetchQuote(String symbol);

    HistoricalCandleResponse
    fetchHistoricalData(

            String symbol,

            String interval

    );
}
