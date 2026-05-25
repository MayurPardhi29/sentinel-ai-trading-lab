package com.sentinel.strategy_engine.client;

import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class MarketDataClient {

    private final RestTemplate restTemplate;

    public MarketDataClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public HistoricalCandleResponse
    getHistoricalData(
            String symbol,
            String interval
    ) {

        String url =
                "http://market-data:8080"
                        +
                        "/api/stocks/history/"
                        +
                        symbol
                        +
                        "?interval="
                        +
                        interval;

        HistoricalCandleResponse response =

                restTemplate
                        .getForObject(
                                url,
                                HistoricalCandleResponse.class
                        );

        if(response == null ||
                response.getValues() == null ||
                response.getValues().isEmpty() ){
            throw new RuntimeException("no historical data found for "
                    +
                    symbol
            );
        }

        Collections.reverse(
                response.getValues()
        );

        return response;
    }
}
