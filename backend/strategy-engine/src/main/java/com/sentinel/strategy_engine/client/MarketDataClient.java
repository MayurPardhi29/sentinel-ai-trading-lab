package com.sentinel.strategy_engine.client;

import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MarketDataClient {

    private final RestTemplate restTemplate;

    public MarketDataClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public HistoricalCandleResponse getHistoricalData(String symbol){

        String url =
                "http://localhost:8080/api/stocks/history/" + symbol;

        return restTemplate.getForObject(url, HistoricalCandleResponse.class);
    }
}
