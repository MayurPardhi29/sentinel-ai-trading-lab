package com.sentinel.market_data_service.service;

import com.sentinel.market_data_service.client.FinnhubClient;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final FinnhubClient finnhubClient;

    public StockService(FinnhubClient finnhubClient){
        this.finnhubClient = finnhubClient;
    }

    public String getStockQuote(String symbol){
        return finnhubClient.fetchStockQuote(symbol);
    }
}
