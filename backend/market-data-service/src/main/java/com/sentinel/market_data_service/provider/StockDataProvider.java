package com.sentinel.market_data_service.provider;

public interface StockDataProvider {

    String fetchQuote(String symbol);
}
